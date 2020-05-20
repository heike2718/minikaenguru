-- MariaDB dump 10.17  Distrib 10.4.13-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: mk_wettbewerb
-- ------------------------------------------------------
-- Server version	10.4.13-MariaDB-1:10.4.13+maria~stretch-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `mk_wettbewerb`
--

/*!40000 DROP DATABASE IF EXISTS `mk_wettbewerb`*/;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `mk_wettbewerb` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `mk_wettbewerb`;

--
-- Table structure for table `EVENTS`
--

DROP TABLE IF EXISTS `EVENTS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `EVENTS` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `DATE_MODIFIED` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `NAME` varchar(100) CHARACTER SET utf8 NOT NULL,
  `TIME_OCCURED` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `BODY` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`BODY`)),
  `VERSION` int(10) DEFAULT 0,
  PRIMARY KEY (`ID`),
  CONSTRAINT `CONSTRAINT_1` CHECK (json_valid(`BODY`))
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='speichert events aus der domain';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SCHULKOLLEGIEN`
--

DROP TABLE IF EXISTS `SCHULKOLLEGIEN`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SCHULKOLLEGIEN` (
  `UUID` varchar(36) CHARACTER SET utf8 NOT NULL COMMENT 'schulkuerzel',
  `KOLLEGIUM` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'Lehrer mit uuid und fullName' CHECK (json_valid(`KOLLEGIUM`)),
  `VERSION` int(10) DEFAULT 0,
  `DATE_MODIFIED` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`UUID`),
  CONSTRAINT `CONSTRAINT_1` CHECK (json_valid(`KOLLEGIUM`))
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `TEILNAHMEN`
--

DROP TABLE IF EXISTS `TEILNAHMEN`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TEILNAHMEN` (
  `UUID` varchar(36) CHARACTER SET utf8 NOT NULL COMMENT 'uuid',
  `TEILNAHMEART` varchar(10) CHARACTER SET utf8 NOT NULL,
  `TEILNAHMENUMMER` varchar(10) CHARACTER SET utf8 NOT NULL,
  `WETTBEWERB_UUID` varchar(36) CHARACTER SET utf8 NOT NULL COMMENT 'uuid des Wettbewerbs zu dieser Teilnahme',
  `VERSION` int(10) DEFAULT 0,
  `DATE_MODIFIED` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `SCHULNAME` varchar(100) CHARACTER SET utf8 DEFAULT NULL COMMENT 'Name der Schule für die Urkunde',
  `ANGEMELDET_DURCH` varchar(36) CHARACTER SET utf8 DEFAULT NULL COMMENT 'UUID des Veranstalters',
  PRIMARY KEY (`UUID`),
  UNIQUE KEY `UK_TEILNAHMEN_1` (`TEILNAHMEART`,`TEILNAHMENUMMER`,`WETTBEWERB_UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `VERANSTALTER`
--

DROP TABLE IF EXISTS `VERANSTALTER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `VERANSTALTER` (
  `UUID` varchar(36) CHARACTER SET utf8 NOT NULL COMMENT 'uuid des Benutzerkontos im authprovider',
  `ROLLE` varchar(10) COLLATE utf8_unicode_ci NOT NULL,
  `FULL_NAME` varchar(101) COLLATE utf8_unicode_ci NOT NULL,
  `TEILNAHMENUMMERN` varchar(1000) CHARACTER SET utf8 DEFAULT NULL,
  `VERSION` int(10) DEFAULT 0,
  `DATE_MODIFIED` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `ZUGANG_UNTERLAGEN` varchar(10) CHARACTER SET utf8 NOT NULL DEFAULT 'DEFAULT',
  PRIMARY KEY (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `WETTBEWERBE`
--

DROP TABLE IF EXISTS `WETTBEWERBE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `WETTBEWERBE` (
  `UUID` varchar(36) CHARACTER SET utf8 NOT NULL COMMENT 'uuid Wettbewerbsjahr',
  `STATUS` varchar(20) CHARACTER SET utf8 NOT NULL,
  `BEGINN` date DEFAULT NULL COMMENT 'Datum des Wettbewerbsbeginns, also des Beginns des Anmeldezeitraums',
  `ENDE` date DEFAULT NULL COMMENT 'Datum des Wettbewerbsendes, also der Vernichtung der personenbezogenen Wettbewerbsdaten',
  `FREISCHALTUNG_LEHRER` date DEFAULT NULL COMMENT 'Datum der Freischaltung der Unterlagen für die Lehrer',
  `FREISCHALTUNG_PRIVAT` date DEFAULT NULL COMMENT 'Datum der Freischaltung der Unterlagen für die Privatpersonen',
  `VERSION` int(10) DEFAULT 0,
  `DATE_MODIFIED` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `schema_version`
--

DROP TABLE IF EXISTS `schema_version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `schema_version` (
  `installed_rank` int(11) NOT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int(11) DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT current_timestamp(),
  `execution_time` int(11) NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `schema_version_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-05-19  6:23:16
