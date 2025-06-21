CREATE TABLE WETTBEWERBE (
	`UUID` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL PRIMARY KEY COMMENT 'uuid Wettbewerbsjahr',
	`STATUS`varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
    `BEGINN` date COMMENT 'Datum des Wettbewerbsbeginns, also des Beginns des Anmeldezeitraums',
    `ENDE` date COMMENT 'Datum des Wettbewerbsendes, also der Vernichtung der personenbezogenen Wettbewerbsdaten',
    `FREISCHALTUNG_LEHRER` date COMMENT 'Datum der Freischaltung der Unterlagen für die Lehrer',
    `FREISCHALTUNG_PRIVAT` date COMMENT 'Datum der Freischaltung der Unterlagen für die Privatpersonen',
	`VERSION` int(10) DEFAULT 0,
	`DATE_MODIFIED` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
