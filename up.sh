#!/bin/bash
set -e
trap 'exit 130' INT #Exit if trap Ctrl+C

#Software necesario
software=( sudo bash docker-compose sysctl docker iptables grep awk basename cat cut sed)

for i in "${software[@]}"; do
    if ! hash $i 2>/dev/null; then
            echo -e "missing $i"
            exit 1
    fi
done

#Root 
[ "$UID" -eq 0 ] || exec sudo bash "$0" "$@"

while getopts ":m :b" opt; do
    case $opt in
        b)
            echo "Installing bridge driver"
            base="$(sed '/# NETWORKS #############################/Q' docker-compose.yml)"
            echo "$base" >docker-compose.yml
            cat .docker-compose.BRIDGE >>docker-compose.yml
        ;;
        m)
            echo "Installing macvlan driver - Review parent interfaces..."
            base="$(sed '/# NETWORKS #############################/Q' docker-compose.yml)"
            echo "$base" >docker-compose.yml
            cat .docker-compose.MACVLAN >>docker-compose.yml
        ;;
        *)
            echo "Invalid option: -$OPTARG" >&2
            echo "Use -b or -m" >&2
            exit 1
        ;;
    esac
done


driver=$(sed -n -e '/# NETWORKS #/,$p' docker-compose.yml | grep -i driver: -m1 | cut -d: -f2 | sed 's/ //g')

if [ $driver == "macvlan" ]; then
    color='\033[0;31m'
elif [ $driver == "bridge" ]; then
    color='\033[0;33m'
else
    echo "Strange network driver. Exiting...."
    exit 1
fi


echo -e "Usign $color${driver}\033[0m network driver"
sleep 1.5


domains=( "um" "upm" )

for dom in "${domains[@]}"; do
    if ! sudo -u $SUDO_USER docker volume inspect $dom-certs 2>/dev/null >/dev/null; then
        sudo -u $SUDO_USER docker volume create $dom-certs
        sudo -u $SUDO_USER docker run --rm -v $dom-certs:/certs \
            -e SSL_SUBJECT="*.$dom.es" \
            -e SSL_SIZE="4096" \
            -e SSL_EXPIRE="360" \
        paulczar/omgwtfssl
    fi
done
sudo -u $SUDO_USER docker-compose up --build -d #Deploy services

sudo sysctl -w net.ipv4.ip_forward=1 #Enable IP Forwarding

#Get list of virtual interfaces
com=
while read -r line; do
	if [[ $line ]]; then
	    com="${com}\|$line"
	fi
done < <(sudo -u $SUDO_USER docker network ls | grep "$(basename "$(pwd)")" |grep -v default| awk '{print $1}')

#Disable iptables for comunicate between virtual interfaces
if [[ $com ]]; then
	while read -r line; do
	    echo "iptables ${line/-A DOCKER-ISOLATION/-D DOCKER-ISOLATION}"
	    eval "iptables ${line/-A DOCKER-ISOLATION/-D DOCKER-ISOLATION}"
	done < <(eval "iptables-save | grep \"DOCKER-ISOLATION\" | grep -i \"\-j DROP\" | grep -v \"docker0\" | grep \"${com:2:-1}\"")
fi
