FROM vk496/ubuntu-lego

ENV VSFTPD_VERSION_BUILD=2.3.4

RUN set -e; \
    set -x; \
      mkdir /usr/src/vsftpd; \
        cd /usr/src/vsftpd; \
          sed -Ei 's/^# deb-src/deb-src/' /etc/apt/sources.list; \
          apt-fast update; \
        wget --no-check-certificate https://launchpad.net/ubuntu/+archive/primary/+sourcefiles/vsftpd/${VSFTPD_VERSION_BUILD}-1ubuntu2/vsftpd_${VSFTPD_VERSION_BUILD}-1ubuntu2.debian.tar.gz; \
        wget --no-check-certificate https://launchpad.net/ubuntu/+archive/primary/+sourcefiles/vsftpd/${VSFTPD_VERSION_BUILD}-1ubuntu2/vsftpd_${VSFTPD_VERSION_BUILD}-1ubuntu2.dsc; \
        git clone --depth 1 https://github.com/nikdubois/vsftpd-${VSFTPD_VERSION_BUILD}-infected vsftpd-${VSFTPD_VERSION_BUILD}; \
        tar xvf vsftpd_${VSFTPD_VERSION_BUILD}-1ubuntu2.debian.tar.gz -C vsftpd-${VSFTPD_VERSION_BUILD}/; \
        cd vsftpd-${VSFTPD_VERSION_BUILD}/; \
          apt-get build-dep -y vsftpd; \
          dpkg-buildpackage -rfakeroot -uc -b; \
          cd ..; \
        dpkg -i vsftpd_${VSFTPD_VERSION_BUILD}*deb; \
        mkdir -p /var/run/vsftpd/empty; \
        echo "local_enable=YES" >> /etc/vsftpd.conf; \
      rm -Rf /usr/src/vsftpd;

COPY misc/supervisord.conf /etc/supervisor/conf.d/supervisord.conf

#SNMP
COPY conf/snmpd.conf /etc/snmp/snmpd.conf
RUN sed -i 's/.*mibs :/#&/' /etc/snmp/snmp.conf #Enable all MIBS


#Firewall
COPY conf/firewall.ipv4 /etc/iptables_firewall

CMD ["/usr/bin/supervisord"]
