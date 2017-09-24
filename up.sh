#!/bin/bash
set -e
trap 'exit 130' INT #Exit if trap Ctrl+C

if [[ $EUID -ne 0 ]]; then
   echo "This script must be run as root" 
   exit 1
fi

sudo -u $SUDO_USER docker-compose up --build -d || exit 1 #Deploy services

sysctl -w net.ipv4.ip_forward=1 #Enable IP Forwarding

#Get list of virtual interfaces
com=
while read -r line; do
	if [[ $line ]]; then
	    com="${com}\|$line"
	fi
done < <(sudo -u $SUDO_USER docker network ls | grep "$(basename "$(pwd)")" | awk '{print $1}')

#Disable iptables for comunicate between virtual interfaces
if [[ $com ]]; then
	while read -r line; do
	    echo "iptables ${line/-A DOCKER-ISOLATION/-D DOCKER-ISOLATION}"
	    eval "iptables ${line/-A DOCKER-ISOLATION/-D DOCKER-ISOLATION}"
	done < <(eval "iptables-save | grep \"DOCKER-ISOLATION\" | grep -i \"\-j DROP\" | grep -v \"docker0\" | grep \"${com:2:-1}\"")
fi
