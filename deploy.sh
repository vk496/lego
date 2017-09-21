#!/bin/bash
set -e
trap 'exit 130' INT #Exit if trap Ctrl+C

docker-compose up #Deploy services

sudo sysctl -w net.ipv4.ip_forward=1 #Enable IP Forwarding

#Get list of virtual interfaces
while read -r line; do
    com="${com}|grep $line "
done < <(docker network ls | grep $(basename $(pwd)) | awk '{print $1}')

#Disable iptables for comunicate between virtual interfaces
while read -r line; do
    echo iptables ${line/-A /-D }
    eval "sudo iptables ${line/-A /-D }"
done < <(eval "sudo iptables-save | ${com:1:-1}")
