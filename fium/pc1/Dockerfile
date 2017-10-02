FROM ubuntu:16.04
LABEL maintainer="valiantsin.kivachuk@um.es"

#ORG1-N1

RUN echo "deb http://ppa.launchpad.net/saiarcot895/myppa/ubuntu xenial main " >> /etc/apt/sources.list.d/apt-fast.list && \
        echo "deb-src http://ppa.launchpad.net/saiarcot895/myppa/ubuntu xenial main " >> /etc/apt/sources.list.d/apt-fast.list && \
        apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys DC058F40 && \
        apt-get update && apt-get --no-install-recommends -y install apt-fast && apt-fast install --no-install-recommends -y \
        net-tools=1.60-26ubuntu1 \
        traceroute=1:2.0.21-1 \
        netcat=1.10-41 \
        dnsutils=1:9.10.3.dfsg.P4-8ubuntu1.8 \
        iputils-ping=3:20121221-5ubuntu2 \
        curl=7.47.0-1ubuntu2.2 \
        nano=2.5.3-2ubuntu2 \
        ifupdown2=1.0~git20151029-1 \
        supervisor \
        snmpd snmp snmp-mibs-downloader \        
&& rm -rf /var/lib/apt/lists/*

COPY misc/supervisord.conf /etc/supervisor/conf.d/supervisord.conf

# RUN sed -i 's/.*mibs :/#&/' /etc/snmp/snmp.conf #Insert comment

COPY conf/snmpd.conf /etc/snmp/snmpd.conf

CMD ["/usr/bin/supervisord"]
