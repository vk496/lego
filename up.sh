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

help_usage() {
            echo "Invalid option: -$OPTARG" >&2
            echo -e "Use:\n $0 -b \n $0 -m iface1 -m iface2" >&2
}

while getopts ":m: :b" opt; do
    case $opt in
        b)
            echo "Installing bridge driver"
            base="$(sed '/# NETWORKS #############################/Q' docker-compose.yml)"
            echo "$base" >docker-compose.yml
            cat .docker-compose.BRIDGE >>docker-compose.yml
        ;;
        m)
            multi+=("$OPTARG")
            mode_macvlan=1
        ;;
        *)
            help_usage
            exit 1
        ;;
    esac
done

if [ $mode_macvlan ]; then

    if [ ${#multi[@]} -ne 2 ]; then
        help_usage
        exit 1
    fi

    shift $((OPTIND -1))

    echo "Installing macvlan driver - Review parent interfaces..."
    base="$(sed '/# NETWORKS #############################/Q' docker-compose.yml)"
    echo "$base" >docker-compose.yml
    cat .docker-compose.MACVLAN >>docker-compose.yml

    sed -i "s/enp1s0.3/${multi[0]}/g" docker-compose.yml
    sed -i "s/enp1s0.4/${multi[1]}/g" docker-compose.yml

fi

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
        
        servicios=(
            "owncloud:$ip_range.5"
            "voip:$ip_range.2"
            "ldap:$ip_range.6"
            "radius:$ip_range.3"
        )
        
        #GENERAL SSL ARGUMENTS
        LEGO_CA_KEY="$dom-key.pem"
        LEGO_CA_CERT="$dom.pem"
        LEGO_CA_SUBJECT="$dom"
        LEGO_CA_EXPIRE="10000"
        LEGO_SSL_CONFIG="openssl.cnf"
        LEGO_SSL_CRL="http://crl.$dom.es/$dom.crl,http://$ip_range.99/$dom.crl"
        LEGO_SSL_SIZE="2048"
        LEGO_SSL_EXPIRE="360"
            
    if ! sudo -u $SUDO_USER docker volume inspect $dom-certs 2>/dev/null >/dev/null; then        
        sudo -u $SUDO_USER docker volume create $dom-certs #All certs
        
        #Generate CA
        sudo -u $SUDO_USER docker run --rm -v $dom-certs:/certs \
            -e CA_KEY="$LEGO_CA_KEY" \
            -e CA_CERT="$LEGO_CA_CERT" \
            -e CA_SUBJECT="$LEGO_CA_SUBJECT" \
            -e CA_EXPIRE="$LEGO_CA_EXPIRE" \
            -e SSL_SIZE="4096" \
            -e SSL_KEY="revoked_key.pem" \
            -e SSL_CSR="revoked_key.csr" \
            -e SSL_CERT="revoked_cert.pem" \
            -e SSL_CRL="$LEGO_SSL_CRL" \
        vk496/omgwtfssl

        sudo -u $SUDO_USER docker run --rm -v $dom-certs:/certs vk496/omgwtfssl cat $dom.pem > $dom.pem #Get CA outside Docker
        
        #Revoke default SSL key
        echo "REVOKE default server cert & keys"
        sudo -u $SUDO_USER docker run --rm -v $dom-certs:/certs vk496/omgwtfssl bash -c "openssl ca -config openssl.cnf -revoke revoked_cert.pem -keyfile $LEGO_CA_KEY -cert $LEGO_CA_CERT"
        echo "OK"
        
        for servicio in "${servicios[@]}"; do
            subdom=$(echo $servicio | cut -d: -f1) #Get domain of service
            ip_serv=$(echo $servicio | cut -d: -f2) #Get ip of service
            
            LEGO_SSL_KEY="$subdom-key.pem"
            LEGO_SSL_CSR="$subdom-key.csr"
            LEGO_SSL_CERT="$subdom-cert.pem"
            LEGO_SSL_SUBJECT="$ip_serv"
            LEGO_SSL_DNS="$subdom.$dom.es"
            LEGO_SSL_IP="$ip_serv"
            
            sudo -u $SUDO_USER docker volume create $dom-certs-$subdom #Service certs
            
            #Generate cert
            sudo -u $SUDO_USER docker run --rm -v $dom-certs:/certs \
                -e CA_KEY="$LEGO_CA_KEY" \
                -e CA_CERT="$LEGO_CA_CERT" \
                -e CA_SUBJECT="$LEGO_CA_SUBJECT" \
                -e SSL_KEY="$LEGO_SSL_KEY" \
                -e SSL_CSR="$LEGO_SSL_CSR" \
                -e SSL_CERT="$LEGO_SSL_CERT" \
                -e SSL_SIZE="$LEGO_SSL_SIZE" \
                -e SSL_EXPIRE="$LEGO_SSL_EXPIRE" \
                -e SSL_SUBJECT="$LEGO_SSL_SUBJECT" \
                -e SSL_DNS="$LEGO_SSL_DNS" \
                -e SSL_IP="$LEGO_SSL_IP" \
                -e SSL_CRL="$LEGO_SSL_CRL" \
            vk496/omgwtfssl
            
            #Aislate the keys from CA and full chain CA
            sudo -u $SUDO_USER docker run --rm -v $dom-certs:/certs -v $dom-certs-$subdom:/service_certs vk496/omgwtfssl bash -c "cp /certs/{$subdom*,$dom.pem} /service_certs && cat /certs/{$subdom-cert.pem,$dom.pem} > /service_certs/$subdom-cert-full.pem && openssl dhparam -out /service_certs/dh 2048"
        done
    fi
     
    if ! sudo -u $SUDO_USER docker volume inspect $dom-certs-user 2>/dev/null >/dev/null; then
        sudo -u $SUDO_USER docker volume create $dom-certs-user #Public CA keys
                
        #Generate User certificates
        sudo -u $SUDO_USER docker run --rm -v $dom-certs:/certs -v $dom-certs-user:/certs_user vk496/omgwtfssl bash -ce "\
            openssl genrsa -out /certs_user/user-key.pem $LEGO_SSL_SIZE > /dev/null && \
            openssl req -new -key /certs_user/user-key.pem -out /certs_user/user-key.csr -subj \"/CN=user1@$dom.es\" -config ${LEGO_SSL_CONFIG} > /dev/null && \
            openssl x509 -req -in /certs_user/user-key.csr -CA ${LEGO_CA_CERT} -CAkey ${LEGO_CA_KEY} -CAcreateserial -out /certs_user/user-cert.pem -days ${LEGO_SSL_EXPIRE} -extensions usr_cert -extfile ${LEGO_SSL_CONFIG} > /dev/null"
        
        sudo -u $SUDO_USER docker run --rm -v $dom-certs-user:/certs vk496/omgwtfssl cat user-cert.pem > $dom-user-cert.pem #Get cert outside docker
        sudo -u $SUDO_USER docker run --rm -v $dom-certs-user:/certs vk496/omgwtfssl cat user-key.pem > $dom-user-key.pem #Get keys outside docker
            
    fi
     
done

if ! sudo -u $SUDO_USER docker volume inspect lego_ca 2>/dev/null >/dev/null; then
    sudo -u $SUDO_USER docker volume create lego_ca #Public CA keys

    for domain in "${domains[@]}"; do
        dom=$(echo $domain | cut -d: -f1) #Get domain
        
        #Prepare public CA certs
        sudo -u $SUDO_USER docker run --rm -v $dom-certs:/certs -v lego_ca:/public_ca vk496/omgwtfssl bash -c "cp /certs/$dom.pem /public_ca && cp /certs/$dom.pem /public_ca/\$(openssl x509 -in /certs/$dom.pem -noout -hash).0"
        
        #Generate CRL
        sudo -u $SUDO_USER docker run --rm -v $dom-certs:/certs -v lego_ca:/public_ca vk496/omgwtfssl bash -c "openssl ca -gencrl -config /certs/openssl.cnf -keyfile /certs/$dom-key.pem -cert $dom.pem -out /public_ca/$dom.crl"
        
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
