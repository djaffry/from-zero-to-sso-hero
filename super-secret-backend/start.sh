#!/bin/bash
set -e

echo "Starting socat proxy for Keycloak..."
socat TCP-LISTEN:8040,fork,reuseaddr TCP:host.docker.internal:8040 &

echo "Starting Node.js application..."
exec npm start
