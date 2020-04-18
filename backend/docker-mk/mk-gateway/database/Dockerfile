FROM mariadb:10.4

MAINTAINER Heike Winkelvoß "public@egladil.de"

COPY mariadb.cnf /etc/mysql/
COPY my.cnf /etc/mysql/
COPY secrets/* /run/secrets/
COPY dumps/* /tmp/

# wird bereits vom mariadb-Docker-Team gemacht, haben das aber nochmal explizit erwähnt
EXPOSE 3306

VOLUME /var/lib/mysql

CMD ["mysqld"]
