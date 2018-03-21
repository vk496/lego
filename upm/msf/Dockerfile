FROM alpine:3.7
MAINTAINER Francesco Colista <fcolista@alpinelinux.org>
MAINTAINER Valiantsin Kivachuk <valiantsin.kivachuk@um.es>
ENV MSF_TAG=4.16.43
ENV PATH=$PATH:/usr/share/metasploit-framework
ENV NOKOGIRI_USE_SYSTEM_LIBRARIES=1
RUN apk add --no-cache \
	build-base \
	ruby \
	ruby-bigdecimal \
	ruby-bundler \
	ruby-io-console \
	ruby-dev \
	libffi-dev\
        libressl-dev \
	readline-dev \
	sqlite-dev \
	postgresql-dev \
        libpcap-dev \
	libxml2-dev \
	libxslt-dev \
	yaml-dev \
	zlib-dev \
	ncurses-dev \
        autoconf \
	bison \
	subversion \
	git \
	sqlite \
	libxslt \
	postgresql \
  ncurses \
      wget linux-headers


ENV NMAP_VERSION=7.70
RUN set -e; \
      mkdir -p /usr/src; \
      cd /usr/src; \
      wget https://nmap.org/dist/nmap-$NMAP_VERSION.tar.bz2; \
      tar xvf nmap-$NMAP_VERSION.tar.bz2; \
        cd nmap-$NMAP_VERSION; \
          ./configure; \
          make; \
          make install; \
      rm -Rf /usr/src/nmap-$NMAP_VERSION*

RUN cd /usr/share && \
    git clone --branch $MSF_TAG --depth 1 https://github.com/rapid7/metasploit-framework.git && \
    cd /usr/share/metasploit-framework && \
    bundle install

RUN apk del \
	ruby-dev \
	libffi-dev\
        libressl-dev \
	readline-dev \
	sqlite-dev \
	postgresql-dev \
	libxml2-dev \
	libxslt-dev \
	yaml-dev \
	zlib-dev \
	ncurses-dev \
	bison \
	autoconf \
	&& rm -rf /var/cache/apk/*

RUN apk add --no-cache supervisor samba-client busybox-extras && \
            mkdir /var/log/supervisor/

COPY misc/supervisord.conf /etc/supervisord.conf

COPY ./conf/start.sh /usr/local/bin/start.sh

CMD ["/usr/bin/supervisord"]
