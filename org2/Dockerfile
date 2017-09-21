FROM ubuntu:16.04

RUN apt-get update && apt-get install -y \
        net-tools \
        traceroute \
        netcat \
        dnsutils \
        iputils-ping \
        curl \
        nano \
        ifupdown2 \
&& rm -rf /var/lib/apt/lists/*

ENTRYPOINT ["/bin/bash"]
