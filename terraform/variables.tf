variable "project_id" {
  description = "The ID of the GCP project"
  type        = string
}

variable "region" {
  description = "The region to deploy resources in"
  type        = string
  default     = "us-central1"
}

variable "zone" {
  description = "The zone to deploy the VM in"
  type        = string
  default     = "us-central1-a"
}

variable "machine_type" {
  description = "The machine type for the Compute Engine VM"
  type        = string
  default     = "e2-small" # e2-micro is free tier but e2-small is better for full stack containers
}
