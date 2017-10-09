#!/bin/bash

set -e

docker-compose down

echo "Remember to disaple IP Forwarding:"
echo "sysctl -w net.ipv4.ip_forward=0"
