FROM vk496/openldap
LABEL maintainer="valiantsin.kivachuk@um.es"


ENV DEBIAN_FRONTEND noninteractive

#TODO ffix duplicated sourcelist
RUN echo "deb http://deb.debian.org/debian stretch main non-free contrib" >> /etc/apt/sources.list && \
echo "deb http://ppa.launchpad.net/saiarcot895/myppa/ubuntu xenial main " >> /etc/apt/sources.list.d/apt-fast.list && \
        echo "deb-src http://ppa.launchpad.net/saiarcot895/myppa/ubuntu xenial main " >> /etc/apt/sources.list.d/apt-fast.list && \
        apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys DC058F40 && \
        apt-get update && apt-get --no-install-recommends -y install apt-fast && apt-fast install -y \
        net-tools \
        traceroute \
        netcat \
        dnsutils \
        iputils-ping \
        curl \
        nano \
        supervisor \
        snmpd snmp snmp-mibs-downloader \
        iptables \
&& rm -rf /var/lib/apt/lists/*

RUN apt-fast update && apt-fast install -y runit && rm -rf /var/lib/apt/lists/*

COPY misc/supervisord.conf /etc/supervisor/conf.d/supervisord.conf
COPY conf/snmpd.conf /etc/snmp/snmpd.conf

#Extend LDAP Schema
COPY conf/um.ldif /container/service/slapd/assets/config/bootstrap/ldif/custom/

RUN sed -i 's/.*mibs :/#&/' /etc/snmp/snmp.conf #Enable all MIBS

#Firewall
COPY conf/firewall.ipv4 /etc/iptables_firewall

RUN mkdir /container/service/supervisor && \
    echo '#!/bin/bash\nexec /usr/bin/supervisord' > /container/service/supervisor/process.sh && \
    chmod +x /container/service/supervisor/process.sh
# CMD ["/usr/bin/supervisord"]
