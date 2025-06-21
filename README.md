# minikaenguru

Monorepo für alles, was mit Minikänguru zu tun hat.

## minikaenguru-product

Dies beinhaltet die seit 2017 entwickelte Produktfamilie, die die Durchführung von Minikänguru-Wettbewerben unterstützen.
Aktuell sind es 3 Anwendungen:

- mkv-app Minikänguru-Verwaltung für Veranstalter von Wettbewerben
- mk-admin-app: Anwendung zur Administration
- mkod-app: rudimentäre Statistiken, die aber nie vernünftig weiterentwickelt wurden

[Überblick minikaenguru-product](./minikaenguru-product/README.md)

## statistics

Neue Webanwendung zur Visualisierung von Statistiken des Minikänguru-Wettbewerbs. Sie wird die mkod-app ablösen.

[Überblick statistics](./statistics/README.adoc)

## ports

| Name              | port |
| ----------------- | ---- |
| authprovider      | 9000 |
| mja-api           | 9210 |
| checklistenserver | 9300 |
| mk-gateway        | 9510 |
| mk-kataloge       | 9530 |
| mkbiza-api        | 9540 |
| profil-server     | 9600 |