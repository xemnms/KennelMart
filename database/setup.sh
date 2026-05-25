#!/bin/bash

# ============================================================================
# KennelMart Database Setup Script (Docker Edition)
# ============================================================================
# This script sets up PostgreSQL in Docker for KennelMart
# Prerequisites: Docker installed and running
# Run: bash database/setup.sh

set -e  # Exit on error

echo "==========================================="
echo "KennelMart PostgreSQL Setup (Docker)"
echo "==========================================="

# Check if Docker is available
if ! command -v docker &> /dev/null; then
    echo "✗ Docker is not installed"
    echo "Please install Docker: https://docs.docker.com/get-docker/"
    exit 1
fi
echo "✓ Docker is available"

echo ""
echo "✓ Setting up PostgreSQL container..."

# Stop and remove existing container
if docker ps -a --format '{{.Names}}' | grep -q "^kennelmart_postgres$"; then
    echo "✓ Removing existing container..."
    docker stop kennelmart_postgres 2>/dev/null || true
    sleep 1
    docker rm kennelmart_postgres 2>/dev/null || true
    sleep 2
fi

echo "✓ Creating new PostgreSQL container (port 5433)..."
docker run -d \
    --name kennelmart_postgres \
    -e POSTGRES_USER=postgres \
    -e POSTGRES_PASSWORD=postgres \
    -e POSTGRES_DB=kennelmart_db \
    -p 5433:5432 \
    postgres:15-alpine

# Wait for PostgreSQL to be ready
echo ""
echo "✓ Waiting for PostgreSQL to be ready..."
until docker exec kennelmart_postgres psql -U postgres -d postgres -c "SELECT 1" > /dev/null 2>&1; do
    echo "  ..."
    sleep 2
done
echo "✓ PostgreSQL is running"

# Create database
echo ""
echo "✓ Creating database: kennelmart_db..."
docker exec kennelmart_postgres psql -U postgres -d postgres -c "CREATE DATABASE kennelmart_db;" 2>/dev/null || echo "  (Database may already exist)"
echo "✓ Database created"

# Load schema
echo ""
echo "✓ Loading database schema..."
docker exec -i kennelmart_postgres psql -U postgres -d kennelmart_db < database/kennelmart_schema.sql
echo "✓ Schema loaded"

echo ""
echo "==========================================="
echo "✓ Database setup complete!"
echo "==========================================="
echo ""
echo "Next steps:"
echo "1. Update kennelmart/src/main/resources/application.properties"
echo "2. Set spring.datasource.username=postgres"
echo "3. Set spring.datasource.password=YOUR_POSTGRES_PASSWORD"
echo "4. Run: cd kennelmart && mvn spring-boot:run"
echo ""
echo "Database Details:"
echo "  - Name: kennelmart_db"
echo "  - User: postgres"
echo "  - Tables created:"
echo "    • users"
echo "    • verified_identities"
echo ""
