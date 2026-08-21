terraform {
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 7.0"
    }
  }

  required_version = ">= 1.15.0"
}

provider "google" {
  project = var.project_id
  region  = var.region
  zone    = var.zone
}

# -------------------------
# VPC
# -------------------------

resource "google_compute_network" "job_notifier_vpc" {
  name                    = "job-notifier-vpc"
  auto_create_subnetworks = false
}

# -------------------------
# Subnet
# -------------------------

resource "google_compute_subnetwork" "job_notifier_subnet" {
  name          = "job-notifier-subnet"
  ip_cidr_range = "10.10.0.0/24"
  region        = var.region
  network       = google_compute_network.job_notifier_vpc.id
}

# -------------------------
# Firewall - SSH
# -------------------------

resource "google_compute_firewall" "allow_ssh" {
  name    = "job-notifier-allow-ssh"
  network = google_compute_network.job_notifier_vpc.name

  allow {
    protocol = "tcp"
    ports    = ["22"]
  }

  source_ranges = ["0.0.0.0/0"]

  target_tags = ["job-notifier"]
}

# -------------------------
# Firewall - Spring Boot
# -------------------------

resource "google_compute_firewall" "allow_http" {
  name    = "job-notifier-allow-http"
  network = google_compute_network.job_notifier_vpc.name

  allow {
    protocol = "tcp"
    ports    = ["80", "443"]
  }

  source_ranges = ["0.0.0.0/0"]

  target_tags = ["job-notifier"]
}

# -------------------------
# Static IP Address
# -------------------------

resource "google_compute_address" "job_notifier_ip" {
  name   = "job-notifier-static-ip"
  region = var.region
}

# -------------------------
# Compute Engine VM
# -------------------------

resource "google_compute_instance" "job_notifier" {
  name         = "job-notifier-vm"
  machine_type = "e2-small"
  zone         = var.zone

  tags = ["job-notifier"]

  boot_disk {
    initialize_params {
      image = "ubuntu-os-cloud/ubuntu-2404-lts-amd64"
      size  = 20
      type  = "pd-balanced"
    }
  }

  network_interface {
    subnetwork = google_compute_subnetwork.job_notifier_subnet.id

    access_config {
      # Use the statically allocated IP
      nat_ip = google_compute_address.job_notifier_ip.address
    }
  }

  metadata_startup_script = replace(<<-EOF
    #!/bin/bash
    set -e

    echo "Starting full automation setup..."

    # 1. Install Dependencies
    apt-get update
    apt-get install -y ca-certificates curl git

    # 2. Install Docker
    if ! command -v docker &> /dev/null; then
      curl -fsSL https://get.docker.com | sh
    fi

    # 3. Pull Repository
    if [ ! -d "/opt/job-notifier" ]; then
      mkdir -p /opt/job-notifier
      git clone -b dev-gamini https://github.com/oshanh/Job-Notifier.git /opt/job-notifier
    else
      cd /opt/job-notifier
      git reset --hard
      git pull origin dev-gamini
    fi

    cd /opt/job-notifier

    # 4. Fetch the entire configuration .env file from Secret Manager
    gcloud secrets versions access latest --secret="${var.env_secret_name}" > .env
    
    # Inject the Cloud SQL connection string dynamically into the .env file
    echo "" >> .env
    echo "DB_INSTANCE_CONNECTION_NAME=${var.project_id}:${var.region}:job-notifier-db-instance" >> .env
    
    # 5. Provision Let's Encrypt SSL Certificates with Certbot
    apt-get install -y certbot
    if [ ! -d "/etc/letsencrypt/live/jobnotifier.tech" ]; then
      echo "No SSL certificate found. Provisioning fresh Let's Encrypt certificate..."
      # Stop docker temporarily if it's already running to free up Port 80
      systemctl stop docker || true
      certbot certonly --standalone -d jobnotifier.tech -d www.jobnotifier.tech --non-interactive --agree-tos -m admin@jobnotifier.tech || true
      systemctl start docker || true
    fi

    # 6. Populate backend .env.docker for completeness 
    mkdir -p backend-job-notifier
    cp .env backend-job-notifier/.env.docker

    # 6. Start the environment natively using modern docker compose plugin
    docker compose -f docker-compose.prod.yml up -d --build
  EOF
  , "\r", "")

  service_account {
    # It requires the cloud-platform scope to fetch from GCP Secret Manager
    scopes = ["https://www.googleapis.com/auth/cloud-platform"]
  }
}

# -------------------------
# Dynamic IAM Permissions
# -------------------------

data "google_compute_default_service_account" "default" {}

resource "google_secret_manager_secret_iam_member" "secret_accessor_binding" {
  project   = var.project_id
  secret_id = var.env_secret_name
  role      = "roles/secretmanager.secretAccessor"
  member    = "serviceAccount:${data.google_compute_default_service_account.default.email}"
}

resource "google_project_iam_member" "cloudsql_client_binding" {
  project = var.project_id
  role    = "roles/cloudsql.client"
  member  = "serviceAccount:${data.google_compute_default_service_account.default.email}"
}

# -------------------------
# Google Cloud SQL
# -------------------------

resource "google_sql_database_instance" "postgres" {
  name             = "job-notifier-db-instance"
  database_version = "POSTGRES_15"
  region           = var.region

  settings {
    tier = "db-f1-micro"
    backup_configuration {
      enabled                        = true
      start_time                     = "01:00"
      point_in_time_recovery_enabled = true
    }
  }

  deletion_protection = true
}

resource "google_sql_database" "database" {
  name     = "job-notifier"
  instance = google_sql_database_instance.postgres.name
}

resource "google_sql_user" "users" {
  name     = var.db_username
  instance = google_sql_database_instance.postgres.name
  password = var.db_password
}

# -------------------------
# CI/CD Github Actions
# -------------------------

resource "google_service_account" "github_actions" {
  account_id   = "github-actions-sa"
  display_name = "Github Actions CI/CD Deployment Account"
}

resource "google_project_iam_member" "github_actions_compute_admin" {
  project = var.project_id
  role    = "roles/compute.instanceAdmin.v1"
  member  = "serviceAccount:${google_service_account.github_actions.email}"
}

resource "google_project_iam_member" "github_actions_service_account_user" {
  project = var.project_id
  role    = "roles/iam.serviceAccountUser"
  member  = "serviceAccount:${google_service_account.github_actions.email}"
}

# -------------------------
# Workload Identity Federation (Keyless Auth)
# -------------------------

resource "google_iam_workload_identity_pool" "github_pool" {
  workload_identity_pool_id = "github-actions-pool"
  display_name              = "GitHub Actions Pool"
  description               = "Identity pool for GitHub Actions deployments"
}

resource "google_iam_workload_identity_pool_provider" "github_provider" {
  workload_identity_pool_id          = google_iam_workload_identity_pool.github_pool.workload_identity_pool_id
  workload_identity_pool_provider_id = "github-actions-provider"
  display_name                       = "GitHub Actions Provider"

  attribute_mapping = {
    "google.subject"       = "assertion.sub"
    "attribute.repository" = "assertion.repository"
  }

  attribute_condition = "attribute.repository == \"oshanh/Job-Notifier\""

  oidc {
    issuer_uri = "https://token.actions.githubusercontent.com"
  }
}

resource "google_service_account_iam_member" "github_actions_oidc" {
  service_account_id = google_service_account.github_actions.name
  role               = "roles/iam.workloadIdentityUser"
  member             = "principalSet://iam.googleapis.com/${google_iam_workload_identity_pool.github_pool.name}/attribute.repository/oshanh/Job-Notifier"
}

resource "google_project_service" "iamcredentials" {
  project = var.project_id
  service = "iamcredentials.googleapis.com"
  disable_on_destroy = false
}