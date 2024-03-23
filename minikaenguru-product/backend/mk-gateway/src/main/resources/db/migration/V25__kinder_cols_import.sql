ALTER TABLE `KINDER` add COLUMN `KLASSENSTUFE_PRUEFEN` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'Flag 0/1  - 1, wenn Klassenstufe aus Importdatei nicht eindeutig ermittelt werden konnte';
ALTER TABLE `KINDER` add COLUMN `DUBLETTE_PRUEFEN` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'Flag 0/1  - 1, wenn die Importdatei Dubletten erzeugt hat';

