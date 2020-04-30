FROM scratch as distro
ADD distro/target/*-server.tar.gz /tmp/
ADD distro/target/*-hook.tar.gz /tmp/

FROM adoptopenjdk/openjdk8:alpine-slim
	
WORKDIR /root

RUN apk add --no-cache \
    bash \
    su-exec \
    python \
    npm 

COPY --from=distro /tmp/apache-* /root/atlas-bin/

EXPOSE 21000

ENV PATH=$PATH:/root/atlas-bin/bin
ENV ATLAS_HOME=/root/atlas-bin

CMD ["/bin/bash", "-c", "$ATLAS_HOME/bin/atlas_start.py; tail -fF $ATLAS_HOME/logs/application.log"]