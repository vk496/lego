FROM alpine

MAINTAINER Valentín Kivachuk <valiantsin.kivachuk@um.es>

RUN apk add --no-cache \
        iproute2 \
        supervisor \
        snort

RUN mkdir /var/log/supervisor

COPY misc/supervisord.conf /etc/supervisord.conf
COPY conf/route.v4 /etc/iptables_route

COPY conf/route_interlan /usr/bin/

COPY conf/snort/snort.conf /etc/snort/snort.conf
COPY conf/snort/rules /var/lib/snort/rules
COPY misc/snort/ /var/lib/snort/etc/

CMD ["/usr/bin/supervisord"]
