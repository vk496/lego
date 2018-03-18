FROM postgres:10-alpine

RUN apk add --no-cache \
    supervisor \
    && mkdir /etc/supervisord.d

COPY misc/supervisord.conf /etc/supervisord.conf

ENTRYPOINT []
CMD ["/usr/bin/supervisord"]
