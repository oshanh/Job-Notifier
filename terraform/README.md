# Job-Notifier GCP Terraform Deployment

This directory contains the Terraform configuration to deploy the entire Job-Notifier application onto Google Cloud Platform. 
It uses a highly portable, cost-effective **Single VM Docker Compose** architecture. This avoids vendor lock-in, meaning when you want to migrate to AWS later, you can reuse the exact same `docker-compose.prod.yml` and startup configuration on an EC2 instance.

## Setup

1. **Install Terraform**: Provide Terraform on your local machine if not already installed.
2. **Setup GCP CLI (`gcloud`)**: Configure your local GCP environment.
   ```bash
   gcloud auth login
   gcloud auth application-default login
   ```
3. Ensure the project is active:
   ```bash
   gcloud config set project [YOUR_PROJECT_ID]
   ```
4. Enable the necessary APIs manually or ensure Compute Engine API is enabled for your project:
   ```bash
   gcloud services enable compute.googleapis.com
   ```

## Deploying

1. `cd terraform/`
2. `terraform init` to download Google providers.
3. `terraform plan -var="project_id=[YOUR_PROJECT_ID]"` to see what will be generated.
4. `terraform apply -var="project_id=[YOUR_PROJECT_ID]"` to deploy. 

Terraform will spit out the `instance_public_ip`. Once it does, the VM will internally run the `startup-script.sh` which installs Docker. 

## Application Startup

In a real world continuous-integration scenario, you'd pull predefined application images from an artifact registry inside the `startup-script.sh`. 

Since this is a personal application, you can simply push the repository files over `scp`/`sftp` or clone the repository to the newly created VM:

```bash
# Connect to the instance
gcloud compute ssh ubuntu@job-notifier-instance

# Switch to the setup directory
cd /opt/job-notifier

# Clone your repo (you need to provide your Git credentials or deploy keys)
git clone https://github.com/oshanh/Job-Notifier.git .

# Start the environment
docker-compose -f docker-compose.prod.yml up -d --build
```

You can then access your web application on `http://[INSTANCE_PUBLIC_IP]`.
