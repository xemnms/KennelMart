#!/bin/bash
set -e

echo "========================================"
echo "KennelMart Backend Build Script"
echo "========================================"

cd /workspaces/KennelMart/kennelmart

echo ""
echo "✓ Cleaning Maven cache..."
rm -rf ~/.m2/repository/org/projectlombok 2>/dev/null || true
rm -rf ~/.m2/repository/io/jsonwebtoken 2>/dev/null || true
rm -rf target 2>/dev/null || true

echo "✓ Running Maven clean install..."
mvn clean install -DskipTests -U

echo ""
echo "========================================"
echo "✓ Build completed successfully!"
echo "========================================"
echo ""
echo "Next: Run the application"
echo "  mvn spring-boot:run"
echo ""
