output "instance_name" {
  description = "The name of the VM"
  value       = google_compute_instance.app_server.name
}

output "instance_public_ip" {
  description = "The public IP address of the VM"
  value       = google_compute_address.static_ip.address
}
