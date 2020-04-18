FROM openjdk:11

MAINTAINER Heike Winkelvoß "public@egladil.de"

WORKDIR /usr/local/bin/mk-wettbewerb

COPY config ./config
COPY *-runner.jar ./app.jar
COPY lib ./lib

VOLUME /usr/local/bin/mk-wettbewerb

EXPOSE 7550

CMD ["java","-jar","app.jar"]