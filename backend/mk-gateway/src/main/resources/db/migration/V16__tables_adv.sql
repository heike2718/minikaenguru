CREATE TABLE `mk_wettbewerb`.`VERTRAEGE_ADV_TEXTE` (
  `UUID` varchar(36)  NOT NULL,
  `VERSIONSNUMMER` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `DATEINAME` varchar(150) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `CHECKSUMME` varchar(128) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `VERSION` int(10) NOT NULL DEFAULT 0,
  `DATE_MODIFIED` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`UUID`),
  UNIQUE KEY `UK_VERTRAG_ADV_TEXTE_1` (`VERSIONSNUMMER`),
  UNIQUE KEY `UK_VERTRAG_ADV_TEXTE_2` (`DATEINAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `mk_wettbewerb`.`VERTRAEGE_ADV` (
  `UUID` varchar(36)  NOT NULL,
  `VERTRAG_ADV_TEXT_UUID` varchar(36) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT 'Referenz auf die Vertragstext-Metadaten',
  `SCHULKUERZEL` varchar(36) CHARACTER SET utf8 COLLATE utf8_unicode_ci  NOT NULL COMMENT 'semantische Referenz auf das kuerzel der Schule',
  `SCHULNAME` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `STRASSE` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `HAUSNR` varchar(10) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `PLZ` varchar(10) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `ORT` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `LAENDERCODE` varchar(2) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `ABGESCHLOSSEN_AM` varchar(19) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `ABGESCHLOSSEN_DURCH` varchar(36) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `VERSION` int(10) NOT NULL DEFAULT 0,
  `DATE_MODIFIED` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`UUID`),
  UNIQUE KEY `UK_VERTRAG_ADV_1` (`SCHULKUERZEL`),
  CONSTRAINT `FK_VERTRAG_ADV_TEXT` FOREIGN KEY (`VERTRAG_ADV_TEXT_UUID`) REFERENCES `VERTRAEGE_ADV_TEXTE` (`UUID`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

