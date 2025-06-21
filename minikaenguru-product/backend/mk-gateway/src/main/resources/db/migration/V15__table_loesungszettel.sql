CREATE TABLE `LOESUNGSZETTEL` (
  `UUID` varchar(36)  NOT NULL,
  `WETTBEWERB_UUID` varchar(36) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT 'Wettbewerbsjahr',
  `TEILNAHMEART` varchar(10) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT 'SCHULE oder PRIVAT',
  `TEILNAHMENUMMER` varchar(36) CHARACTER SET utf8 COLLATE utf8_unicode_ci  NOT NULL COMMENT 'semantische Referenz auf das kuerzel der zugehörigen Teilnahme',
  `KLASSENSTUFE` varchar(4) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `NUMMER` int(5) NOT NULL COMMENT 'Nummer des Teilnehmers innerhalb einer Klasse',
  `PUNKTE` int(4) NOT NULL DEFAULT 0 COMMENT 'Die erreichten Punkte als ganze Zahl (also mit 100 multipliziert)',
  `KAENGURUSPRUNG` int(2) NOT NULL DEFAULT 0 COMMENT 'Länge des Kängurusprungs',
  `LANDKUERZEL` varchar(5) CHARACTER SET utf8 COLLATE utf8_unicode_ci COMMENT 'Kürzel des (Bundes-)Landes',
  `SPRACHE` varchar(3) CHARACTER SET utf8 COLLATE utf8_unicode_ci COMMENT 'Kürzel für die Sprache der Aufgaben',
  `QUELLE` varchar(6) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT 'Quelle der Daten aus enum UPLOAD oder ONLINE',
  `NUTZEREINGABE` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci COMMENT 'das, was der Ersteller des Lösungszettels eingegeben hat, also ABCDEN oder frn',
  `ANTWORTCODE` varchar(15) CHARACTER SET utf8 COLLATE utf8_unicode_ci COMMENT 'lückenlose Aneinanderreihung aller vom Kind gesetzten Antwortbuchstaben A-E oder N. 15 für Klasse 2, 12 für Klasse 1',
  `WERTUNGSCODE` varchar(15) COLLATE utf8_unicode_ci NOT NULL COMMENT 'lückenlose Aneinanderreihung der Bewertung des Antwortcodes f,n oder r. 15 für Klasse 2, 12 für Klasse 1, 6 für IKids',
  `TYPO` int(1) NOT NULL DEFAULT 0 COMMENT 'Flag, ob dieser Lösungszettel einen Typo enthielt',
  `VERSION` int(10) NOT NULL DEFAULT 0,
  `DATE_MODIFIED` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`UUID`),
  UNIQUE KEY `uk_loesungszettel_1` (`TEILNAHMEART`,`WETTBEWERB_UUID`,`TEILNAHMENUMMER`,`KLASSENSTUFE`,`NUMMER`)
) ENGINE=InnoDB AUTO_INCREMENT=1530 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



CREATE TABLE `TEMP_LOESUNGSZETTEL` (
  `VERSION` int(10) NOT NULL DEFAULT 0,
  `DATE_MODIFIED` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `KUERZEL` varchar(22) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `NUMMER` int(5) NOT NULL,
  `KLASSENSTUFE` varchar(4) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `TEILNAHMEKUERZEL` varchar(36) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT 'semantische Referenz auf das kuerzel der zugehörigen Teilnahme',
  `TEILNAHMENUMMER_NEU` varchar(36) CHARACTER SET utf8 COLLATE utf8_unicode_ci COMMENT 'bei Privatteilnahmen gibt es ein Mapping der alten auf die neue Teilnahmenummer',
  `QUELLE` varchar(6) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT 'Quelle der Daten aus enum UPLOAD oder ONLINE',
  `TEILNAHMEART` varchar(10) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT 'S(schule) oder P(rivat)',
  `JAHR` varchar(36) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT 'Wettbewerbsjahr',
  `LANDKUERZEL` varchar(5) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL COMMENT 'Kürzel des (Bundes-)Landes',
  `KAENGURUSPRUNG` int(2) NOT NULL COMMENT 'Länge des Kängurusprungs',
  `TYPO` int(1) NOT NULL DEFAULT 0 COMMENT 'Flag, ob dieser Lösungszettel einen Typo enthielt',
  `ORIGINALWERTUNG` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci COMMENT 'Die Wertungen kommasepariert aus der Excel-Tabelle',
  `PUNKTE` int(4) DEFAULT NULL COMMENT 'Die erreichten Punkte als ganze Zahl (also mit 100 multipliziert)',
  `ANTWORTCODE` varchar(15)  CHARACTER SET utf8 COLLATE utf8_unicode_ci COMMENT 'lückenlose Aneinanderreihung aller vom Kind gesetzten Antwortbuchstaben A-E oder N. 15 für Klasse 2, 12 für Klasse 1',
  `WERTUNGSCODE` varchar(15) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT 'lückenlose Aneinanderreihung der Bewertung des Antwortcodes f,n oder r. 15 für Klasse 2, 12 für Klasse 1',
  `SPRACHE` varchar(3) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Kürzel für die Sprache der Aufgaben',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1530 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;




