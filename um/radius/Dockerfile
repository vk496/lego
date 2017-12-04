FROM ubuntu:16.04
LABEL maintainer="valiantsin.kivachuk@um.es"

#ORG1-N1

RUN echo "deb http://ppa.launchpad.net/saiarcot895/myppa/ubuntu xenial main " >> /etc/apt/sources.list.d/apt-fast.list && \
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
        ifupdown2 \
        supervisor \
        snmpd snmp snmp-mibs-downloader \
        freeradius \
&& rm -rf /var/lib/apt/lists/*

RUN apt-fast update && apt-fast install -y \
        freeradius-ldap \
&& rm -rf /var/lib/apt/lists/*


COPY misc/supervisord.conf /etc/supervisor/conf.d/supervisord.conf

RUN sed -i 's/.*mibs :/#&/' /etc/snmp/snmp.conf #Insert comment

COPY conf/snmpd.conf /etc/snmp/snmpd.conf


#Freeradius
COPY conf/clients.conf /etc/freeradius/clients.conf
COPY conf/eap.conf /etc/freeradius/eap.conf
COPY conf/sites-available/default /etc/freeradius/sites-available/default
COPY conf/sites-available/inner-tunnel /etc/freeradius/sites-available/inner-tunnel
COPY conf/proxy.conf /etc/freeradius/proxy.conf
COPY conf/modules/ldap /etc/freeradius/modules/ldap

RUN sed -i 's/.*mibs :/#&/' /etc/snmp/snmp.conf #Enable all MIBS

CMD ["/usr/bin/supervisord"]
