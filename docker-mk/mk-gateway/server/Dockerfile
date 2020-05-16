FROM openjdk:11

MAINTAINER Heike Winkelvoß "public@egladil.de"

WORKDIR /usr/local/bin/mk-gateway

COPY config ./config
COPY *-runner.jar ./app.jar
COPY lib ./lib

VOLUME /usr/local/bin/mk-gateway

EXPOSE 7500

CMD ["java","-jar","app.jar"]