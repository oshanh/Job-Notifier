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
    ports    = ["80", "8080"]
  }

  source_ranges = ["0.0.0.0/0"]

  target_tags = ["job-notifier"]
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
      # Ephemeral public IP
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
      git clone https://github.com/oshanh/Job-Notifier.git /opt/job-notifier
    else
      cd /opt/job-notifier
      git reset --hard
      git pull origin main
    fi

    cd /opt/job-notifier

    # 4. Fetch the entire configuration .env file from Secret Manager
    gcloud secrets versions access latest --secret="${var.env_secret_name}" > .env
    
    # 5. Populate backend .env.docker for completeness 
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