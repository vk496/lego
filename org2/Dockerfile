FROM ubuntu:16.04
LABEL maintainer="valiantsin.kivachuk@um.es"

#ORG2-N1

RUN echo "deb http://ppa.launchpad.net/saiarcot895/myppa/ubuntu xenial main " >> /etc/apt/sources.list.d/apt-fast.list && \
        echo "deb-src http://ppa.launchpad.net/saiarcot895/myppa/ubuntu xenial main " >> /etc/apt/sources.list.d/apt-fast.list && \
        apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys DC058F40 && \
        apt-get update && apt-get --no-install-recommends -y install apt-fast && apt-fast install --no-install-recommends -y \
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
&& rm -rf /var/lib/apt/lists/*

COPY misc/supervisord.conf /etc/supervisor/conf.d/supervisord.conf

# RUN sed -i 's/.*mibs :/#&/' /etc/snmp/snmp.conf #Insert comment

COPY conf/snmpd.conf /etc/snmp/snmpd.conf

CMD ["/usr/bin/supervisord"]
