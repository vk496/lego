FROM ubuntu:16.04
LABEL maintainer="valiantsin.kivachuk@um.es"

ENV DEBIAN_FRONTEND noninteractive        

#Colors
RUN bash -c 'echo -e "Dpkg::Progress-Fancy "1";\nAPT::Color "1";" > /etc/apt/apt.conf.d/99geekosupremo'

RUN echo "deb http://ppa.launchpad.net/saiarcot895/myppa/ubuntu xenial main " >> /etc/apt/sources.list.d/apt-fast.list && \
        echo "deb-src http://ppa.launchpad.net/saiarcot895/myppa/ubuntu xenial main " >> /etc/apt/sources.list.d/apt-fast.list && \
        apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys DC058F40 && \
        apt-get update && apt-get install --no-install-recommends -y apt-fast && apt-fast install --no-install-recommends -y \
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
&& rm -rf /var/lib/apt/lists/* \
&& rm -rf /var/cache/oracle-jdk8-installer

ENV SNMP_VERSION 5.7.3

RUN wget -P /usr/src/ https://sourceforge.net/projects/net-snmp/files/net-snmp/$SNMP_VERSION/net-snmp-$SNMP_VERSION.tar.gz && tar --directory /usr/src/ -xvf /usr/src/net-snmp-$SNMP_VERSION.tar.gz

# Configure
RUN cd /usr/src/net-snmp-$SNMP_VERSION && \
        ./configure \
            --with-security-modules=tsm \
            --with-transports="DTLSUDP TLSTCP" \
            --with-default-snmp-version=3 \
            --with-sys-contact=admin@um.es \
            --with-sys-location=Murcia \
            --with-logfile=/var/log/snmpd.log \
            --with-persistent-directory=/var/net-snmp \
            --disable-embedded-perl \
            --without-perl-modules \
        && \
        make -j $(($(getconf _NPROCESSORS_ONLN)+1)) && \
        make -j $(($(getconf _NPROCESSORS_ONLN)+1)) install && \
        \
        rm -Rf /usr/src/net-snmp*


#Wrapper
COPY misc/supervisord.conf /etc/supervisor/conf.d/supervisord.conf
COPY misc/traps_wrapper /usr/bin/traps_wrapper

RUN sed -i 's/.*mibs :/#&/' /etc/snmp/snmp.conf #Enable all MIBS

COPY conf/snmpd.conf /etc/snmp/snmpd.conf
COPY conf/snmptrapd.conf /etc/snmp/snmptrapd.conf

ENTRYPOINT ["/usr/bin/supervisord"]
