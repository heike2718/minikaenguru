CREATE TABLE TEILNAHMEN (
	`UUID` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL PRIMARY KEY COMMENT 'uuid',
	`TEILNAHMEART`varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
	`TEILNAHMENUMMER` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'Schulkürzel oder Teilnahmenummer eines privaten Veranstalters',
	`WETTBEWERB_UUID` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'uuid des Wettbewerbs zu dieser Teilnahme',
 	`VERSION` int(10) DEFAULT 0,
	`DATE_MODIFIED` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
