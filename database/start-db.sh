#!/bin/bash

echo "========================================"
echo "   Starting PostgreSQL with Docker"
echo "========================================"
echo

echo "[1] Starting PostgreSQL container..."
docker-compose up -d postgres

if [ $? -ne 0 ]; then
    echo "[ERROR] Failed to start PostgreSQL container!"
    echo "Make sure Docker is running and try again."
    exit 1
fi

echo
echo "[2] Waiting for PostgreSQL to be ready..."
sleep 5

while ! docker-compose exec postgres pg_isready -U postgres -d app_swing_db > /dev/null 2>&1; do
    echo "Waiting for database to be ready..."
    sleep 2
done

echo
echo "[SUCCESS] PostgreSQL is ready!"
echo
echo "Database Information:"
echo "- Host: localhost"
echo "- Port: 5432"
echo "- Database: app_swing_db"
echo "- Username: postgres"
echo "- Password: 123456"
echo
echo "You can now run your Java application!"
echo
echo "To stop the database, run: docker-compose down"
echo "To view logs: docker-compose logs postgres"
echo 