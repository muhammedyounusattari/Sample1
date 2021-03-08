#!/bin/bash
set -e

POSTGRES="psql --username postgres"

echo "Creating database: sample1"

$POSTGRES <<EOSQL
SELECT 'CREATE DATABASE sample1' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'sample1')\gexec;
EOSQL

echo "Creating sample1 tables..."
psql -d sample1 -a -U postgres -f /sample1.sql