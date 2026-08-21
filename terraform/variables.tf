variable "project_id" {
  description = "GCP project ID"
  type        = string
}

variable "region" {
  description = "GCP region"
  type        = string
  default     = "asia-south1"
}

variable "zone" {
  description = "GCP zone"
  type        = string
  default     = "asia-south1-a"
}

variable "env_secret_name" {
  description = "The name of the Secret in GCP Secret Manager that contains the entire .env file"
  type        = string
}

variable "db_username" {
  description = "The initial Postgres database user to create in Cloud SQL"
  type        = string
}

variable "db_password" {
  description = "The initial Postgres database password to create in Cloud SQL"
  type        = string
  sensitive   = true
}