FROM vk496/ubuntu-lego

COPY misc/supervisord.conf /etc/supervisor/conf.d/supervisord.conf

RUN sed -i 's/.*mibs :/#&/' /etc/snmp/snmp.conf #Enable all MIBS

COPY conf/snmpd.conf /etc/snmp/snmpd.conf

#Firewall
COPY conf/firewall.ipv4 /etc/iptables_firewall

CMD ["/usr/bin/supervisord"]
