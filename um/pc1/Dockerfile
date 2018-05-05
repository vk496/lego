FROM vk496/ubuntu-lego

COPY misc/supervisord.conf /etc/supervisor/conf.d/supervisord.conf

RUN sed -i 's/.*mibs :/#&/' /etc/snmp/snmp.conf #Enable all MIBS

COPY conf/snmpd.conf /etc/snmp/snmpd.conf

RUN set -e; \
      apt-fast update; \
        apt-fast install -y dsniff; \
      rm -rf /var/lib/apt/lists/*

CMD ["/usr/bin/supervisord"]
