# mk-kataloge project

Stellt die für Minikänguru erforderlichen Schulen zur Verfügung.

__Migration des Schulkatalogs nach mk-kataloge__

Erstbefüllung erfolgt aus dem alten mkvadmin-Service als POST request zu

http://localhost:9700/mk-kataloge-api/schulen

mit SchuleMessage als payload.

Auf der Client-Seite (mkvadmin) können Schulen schrittweise exportiert werden, indem sie nach
dem ersten Zeichen ihres Kürzels gruppiert werden.

POST als authentisierter ADMIN:

https://mathe-jung-alt.de/admin-app/schulen/{firstLetter}/export
