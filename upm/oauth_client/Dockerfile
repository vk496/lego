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
    && mkdir /etc/supervisord.d


COPY --from=builder /src/oauth_um/target/oauth_UPM_CLIENT-1.0-SNAPSHOT.war /opt/payara/deployments/


COPY misc/supervisord.conf /etc/supervisord.conf

USER payara
ENTRYPOINT []
CMD ["/usr/bin/supervisord"]
