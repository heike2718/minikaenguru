CREATE TABLE `NEWSLETTERS` (
  `UUID` varchar(36)  NOT NULL,
  `BETREFF` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci  COMMENT 'Text, der als Betreff der Mail erscheinen soll',
  `TEXT` LONGTEXT CHARACTER SET utf8 COLLATE utf8_unicode_ci COMMENT 'Text, der im Mailbody erscheinen soll',
  `VERSION` int(10) NOT NULL DEFAULT 0,
  `DATE_MODIFIED` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`UUID`)
) ENGINE=InnoDB AUTO_INCREMENT=1530 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `VERSANDINFOS` (
  `UUID` varchar(36)  NOT NULL,
  `NEWSLETTER_UUID` varchar(36) CHARACTER SET utf8 COLLATE utf8_unicode_ci  NOT NULL COMMENT 'semantische Referenz eine Zeile von NEWSLETTERS',
  `EMPFAENGERTYP` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT 'enum Empfaengertyp value',
  `ANZAHL_EMPFAENGER` int(10) NOT NULL DEFAULT 0 COMMENT 'Anzahl der Empfänger beim gewählten EMPFAENGERTYP',
  `ANZAHL_VERSENDET` int(10) NOT NULL DEFAULT 0 COMMENT 'Anzahl der zu einem bestimmten Zeitpunkt tatsächlich versendeten Mails',
  `VERSAND_BEGONNEN_AM` datetime COMMENT 'Datum mit Uhrzeit, zu der der Versand begonnen hat',
  `VERSAND_BEENDET_AM` datetime COMMENT 'Datum mit Uhrzeit, zu der der Versand beendet war',
  `MIT_FEHLERN` tinyint(1) NOT NULL DEFAULT 0,
  `VERSION` int(10) NOT NULL DEFAULT 0,
  `DATE_MODIFIED` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`UUID`),
  UNIQUE KEY `uk_versandinfos_1` (`NEWSLETTER_UUID`,`EMPFAENGERTYP`),
  CONSTRAINT `fk_versandinfo_newsletter` FOREIGN KEY (`NEWSLETTER_UUID`) REFERENCES `NEWSLETTERS` (`UUID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1530 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `MUSTERTEXTE` (
  `UUID` varchar(36)  NOT NULL,
  `NAME` varchar(55) CHARACTER SET utf8 COLLATE utf8_unicode_ci  COMMENT 'Name des Mustertexts zur Auswahl und Anzeige',
  `TEXT` LONGTEXT CHARACTER SET utf8 COLLATE utf8_unicode_ci COMMENT 'Text, der in einen Newsletter übernommen werden kann',
  `VERSION` int(10) NOT NULL DEFAULT 0,
  `DATE_MODIFIED` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`UUID`)
) ENGINE=InnoDB AUTO_INCREMENT=1530 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


