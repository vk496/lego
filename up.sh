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


domains=(
    "um:192.168.251"
    "upm:192.168.252"
)

for domain in "${domains[@]}"; do
        dom=$(echo $domain | cut -d: -f1) #Get domain
        ip_range=$(echo $domain | cut -d: -f2) #Get IP range
        
    if ! sudo -u $SUDO_USER docker volume inspect $dom-certs 2>/dev/null >/dev/null; then
        init_TLS=true
        
        sudo -u $SUDO_USER docker volume create $dom-certs #All certs
        
        #Generate CA
        sudo -u $SUDO_USER docker run --rm -v $dom-certs:/certs \
            -e CA_KEY="$dom-key.pem" \
            -e CA_CERT="$dom.pem" \
            -e CA_SUBJECT="$dom" \
            -e CA_EXPIRE="1000" \
            -e SSL_SIZE="4096" \
            -e SSL_KEY="delete_key.pem" \
            -e SSL_CSR="delete_key.csr" \
            -e SSL_CERT="delete_cert.pem" \
        paulczar/omgwtfssl

        #Delete stuff
        echo "DELETE default server cert & keys"
        sudo -u $SUDO_USER docker run --rm -v $dom-certs:/certs paulczar/omgwtfssl bash -c 'rm delete*'
        echo "OK"
        
        servicios=(
            "owncloud:$ip_range.5"
            "voip:$ip_range.2"
        )
        
        for servicio in "${servicios[@]}"; do
            subdom=$(echo $servicio | cut -d: -f1) #Get domain of service
            ip_serv=$(echo $servicio | cut -d: -f2) #Get ip of service
            
            sudo -u $SUDO_USER docker volume create $dom-certs-$subdom #Service certs
            
            #Generate cert
            sudo -u $SUDO_USER docker run --rm -v $dom-certs:/certs \
                -e CA_KEY="$dom-key.pem" \
                -e CA_CERT="$dom.pem" \
                -e CA_SUBJECT="$dom" \
                -e SSL_KEY="$subdom-key.pem" \
                -e SSL_CSR="$subdom-key.csr" \
                -e SSL_CERT="$subdom-cert.pem" \
                -e SSL_SIZE="2048" \
                -e SSL_EXPIRE="360" \
                -e SSL_SUBJECT="$ip_serv" \
                -e SSL_DNS="$subdom.$dom.es" \
            paulczar/omgwtfssl
            
            #Aislate the keys from CA
            sudo -u $SUDO_USER docker run --rm -v $dom-certs:/certs -v $dom-certs-$subdom:/service_certs paulczar/omgwtfssl bash -c "cp /certs/{$subdom*,$dom.pem} /service_certs"
        done
    fi
        
done

if [ $init_TLS ]; then
    sudo -u $SUDO_USER docker volume create lego_ca #Public CA keys

    for domain in "${domains[@]}"; do
        dom=$(echo $domain | cut -d: -f1) #Get domain
        
        sudo -u $SUDO_USER docker run --rm -v $dom-certs:/certs -v lego_ca:/public_ca paulczar/omgwtfssl bash -c "cp /certs/$dom.pem /public_ca && cp /certs/$dom.pem /public_ca/\$(openssl x509 -in /certs/$dom.pem -noout -hash).0"
    done
fi

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
