FROM postgres:10-alpine

RUN apk add --no-cache \
    supervisor \
    iproute2 \
    && mkdir /etc/supervisord.d

COPY misc/supervisord.conf /etc/supervisord.conf

#Firewall
COPY conf/firewall.ipv4 /etc/iptables_firewall

ENTRYPOINT []
CMD ["/usr/bin/supervisord"]
