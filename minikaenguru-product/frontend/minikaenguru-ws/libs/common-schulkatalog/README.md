# common-schulkatalog

Enthält die Katalog-Komponenten für den Minikänguru-Wettbewerb.

Der Service wird mittels eines Injectiontokens konfiguriert. konfigurationsparameter: Konfigurationsparameter sind

* baseUrl string - die Url für mk-kataloge
* devmode boolean - Flag zum Aktivieren des development modus, in dem die Namen der Komponenten angezeigt werden
* admin boolean - Flag zum Starten als Administrators devmode : boolean - flag, ob im devmode. In diesem Modus werden die Namen der Komponenten im html angezeigt
*
Für den Service gibt es einen Konfigurations-Hook
