FROM vk496/ubuntu-lego

COPY misc/supervisord.conf /etc/supervisor/conf.d/supervisord.conf

RUN sed -i 's/.*mibs :/#&/' /etc/snmp/snmp.conf #Insert comment

COPY conf/snmpd.conf /etc/snmp/snmpd.conf


#Freeradius
COPY conf/clients.conf /etc/freeradius/clients.conf
COPY conf/eap.conf /etc/freeradius/eap.conf
COPY conf/sites-available/default /etc/freeradius/sites-available/default
COPY conf/sites-available/inner-tunnel /etc/freeradius/sites-available/inner-tunnel
COPY conf/proxy.conf /etc/freeradius/proxy.conf
COPY conf/modules/ldap /etc/freeradius/modules/ldap

RUN sed -i 's/.*mibs :/#&/' /etc/snmp/snmp.conf #Enable all MIBS

CMD ["/usr/bin/supervisord"]
