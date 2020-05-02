FROM openjdk:11

MAINTAINER Heike Winkelvoß "public@egladil.de"

WORKDIR /usr/local/bin/mk-kataloge

COPY config ./config
COPY *-runner.jar ./app.jar
COPY lib ./lib

VOLUME /usr/local/bin/mk-kataloge

EXPOSE 7700

CMD ["java","-jar","app.jar"]