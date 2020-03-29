FROM openjdk:8-jdk-alpine

WORKDIR /root

RUN apk add --no-cache \
    bash \
    su-exec \
    python \
    npm \
    && tar xzf /distro/target/*bin.tar.gz --strip-components 1 -C /root/atlas-bin \
    && mkdir /root/atlas-bin/logs

EXPOSE 21000

ENV PATH=$PATH:/root/atlas-bin/bin
ENV ATLAS_HOME=/root/atlas-bin

CMD ["/bin/bash", "-c", "$ATLAS_HOME/bin/atlas_start.py; tail -fF $ATLAS_HOME/logs/application.log"]