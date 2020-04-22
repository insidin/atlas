FROM openjdk:8-jdk-alpine

WORKDIR /root

COPY target/distro/ /tmp

RUN apk add --no-cache \
    bash \
    su-exec \
    python \
    npm \
    && mkdir -p /root/atlas-bin \
    && for i in /tmp/apache-atlas-*.tar.gz; do tar -zxf $i --strip-components 1 -C /root/atlas-bin ;done \
    && mkdir /root/atlas-bin/logs \
    && rm /tmp/apache-atlas-*.tar.gz

EXPOSE 21000

ENV PATH=$PATH:/root/atlas-bin/bin
ENV ATLAS_HOME=/root/atlas-bin

CMD ["/bin/bash", "-c", "$ATLAS_HOME/bin/atlas_start.py; tail -fF $ATLAS_HOME/logs/application.log"]