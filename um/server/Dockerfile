FROM ubuntu:16.04
LABEL maintainer="valiantsin.kivachuk@um.es"

ENV DEBIAN_FRONTEND noninteractive        

#Colors
RUN bash -c 'echo -e "Dpkg::Progress-Fancy "1";\nAPT::Color "1";" > /etc/apt/apt.conf.d/99geekosupremo'

RUN echo "deb http://ppa.launchpad.net/saiarcot895/myppa/ubuntu xenial main " >> /etc/apt/sources.list.d/apt-fast.list && \
        echo "deb-src http://ppa.launchpad.net/saiarcot895/myppa/ubuntu xenial main " >> /etc/apt/sources.list.d/apt-fast.list && \
        apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys DC058F40 && \
        apt-get update && apt-get install --no-install-recommends -y apt-fast && apt-fast install --no-install-recommends -y \
            net-tools=1.60-26ubuntu1 \
            traceroute=1:2.0.21-1 \
            netcat=1.10-41 \
            dnsutils=1:9.10.3.dfsg.P4-8ubuntu1.8 \
            iputils-ping=3:20121221-5ubuntu2 \
            curl=7.47.0-1ubuntu2.2 \
            nano=2.5.3-2ubuntu2 \
            ifupdown2=1.0~git20151029-1 \
            bash-completion \
            supervisor \
            locales \
            snmpd snmptrapd snmp snmp-mibs-downloader \
            software-properties-common \
            wget \
            git-core \
            unzip \
            default-mta \
&& rm -rf /var/lib/apt/lists/* \
&& rm -rf /var/cache/oracle-jdk8-installer


#Wrapper
COPY misc/supervisord.conf /etc/supervisor/conf.d/supervisord.conf
COPY misc/traps_wrapper /usr/bin/traps_wrapper

RUN sed -i 's/.*mibs :/#&/' /etc/snmp/snmp.conf #Enable all MIBS

COPY conf/snmpd.conf /etc/snmp/snmpd.conf
COPY conf/snmptrapd.conf /etc/snmp/snmptrapd.conf

ENTRYPOINT ["/usr/bin/supervisord"]
