#!/bin/bash
# Exit on error
set -e

echo "Starting setup..."

# 1. Update packages and install prerequisites
apt-get update -y
apt-get install -y apt-transport-https ca-certificates curl software-properties-common git

# 2. Install Docker
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | apt-key add -
add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" -y
apt-get update -y
apt-get install -y docker-ce docker-ce-cli containerd.io

# Enable and start Docker
systemctl enable docker
systemctl start docker

# Add default user (ubuntu) to docker group
usermod -aG docker ubuntu

# 3. Install Docker Compose
curl -L "https://github.com/docker/compose/releases/download/v2.24.5/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose

# 4. Create App Directory
mkdir -p /opt/job-notifier
cd /opt/job-notifier

# 5. The application code deployment
# Note: For security in a real setup, we would clone a private Git repo via Deploy Keys
# or pull Docker images from a registry (GCR/Artifact Registry).
# Assuming the user will manually upload files via SCP or git clone, we initialize the directory.
# Example: 
# git clone https://github.com/oshanh/Job-Notifier.git . 
# docker-compose -f docker-compose.prod.yml up -d --build

echo "Docker and Docker Compose are successfully installed."
echo "Please configure your environment variables and start production docker-compose."
