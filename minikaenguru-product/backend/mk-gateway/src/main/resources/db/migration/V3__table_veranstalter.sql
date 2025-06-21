CREATE TABLE VERANSTALTER (
	`UUID` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL PRIMARY KEY COMMENT 'uuid des Benutzerkontos im authprovider',
    `ROLLE` varchar(10) COLLATE utf8_unicode_ci NOT NULL ,
    `FULL_NAME` varchar(101) COLLATE utf8_unicode_ci NOT NULL ,
    `TEILNAHMEKUERZEL` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT 'Kommaseparierte Liste der Kürzel der Schulen oder Privatteilnahmen',
	`VERSION` int(10) DEFAULT 0,
	`DATE_MODIFIED` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
