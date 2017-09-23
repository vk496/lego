FROM ubuntu:16.04
LABEL maintainer="valiantsin.kivachuk@um.es"

ENV DEBIAN_FRONTEND noninteractive
# RUN locale-gen en_US.UTF-8
# ENV LANG en_US.UTF-8
# ENV LANGUAGE en_US:en 
# ENV LC_ALL en_US.UTF-8

ENV JAVA_HOME /usr/lib/jvm/java-8-oracle
ENV OPENNMS_HOME=/usr/share/opennms

RUN echo 'Dpkg::Progress-Fancy "1";' > /etc/apt/apt.conf.d/99progressbar

RUN echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu xenial main " >> /etc/apt/source.list && \
        echo "deb-src http://ppa.launchpad.net/webupd8team/java/ubuntu xenial main " >> /etc/apt/source.list && \
        apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys EEA14886 && \
        
        echo "deb http://debian.opennms.org opennms-20 main" >> /etc/apt/sources.list.d/opennms.list && \
        echo "deb-src http://debian.opennms.org opennms-20 main " >> /etc/apt/sources.list.d/opennms.list && \
        apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 5B9EFD43 && \
        
        echo "opennmsdb opennms-db/noinstall string ok" | debconf-set-selections && \
        echo "debconf shared/accepted-oracle-license-v1-1 select true" | debconf-set-selections && \
        echo "debconf shared/accepted-oracle-license-v1-1 seen true" | debconf-set-selections && \
        echo "postfix postfix/mailname string opennms" | debconf-set-selections && \
        echo "postfix postfix/main_mailer_type string 'Internet Site'" | debconf-set-selections && \
        
        apt-get update && apt-get install --no-install-recommends -y \
            net-tools=1.60-26ubuntu1 \
            traceroute=1:2.0.21-1 \
            netcat=1.10-41 \
            dnsutils=1:9.10.3.dfsg.P4-8ubuntu1.8 \
            iputils-ping=3:20121221-5ubuntu2 \
            curl=7.47.0-1ubuntu2.2 \
            nano=2.5.3-2ubuntu2 \
            ifupdown2=1.0~git20151029-1 \
            supervisor \
            snmpd snmptrapd snmp snmp-mibs-downloader \
            software-properties-common \
            wget \
            git-core \
            unzip \
            default-mta \
            opennms \
&& rm -rf /var/lib/apt/lists/* \
&& rm -rf /var/cache/oracle-jdk8-installer

COPY supervisord.conf /etc/supervisor/conf.d/supervisord.conf


RUN sed -i 's/.*mibs :/#&/' /etc/snmp/snmp.conf #Insert comment
COPY conf/snmpd.conf /etc/snmp/snmpd.conf
COPY conf/snmptrapd.conf /etc/snmp/snmptrapd.conf

ENTRYPOINT ["/usr/bin/supervisord"]
