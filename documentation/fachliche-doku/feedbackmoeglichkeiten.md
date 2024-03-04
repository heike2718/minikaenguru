# Funktion zum Sammeln von Feedback über einen Wettbewerb

## Welche Informationen benötige ich?

### Je Aufgabe

1. __SCHWIERIGKEITSGRAD:__ War die Aufgabe für die Kategorie A/B/C viel zu leicht, zu leicht, genau richtig, zu schwer, viel zu schwer? Hinweis: Die Aufgaben der Kategorie A sollen von vielen der Kinder richtig gelöst werden können. Die Aufgaben der Kategorie B sollen ungefähr von der Hälfte der Kinder richtig gelöst werden können. Die Aufgaben der Kategorie C sollen von den besten Kindern der Klasse gelöst werden können.
2. __KATEGORIE:__ Welche Kategorie (A/B/C) hätten Sie dieser Aufgabe gegeben? A, B, C
3. __LEHRPLAN:__ Entsprach die Aufgabe dem Lehrplaninhalt? ja/nein
4. __VERSTAENDLICHKEIT:__ War der Text der Aufgabe für die Kinder gut verständlich?  ja/nein


### Je Klassenstufe

1. __SPASS:__ Wie viel Spaß hatten die Kinder an diesem Wettbewerb?
2. __PERSOENLICHE_ZUFRIEDENHEIT:__ Wie zufrieden waren Sie persönlich insgesamt mit den Wettbewerbsaufgaben?


Score soll nach Ländern aggregiert werden können.


### Tabelle SCORES_AUFGABEN

Je Bewertung einer Aufgabe genau eine Zeile:

| UUID  | JAHR | KLASSENSTUFE | AUFGABE_NR | LAND_KUERZEL | SCHWIERIGKEITSGRAD | KATEGORIE | LEHRPLAN | VERSTAENDLICHKEIT | FREITEXT   |
| ----- | ---- | ------------ | ---------- | ------------ | ------------------ | --------- | -------- | ----------------- | ---------- |
| abcde | 2024 | ZWEI         | A-1        | TH           | 4                  | B         | 1        | 1                 | blabla bla |

### Tabelle SCORES_KLASSENSTUFEN

Je Gesamtbewertung einer Klassenstufe genau eine Zeile:

| UUID  | JAHR | KLASSENSTUFE | LAND_KUERZEL | SPASS | ZUFRIEDENHEIT | FREITEXT   |
| ----- | ---- | ------------ | ------------ | ----- | ------------- | ---------- |
| abcde | 2024 | ZWEI         | TH           | 3     | 4             | blabla bla |

## Wann soll LehrerAPIDto neu geladen werden, um den Status bewertungsfragebogenAnzeigen zu aktualisieren?

Nur bei Lehrern!

+ Auswertung Schule erstellen - neuer Button zum Bewertungsfragebogen sollte ständig sichtbar sein.
+ Auswertungen hochladen
+ Lösungszettel speichern
+ Lösungszettel löschen
+ Klasse löschen
+ alle Klassen löschen


# Kontaktformular für Anregungen, Fragen, Kritik

Soll Freitext ohne <>'" enthalten können und per Mail an mich gesendet werden. Will damit nicht die DB zumüllen.

