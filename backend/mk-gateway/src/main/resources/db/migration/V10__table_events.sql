-- da mariadb den ersten timestamp in der column list automatisch mit den defaults versieht, dies bei TIME_OCCURED aber
-- blöd ist, spendieren wir eine weitere column DATE_MODIFIED

CREATE TABLE EVENTS (
	`ID` INT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT,
	`DATE_MODIFIED` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  	`NAME` varchar(100) CHARACTER SET utf8 NOT NULL,
	`TIME_OCCURED` timestamp NOT NULL,
  	`BODY` JSON,
	`VERSION` int(10) DEFAULT 0,
	CHECK (JSON_VALID(BODY))
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'speichert events aus der domain';

