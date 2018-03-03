FROM alpine

MAINTAINER Valentín Kivachuk <valiantsin.kivachuk@um.es>

RUN apk add --no-cache iproute2 supervisor

RUN mkdir /var/log/supervisor

COPY misc/supervisord.conf /etc/supervisord.conf
COPY conf/route.v4 /etc/iptables_route


CMD ["/usr/bin/supervisord"]
