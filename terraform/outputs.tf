output "project_id" {
  value = var.project_id
}

output "region" {
  value = var.region
}

output "zone" {
  value = var.zone
}

output "vm_name" {
  value = google_compute_instance.job_notifier.name
}

output "vm_external_ip" {
  value = google_compute_instance.job_notifier.network_interface[0].access_config[0].nat_ip
}

output "vm_internal_ip" {
  value = google_compute_instance.job_notifier.network_interface[0].network_ip
}