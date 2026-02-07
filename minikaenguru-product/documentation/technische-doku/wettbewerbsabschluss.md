# Arbeiten zum Wettbewerbsabschluss

## MK-ADMIN

+ Mediane des Wettbewerbs eintragen, solange er nicht beendet ist
+ Wettbewerb beenden
+ neuen Wettbewerb für das kommende Jahr erfassen
+ Meldung schalten: Der Wettbewerb XXXX ist beendet. Falls Sie den Wettbewerb selbst ausgewertet und die Excel- Tabelle noch nicht hochgeladen haben, können Sie mir per Mail zusenden. Ich lade sie gern für Sie hoch. 

Dies führt dazu, dass beim Einloggen in die Minikänguru-App keine Auswertung für das aktuelle Jahr mehr möglich ist.

## Rätselbaukasten

+ Aufgabensammlung des Wettbewerbsjahrs sowie alle zugehörigen Aufgaben freischalten
  
Dies führt dazu, dass in der Statistik die Aufgaben nebst Prozentzahlen angezeigt werden. War sehr mühsam. Unbedingt "Aufgabensammlung mit allen Aufgaben gemeinsam freischalten" implementieren. Oder wahlweise: Aufgabensammlung lässt sich nur freischalten, wenn auch alle Aufgaben freigeschaltet sind.

## Datenbank

+ Tabellen KINDER, KLASSEN und UPPLOADS leeren
+ Falls das neue Wettbewerbsjahr 2041 ist, müssen in mk_wettbewerb.FARBEN_WETTBEWERBE neue Farben für die Statistik eingetragen werden.

```
delete from EVENTS where NAME in ('KindChanged', 'KlasseDeleted', 'KlasseCreated', 'KindCreated', 'KlasseChanged', 'KindDeleted', 'LoesungszettelCreated', 'LoesungszettelDeleted', 'LoesungszettelChanged');

## static Webcontent

+ index.html aktualisieren
+ archiv.html: Aufgabenlinks für aktuelles Jahr

