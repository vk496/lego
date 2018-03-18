FROM vk496/vm-base:0.0.4
COPY metasploitable2.qcow2 /base_image/metasploitable2.img
RUN apt-get update && apt-get install -y supervisor

#Wrapp
COPY misc/supervisord.conf /etc/supervisor/conf.d/supervisord.conf

ENTRYPOINT ["/usr/bin/supervisord"]
