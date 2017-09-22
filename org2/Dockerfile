FROM ubuntu:16.04
LABEL maintainer="valiantsin.kivachuk@um.es"

RUN apt-get update && apt-get install --no-install-recommends -y \
        net-tools=1.60-26ubuntu1 \
        traceroute=1:2.0.21-1 \
        netcat=1.10-41 \
        dnsutils=1:9.10.3.dfsg.P4-8ubuntu1.8 \
        iputils-ping=3:20121221-5ubuntu2 \
        curl=7.47.0-1ubuntu2.2 \
        nano=2.5.3-2ubuntu2 \
        ifupdown2=1.0~git20151029-1 \
&& rm -rf /var/lib/apt/lists/*

ENTRYPOINT ["/bin/bash"]
