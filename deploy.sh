#!/bin/bash
set -e
trap 'exit 130' INT #Exit if trap Ctrl+C

docker-compose build && docker-compose up || exit 1 #Deploy services

sudo sysctl -w net.ipv4.ip_forward=1 #Enable IP Forwarding

#Get list of virtual interfaces
com=
while read -r line; do
	if [[ $line ]]; then
	    com="${com}|grep $line "
	fi
done < <(docker network ls | grep "$(basename "$(pwd)")" | awk '{print $1}')

#Disable iptables for comunicate between virtual interfaces
if [[ $com ]]; then
	while read -r line; do
	    echo "iptables ${line/-A DOCKER-ISOLATION/-D DOCKER-ISOLATION}"
	    eval "sudo iptables ${line/-A /-D }"
	done < <(eval "sudo iptables-save | ${com:1:-1}")
fi
