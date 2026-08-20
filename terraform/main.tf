provider "google" {
  project = var.project_id
  region  = var.region
  zone    = var.zone
}

# Reserve a static external IP address
resource "google_compute_address" "static_ip" {
  name   = "job-notifier-static-ip"
  region = var.region
}

# Firewall rule to allow HTTP/HTTPS and SSH traffic
resource "google_compute_firewall" "allow_web" {
  name    = "allow-web-traffic"
  network = "default"

  allow {
    protocol = "tcp"
    ports    = ["22", "80", "443", "8080"]
  }

  source_ranges = ["0.0.0.0/0"]
  target_tags   = ["web-server"]
}

# The Compute Engine VM Instance
resource "google_compute_instance" "app_server" {
  name         = "job-notifier-instance"
  machine_type = var.machine_type
  zone         = var.zone

  tags = ["web-server"]

  boot_disk {
    initialize_params {
      image = "ubuntu-os-cloud/ubuntu-2204-lts"
      size  = 30
      type  = "pd-standard"
    }
  }

  network_interface {
    network = "default"
    access_config {
      # Attach the reserved static IP to the VM
      nat_ip = google_compute_address.static_ip.address
    }
  }

  # Startup script to install Docker, Docker Compose
  metadata_startup_script = file("${path.module}/startup-script.sh")

  service_account {
    # It requires necessary IAM roles on the project level if pulling images from Artifact Registry 
    # Use default compute service account for simplicity 
    scopes = ["https://www.googleapis.com/auth/cloud-platform"]
  }
}
