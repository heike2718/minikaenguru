CREATE TABLE `UPLOADS` (
  `UUID` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'technische ID',
  `BENUTZER_UUID` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'uuid des Benutzers, der die Datei hochgeladen hat',
  `TEILNAHMENUMMER` varchar(36) COLLATE utf8_unicode_ci NOT NULL COMMENT 'semantische Referenz auf das kuerzel der zugehörigen Teilnahme (Schule)',
  `DATEINAME` varchar(150) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Name, die der Datei durch den Lehrer gegeben wurde',
  `UPLOAD_TYPE` varchar(15) COLLATE utf8_unicode_ci NOT NULL COMMENT 'AUSWERTUNG oder KLASSENLISTE',
  `CHARSET` varchar(15) COLLATE utf8_unicode_ci COMMENT 'falls es sich ermitteln ließ, dann das Charset der hochgeladenen Datei, sonst null',
  `STATUS` varchar(15) COLLATE utf8_unicode_ci NOT NULL DEFAULT 'HOCHGELADEN' COMMENT 'UploadStatus der Datei: HOCHGELADEN, IMPORTIERT, LEER, FEHLER',
  `MEDIATYPE` varchar(200) COLLATE utf8_unicode_ci NOT NULL COMMENT 'durch TIKA ermittelter MediaType',
  `CHECKSUMME` BIGINT NOT NULL COMMENT 'Checksumme, um Doppeluploads zu vermeiden',
  `DATE_UPLOAD` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `VERSION` int(10) NOT NULL DEFAULT 0,
  PRIMARY KEY (`UUID`),
  UNIQUE KEY `uk_uploads_1` (`BENUTZER_UUID`,`CHECKSUMME`,`TEILNAHMENUMMER`)
) ENGINE=InnoDB AUTO_INCREMENT=282 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci
