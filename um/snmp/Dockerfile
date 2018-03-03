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

ARG SNMP_VERSION=5.6.2
ENV SNMP_VERSION_BUILD=$SNMP_VERSION

ENV SNMP_URL https://sourceforge.mirrorservice.org/n/ne/net-snmp/net-snmp/$SNMP_VERSION_BUILD/net-snmp-$SNMP_VERSION_BUILD.tar.gz

RUN wget -P /usr/src/ $SNMP_URL && tar --directory /usr/src/ -xvf /usr/src/net-snmp-$SNMP_VERSION_BUILD.tar.gz

# Configure
RUN cd /usr/src/net-snmp-$SNMP_VERSION_BUILD && \
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
            --disable-shared \
            --enable-static \
            --with-pic \
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
