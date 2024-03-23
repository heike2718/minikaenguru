CREATE TABLE mk_kataloge.PACEMAKERS (
    `UUID` varchar(36)  NOT NULL,
	`WERT` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
	`VERSION` int(10) DEFAULT 0,
	`DATE_MODIFIED` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	PRIMARY KEY (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='registrierte herzschrittmacher für mk-kataloge';

INSERT INTO mk_kataloge.PACEMAKERS (UUID, WERT) VALUES ('mk-kataloge-database','erster');


