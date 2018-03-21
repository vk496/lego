FROM vk496/docker-nessus_scanner
LABEL maintainer="valiantsin.kivachuk@um.es"


RUN set -e; \
      curl "https://bootstrap.pypa.io/get-pip.py" | python -; \
      pip install supervisor; \
        mkdir -p /etc/supervisor/conf.d /var/log/supervisor


COPY misc/base.conf /etc/supervisor/supervisord.conf
COPY misc/supervisord.conf /etc/supervisor/conf.d/supervisord.conf


CMD ["/usr/bin/supervisord"]
