CREATE TABLE `DOWNLOADS` (
  `UUID` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'technische ID',
  `VERANSTALTER_UUID` varchar(36) COLLATE utf8_unicode_ci NOT NULL COMMENT 'uuid des Veranstalters, der die Datei heruntergeladen hat',
  `JAHR` int(4) NOT NULL COMMENT 'das Wettbewerbsjahr',
  `DOWNLOAD_TYPE` varchar(30) COLLATE utf8_unicode_ci NOT NULL COMMENT 'aus enum Downloadtyp - verweist auf das heruntergeladene File',
  `ANZAHL` int(4) NOT NULL DEFAULT 0 COMMENT 'Anzahl der Downloads für dieses File',
  `DATE_DOWNLOAD` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `SORTNR` bigint(20) NOT NULL COMMENT 'Sortierung für zuverlässuge Reihenfolge beim Lesen',
  `VERSION` int(10) NOT NULL DEFAULT 0,
  PRIMARY KEY (`UUID`),
  UNIQUE KEY `uk_downloads_1` (`VERANSTALTER_UUID`,`JAHR`,`DOWNLOAD_TYPE`)
) ENGINE=InnoDB AUTO_INCREMENT=282 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE OR REPLACE VIEW VW_DOWNLOADS AS
SELECT d.SORTNR,
    d.UUID,
	d.VERANSTALTER_UUID,
	d.JAHR,
	d.DOWNLOAD_TYPE,
	d.ANZAHL,
	d.DATE_DOWNLOAD,
	v.FULL_NAME,
	v.EMAIL
FROM DOWNLOADS d, VERANSTALTER v
WHERE d.VERANSTALTER_UUID = v.UUID
ORDER BY d.SORTNR;
