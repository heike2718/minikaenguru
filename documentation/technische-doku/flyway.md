# Änderungen für Datenbank

## In der Entwicklung

Aus historischen Gründen wurde Flyway separat manuell gestartet. Das lässt sich ohne Risiko des kompletten Datenverlusts jetzt nicht mehr ändern. Daher wird hier beschrieben, wie man vorgehen muss, um eine neue Version der DB-Schema zu erzeugen.

Die Migrationsskripte liegen alle unter home/heike/git/konfigurationen/flyway.

Flyway liegt hier: /opt/flyway-5.2.4

### backup dumps ziehen

Hier VXXX durch die aktuelle Version ersetzen!!!

```
mysqldump --databases mk_wettbwerb --dump-date --add-drop-database -h 172.21.0.2  -u root -p  > VXX__mk_wettbewerb_complete_dump.sql
```

### migrate

```
sudo /opt/flyway-5.2.4/flyway  -configFile=/home/heike/git/konfigurationen/flyway/mk_kataloge/conf/flyway.conf migrate
sudo /opt/flyway-5.2.4/flyway  -configFile=/home/heike/git/konfigurationen/flyway/mk_wettbewerb/conf/flyway.conf migrate
```
Anchließend gleich wieder dump ziehen

## Test-DB aktualisieren

analog zu Produktion:

### Migration einspielen

Migrationsskript nach dumps kopieren. Dann 

```
docker image build -t heik2718/mk_wettbewerb-test-db .

docker container stop mk_wettbewerb-test-db

docker container rm mk_wettbewerb-test-db

docker container run -p 3309:3306 --name mk_wettbewerb-test-db -e MYSQL_ROOT_PASSWORD_FILE=/run/secrets/mysql-root-pwd -e MYSQL_DATABASE_FILE=/run/secrets/mysql-database -e MYSQL_USER_FILE=/run/secrets/mysql-user -e MYSQL_PASSWORD_FILE=/run/secrets/mysql-user-pwd -d heik2718/mk_wettbewerb-test-db

```

Zuerst den kompletten dump einspielen, dann die Migration (Versionsnummer ist nur angedeutet)

```
docker container exec -it mk_wettbewerb-test-db bash
cd /tmp
mysql -u root -p
source docker-VXX__mk_wettbewerb_complete_dump.sql
source V34__table_newsletterversand.sql
```


### von außen connecten

TCP address finden:

```
docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' mk_wettbewerb-test-db
```

```
mysql -h 172.17.0.2 -u root -p
```

### dump ziehen

Hier wieder mit der Versionsnummer aufpassen

```
cd dumps

mysqldump --databases mk_wettbewerb --dump-date --add-drop-database -h172.17.0.2  -u root -p  > docker-V34__mk_wettbewerb_complete_dump.sql
```

Damit kann dann die DB vor jedem Test zurückgesetzt werden.


## Produktion

Hier werden die Migrationsskripte in den container kopiert und manuell ausgeführt ohne Flyway
