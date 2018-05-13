FROM maven:3.5.3-jdk-8-alpine as builder

ENV work_dir /src/oauth_um
WORKDIR $work_dir

COPY src/ $work_dir

RUN mvn clean package


FROM payara/micro:5.181
#Temp disable non-root user
USER root

RUN apk add --no-cache \
    supervisor \
    iproute2 \
    nginx \
    && mkdir /etc/supervisord.d


COPY --from=builder /src/oauth_um/target/oauth_UM-1.0-SNAPSHOT.war /opt/payara/deployments/


COPY misc/supervisord.conf /etc/supervisord.conf
RUN mkdir -p /var/log/supervisor; chown payara:payara /var/log/supervisor

#Nginx
COPY conf/default.conf /etc/nginx/conf.d/default.conf
RUN mkdir /run/nginx

#Firewall
COPY conf/firewall.ipv4 /etc/iptables_firewall

ENTRYPOINT []
CMD ["/usr/bin/supervisord"]
