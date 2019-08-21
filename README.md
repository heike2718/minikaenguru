# mkadmin-server

REST-API for admin of the mathematical minikaenguru competition

Sarten des Servers mit

java -jar mkadmin-server.jar -Dcom.kumuluz.ee.configuration.file=path-to-auth-provider-config.yaml

Root-Resource zum Testen, ob das Backend da ist:

http://localhost:9400/mkadmin-server/dev

http://localhost:9400/mkadmin-server/heartbeats?heartbeatId=heartbeat


### Starten in Eclipse

mit IDEChecklistenAppRunner und VMargs

	-Dcom.kumuluz.ee.configuration.file=/home/heike/git/mkadmin-server/src/config/mkadmin-server-config.yaml

oder

	-Djavax.net.debug=all -Dcom.kumuluz.ee.configuration.file=/home/heike/git/mkadmin-server/src/config/mkadmin-server-config.yaml


## Relesenotes

* __Release x.x.x:__ text
