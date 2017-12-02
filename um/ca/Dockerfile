FROM ubuntu:16.04
LABEL maintainer="valiantsin.kivachuk@um.es"

ENV DEBIAN_FRONTEND noninteractive        

#Colors
RUN bash -c 'echo -e "Dpkg::Progress-Fancy "1";\nAPT::Color "1";" > /etc/apt/apt.conf.d/99geekosupremo'

RUN echo "deb http://ppa.launchpad.net/saiarcot895/myppa/ubuntu xenial main " >> /etc/apt/sources.list.d/apt-fast.list && \
        echo "deb-src http://ppa.launchpad.net/saiarcot895/myppa/ubuntu xenial main " >> /etc/apt/sources.list.d/apt-fast.list && \
        apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys DC058F40 && \
        apt-get update && apt-get install --no-install-recommends -y apt-fast && apt-fast install -y \
            net-tools \
            traceroute \
            netcat \
            dnsutils \
            iputils-ping \
            curl \
            nano \
            ifupdown2 \
            bash-completion \
            supervisor \
            locales \
            software-properties-common \
            snmpd snmptrapd snmp snmp-mibs-downloader \
            wget \
            git-core \
            unzip \
            default-mta \
            libssl-dev \
            ca-certificates \
            build-essential \
            libtool \
            freeradius-utils \
&& rm -rf /var/lib/apt/lists/* \
&& rm -rf /var/cache/oracle-jdk8-installer

WORKDIR /certs

#Wrapper
COPY misc/supervisord.conf /etc/supervisor/conf.d/supervisord.conf

ENTRYPOINT ["/usr/bin/supervisord"]
