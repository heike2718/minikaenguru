# Veröffentlichung der Unterlagen

## Namenskonvention für die zip-Files

- {0}-minikangaroo-english-private.zip
- {0}-minikangaroo-english-schools.zip
- {0}-minikaenguru-deutsch-privat.zip
- {0}-minikaenguru-deutsch-schulen.zip

Sabei ist {0} das aktuelle Wettbewerbjahr

Diese erstellen und hier ablegen:

```
/media/veracrypt1/ansible/vserver/minikaenguru-unterlagen/
```

## Ablageort auf dem Server

```
/home/drpwzrd/docker-volumes/mk-gateway/files/unterlagen#
```

Permissions: 

644 für die Files
7xx für den Verzeichnisbaum


## Ansible

```
/media/veracrypt1/ansible/vserver/roles/vserver1/tasks/minikaenguru/v1-05-release-unterlagen.yml
```

Starten mittels 

```
cd /media/veracrypt1/ansible/vserver/scripts/v1/minikaenguru/
. v1-05-release-unterlagen.sh
```


# Freischaltung der Unterlagen

## Standardweg

Status des Wettbewerbs auf "Download xxx" setzen. Wenn die Zip-Files auf dem Server sind und die korrekten Permissions haben

## Einzelne User berechtigen

In der Admin-Anwendung Veranstalter Suchen und Erlaubnis auf "ERTEILT" setzen.
