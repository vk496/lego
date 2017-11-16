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
&& rm -rf /var/lib/apt/lists/*

COPY misc/supervisord.conf /etc/supervisor/conf.d/supervisord.conf
COPY conf/snmpd.conf /etc/snmp/snmpd.conf

#Extend LDAP Schema
COPY conf/um.ldif /container/service/slapd/assets/config/bootstrap/ldif/custom/



CMD ["/usr/bin/supervisord"]
