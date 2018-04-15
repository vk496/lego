FROM vk496/ubuntu-lego

WORKDIR /certs

#Wrapper
COPY misc/supervisord.conf /etc/supervisor/conf.d/supervisord.conf

ENTRYPOINT ["/usr/bin/supervisord"]
