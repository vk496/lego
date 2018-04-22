FROM vk496/ubuntu-lego

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

#Firewall
COPY conf/firewall.ipv4 /etc/iptables_firewall

ENTRYPOINT ["/usr/bin/supervisord"]
