CREATE TABLE `KLASSEN` (
  `UUID` varchar(36)  NOT NULL,
  `SCHULKUERZEL` varchar(36) CHARACTER SET utf8 COLLATE utf8_unicode_ci  NOT NULL COMMENT 'semantische Referenz auf das kuerzel der zugehörigen Schule',
  `NAME` varchar(55) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT 'Name der Klasse für die Urkunde',
  `VERSION` int(10) NOT NULL DEFAULT 0,
  `DATE_MODIFIED` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`UUID`),
  UNIQUE KEY `uk_klassen_1` (`SCHULKUERZEL`,`NAME`)
) ENGINE=InnoDB AUTO_INCREMENT=1530 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `KINDER` (
  `UUID` varchar(36)  NOT NULL,
  `TEILNAHMEART` varchar(10) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT 'SCHULE oder PRIVAT',
  `TEILNAHMENUMMER` varchar(36) CHARACTER SET utf8 COLLATE utf8_unicode_ci  NOT NULL COMMENT 'semantische Referenz auf das kuerzel der zugehörigen Teilnahme',
  `KLASSENSTUFE` varchar(4) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `SPRACHE` varchar(3) CHARACTER SET utf8 COLLATE utf8_unicode_ci COMMENT 'Kürzel für die Sprache der Aufgaben',
  `VORNAME` varchar(55) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT 'der Vorname des Kindes',
  `NACHNAME` varchar(55) CHARACTER SET utf8 COLLATE utf8_unicode_ci COMMENT 'der optionale Nachname des Kindes',
  `ZUSATZ` varchar(55) CHARACTER SET utf8 COLLATE utf8_unicode_ci COMMENT 'die optionale Zusatzangabe zum Kind',
  `KLASSE_UUID` varchar(36) COMMENT 'Referenz auf KLASSEN, falls es ein Schulkind ist',
  `LOESUNGSZETTEL_UUID` varchar(36) COMMENT 'Referenz auf LOESUNGSZETTEL',
  `VERSION` int(10) NOT NULL DEFAULT 0,
  `DATE_MODIFIED` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`UUID`)
) ENGINE=InnoDB AUTO_INCREMENT=1530 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


ALTER TABLE `LOESUNGSZETTEL` ADD `KIND_ID` varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT 'solange es Einträge in KINDER gibt, sind es die ersten 8 Zeichen der UUID eines Kindes, sonst einfach eine Zeichenkette fuer den uk_loesungszettel_1';

UPDATE `LOESUNGSZETTEL` SET KIND_ID = SUBSTRING(UUID(), 1, 8);

ALTER TABLE `LOESUNGSZETTEL` DROP INDEX uk_loesungszettel_1;

ALTER TABLE `LOESUNGSZETTEL` DROP COLUMN `NUMMER`;

CREATE UNIQUE INDEX `uk_loesungszettel_1` ON LOESUNGSZETTEL(`TEILNAHMEART`,`WETTBEWERB_UUID`,`TEILNAHMENUMMER`,`KLASSENSTUFE`,`KIND_ID`);


