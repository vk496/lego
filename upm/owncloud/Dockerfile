FROM owncloud/server:9.1.6
LABEL maintainer="valiantsin.kivachuk@um.es"

ENV DEBIAN_FRONTEND noninteractive        

#TODO ffix duplicated sourcelist
RUN echo "deb http://ppa.launchpad.net/saiarcot895/myppa/ubuntu xenial main " >> /etc/apt/sources.list.d/apt-fast.list && \
        echo "deb-src http://ppa.launchpad.net/saiarcot895/myppa/ubuntu xenial main " >> /etc/apt/sources.list.d/apt-fast.list && \
        apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys DC058F40 && \
        apt-get update && apt-get --no-install-recommends -y install apt-fast && apt-fast install -y \
        net-tools \
        traceroute \
        netcat \
        dnsutils \
        iputils-ping \
        curl \
        nano \
        supervisor \
        snmpd snmp snmp-mibs-downloader \
        libldap2-dev \
&& rm -rf /var/lib/apt/lists/*


COPY misc/supervisord.conf /etc/supervisor/conf.d/supervisord.conf
COPY conf/snmpd.conf /etc/snmp/snmpd.conf

#Do magic for LDAP
COPY misc/owncloud_ldap.sh /usr/bin

#SSL
RUN mkdir /etc/apache2/keys
RUN ln -s /certs/owncloud-cert.pem /etc/apache2/keys/ssl-cert.crt
RUN ln -s /certs/owncloud-key.pem /etc/apache2/keys/ssl-cert.key

COPY conf/apache_sites-available/toppath.conf /root/owncloud/toppath.conf
COPY conf/apache_sites-available/subpath.conf /root/owncloud/subpath.conf

CMD ["/usr/bin/supervisord"]
