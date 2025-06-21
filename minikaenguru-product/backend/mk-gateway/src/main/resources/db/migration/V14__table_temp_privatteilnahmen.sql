CREATE TABLE TEMP_PRIVATTEILNAHMEN (
	`ID` INT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT,
	`VERANSTALTER_UUID` varchar(36) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT 'uuid des Veranstalters',
	`TEILNAHMENUMMER_ALT` varchar(36) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT 'kuerzel der Privatteilnahme in mkverwaltung',
	`TEILNAHMENUMMER_NEU` varchar(36) CHARACTER SET utf8 COLLATE utf8_unicode_ci COMMENT 'Teilnahmenummer, auf die die alte gemapped wurde',
	`WETTBEWERB_UUID` varchar(36) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT 'uuid des Wettbewerbs zu dieser Teilnahme',
 	`VERSION` int(10) DEFAULT 0,
	`DATE_MODIFIED` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

alter table TEILNAHMEN modify UUID varchar(36) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;
alter table TEILNAHMEN modify TEILNAHMEART varchar(10) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;
alter table TEILNAHMEN modify TEILNAHMENUMMER varchar(36) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;
alter table TEILNAHMEN modify WETTBEWERB_UUID varchar(36) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;

alter table WETTBEWERBE modify UUID varchar(36) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;
alter table WETTBEWERBE modify STATUS varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;

alter table VERANSTALTER modify UUID varchar(36) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;
alter table VERANSTALTER modify ROLLE varchar(10) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;
alter table VERANSTALTER modify FULL_NAME varchar(101) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;

alter table SCHULKOLLEGIEN modify UUID varchar(36) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;