#!/bin/bash
set -e

POSTGRES="psql --username postgres"

echo "Creating database: dvdrental"

$POSTGRES <<EOSQL
SELECT 'CREATE DATABASE dvdrental' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'dvdrental')\gexec;
EOSQL

echo "Creating dvdrental tables..."
psql -d dvdrental -a -U postgres -f /dvdrental.sql