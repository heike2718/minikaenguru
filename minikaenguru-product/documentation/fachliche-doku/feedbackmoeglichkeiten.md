# Funktion zum Sammeln von Feedback über einen Wettbewerb

## Welche Informationen benötige ich?

### Je Aufgabe

1. __SCHWIERIGKEITSGRAD:__ War die Aufgabe für die Kategorie A/B/C viel zu leicht, zu leicht, genau richtig, zu schwer, viel zu schwer? Hinweis: Die Aufgaben der Kategorie A sollen von vielen der Kinder richtig gelöst werden können. Die Aufgaben der Kategorie B sollen ungefähr von der Hälfte der Kinder richtig gelöst werden können. Die Aufgaben der Kategorie C sollen von den besten Kindern der Klasse richtig gelöst werden können.
2. __KATEGORIE:__ Welche Kategorie (A/B/C) hätten Sie dieser Aufgabe gegeben? A, B, C  (N = nicht bewertet)
3. __LEHRPLAN:__ Entsprach die Aufgabe dem Lehrplaninhalt? 3 Werte: -1 = nein, 0 = nicht bewertet, 1 = ja
4. __VERSTAENDLICHKEIT:__ War der Text der Aufgabe für die Kinder gut verständlich?  3 Werte: -1 = nein, 0 = nicht bewertet, 1 = ja

### Je Klassenstufe

1. __SPASS:__ Wie viel Spaß hatten die Kinder an diesem Wettbewerb?  6 Werte: 0 = nicht bewertet, dann aufsteigend nach Maß des Spaßes
2. __PERSOENLICHE_ZUFRIEDENHEIT:__ Wie zufrieden waren Sie persönlich insgesamt mit den Wettbewerbsaufgaben?  6 Werte: 0 = nicht bewertet, dann aufsteigend nach Maß der Zufriedenheit


Score soll nach Ländern aggregiert werden können. Bei Lehrern, die in mehreren Ländern den Wettbewerb veranstalten, wird ein beliegiges Land ihrer Schulen genommen.


### Tabelle SCORES_AUFGABEN

Je Bewertung einer Aufgabe genau eine Zeile:

| UUID  | JAHR | KLASSENSTUFE | AUFGABE_NR | LANDKUERZEL | SCORE_SCHWIERIGKEITSGRAD | SCORE_KATEGORIE | SCORE_LEHRPLAN | SCORE_VERSTAENDLICHKEIT | FREITEXT   |
| ----- | ---- | ------------ | ---------- | ----------- | ------------------------ | --------------- | -------------- | ----------------------- | ---------- |
| abcde | 2024 | ZWEI         | A-1        | TH          | 4                        | B               | 1              | 1                       | blabla bla |

### Tabelle SCORES_KLASSENSTUFEN

Je Gesamtbewertung einer Klassenstufe genau eine Zeile:

| UUID  | JAHR | KLASSENSTUFE | LANDKUERZEL | SCORE_SPASS | SCORE_ZUFRIEDENHEIT | FREITEXT   |
| ----- | ---- | ------------ | ----------- | ----------- | ------------------- | ---------- |
| abcde | 2024 | ZWEI         | TH          | 3           | 4                   | blabla bla |

## Wann soll LehrerAPIModel neu geladen werden, um den Status bewertungsfragebogenAnzeigen zu aktualisieren?

+ Auswertung Schule erstellen - neuer Button zum Bewertungsfragebogen sollte ständig sichtbar sein.
+ Auswertungen hochladen
+ Lösungszettel speichern
+ Lösungszettel löschen
+ Klasse löschen
+ alle Klassen löschen

## Wie erfolgt die Auswertung?

Je Kategorie wird aggregiert. Nur die Zeilen werden berücksichtigt, bei denen die Bewertung nicht 0 oder N ist.

Aufgabenkategorie: man könnte den Abstand zwischen tatsächlicher undempfohlener Kategorie berechnen. Aufgaben, bei denen der durchschnittliche Abstand negativ ist, waren tendenziell zu schwer, solche, bei denen er positiv ist, tendenziell zu leicht.

Bei den Kategorien LEHRPLAN und VERSTÄNDLICHKEIT signalisiert ein negativer Durchschnitt, dass Verbesserungsbedarf besteht, ein positiver, dass alles OK war. 


# Kontaktformular für Anregungen, Fragen, Kritik

Soll Freitext ohne <>'" enthalten können und per Mail an mich gesendet werden. Will damit nicht die DB zumüllen.

