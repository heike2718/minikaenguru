# MkvApp

Dies ist die Minikänguru-Anwendung, bei der sich Schulen und Privatpersonen für die Teilnahme am [Minikänguru-Wettbewerb](https://mathe-jung-alt.de/minikaenguru) registrieren, die Unterlagen herunterladen, den Wettbewerb auswerten und Urkunden generieren können.


## DEV-Notizen

ng generate store auth/Auth -m auth.module.ts
ng generate store kataloge/Kataloge -m kataloge.module.ts

Wenn anschließend store/update-reducers nicht aufgerufen wird, liegt es vermutlich daran, dass man vergessen hat, das
Modul zu starten (also in app.module.ts deklarieren oder mit forRoot annotieren).
