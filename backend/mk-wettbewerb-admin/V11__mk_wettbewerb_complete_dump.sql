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
-- Dumping data for table `EVENTS`
--

LOCK TABLES `EVENTS` WRITE;
/*!40000 ALTER TABLE `EVENTS` DISABLE KEYS */;
/*!40000 ALTER TABLE `EVENTS` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `SCHULKOLLEGIEN`
--

LOCK TABLES `SCHULKOLLEGIEN` WRITE;
/*!40000 ALTER TABLE `SCHULKOLLEGIEN` DISABLE KEYS */;
INSERT INTO `SCHULKOLLEGIEN` VALUES ('6XOA2A11','[{\"uuid\":\"2f09da36-07c6-4033-a2f1-5e110c804026\",\"fullName\":\"Tolle Lehrerin\"}]',0,'2020-05-15 19:15:39'),('EEGEECP6','[{\"uuid\":\"24f4c507-454a-4cb1-bc0c-4da5df4a4a1b\",\"fullName\":\"event lehrer test eins\"},{\"uuid\":\"4e4ce045-cd02-4fbd-93cb-00d9f559f8e1\",\"fullName\":\"event lehrer Zwei\"},{\"uuid\": \"2f09da36-07c6-4033-a2f1-5e110c804026\",\"fullName\":\"Tolle Lehrerin\"}]',1,'2020-05-20 07:04:39'),('JPTM15A5','[{\"uuid\":\"724ab242-76b4-49e1-89b1-80c098ce58cd\",\"fullName\":\"Lehrerin Hanau\"}]',0,'2020-05-16 08:01:50'),('ZZO6ZF1V','[{\"uuid\":\"2f09da36-07c6-4033-a2f1-5e110c804026\",\"fullName\":\"Tolle Lehrerin\"}]',0,'2020-05-20 07:06:39');
/*!40000 ALTER TABLE `SCHULKOLLEGIEN` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `TEILNAHMEN`
--

LOCK TABLES `TEILNAHMEN` WRITE;
/*!40000 ALTER TABLE `TEILNAHMEN` DISABLE KEYS */;
/*!40000 ALTER TABLE `TEILNAHMEN` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `VERANSTALTER`
--

LOCK TABLES `VERANSTALTER` WRITE;
/*!40000 ALTER TABLE `VERANSTALTER` DISABLE KEYS */;
INSERT INTO `VERANSTALTER` VALUES ('07e89a7f-06f3-4e07-93d7-f8293d6c4256','PRIVAT','event-privat test-sechs','293D6C4256',0,'2020-05-17 19:57:52','DEFAULT'),('0f82f648-283d-4e23-90d4-579f2d78cd68','PRIVAT','regress ionstest','9F2D78CD68',0,'2020-05-17 19:57:52','DEFAULT'),('24f4c507-454a-4cb1-bc0c-4da5df4a4a1b','LEHRER','event lehrer test eins','EEGEECP6',0,'2020-05-15 19:14:23','DEFAULT'),('2cdf18eb-dcd7-4e9d-8091-0e6340db0f11','PRIVAT','event-privat test-eins','6340DB0F11',0,'2020-05-17 19:57:52','DEFAULT'),('2f09da36-07c6-4033-a2f1-5e110c804026','LEHRER','Tolle Lehrerin','6XOA2A11,EEGEECP6,ZZO6ZF1V',0,'2020-05-20 06:59:16','DEFAULT'),('444c50cc-4d57-47dc-9dbf-579663fac8e9','PRIVAT','event-privat test-drei','9663FAC8E9',0,'2020-05-17 19:57:52','DEFAULT'),('4e4ce045-cd02-4fbd-93cb-00d9f559f8e1','LEHRER','event lehrer Zwei','EEGEECP6',0,'2020-05-15 19:15:12','DEFAULT'),('503034a5-155e-40d3-aca4-591dfaea4dc9','PRIVAT','event-privat test-vier','1DFAEA4DC9',0,'2020-05-17 19:57:52','DEFAULT'),('724ab242-76b4-49e1-89b1-80c098ce58cd','LEHRER','Lehrerin Hanau','JPTM15A5',0,'2020-05-16 08:01:51','DEFAULT'),('82d7f4b0-1c2e-4d1d-b661-86f92a3fb820','PRIVAT','privat event Zwei','F92A3FB820',0,'2020-05-17 19:57:52','DEFAULT'),('8efc761c-83e7-42e3-bd2c-ba29b0db80dc','PRIVAT','Gretel von Hänsel','29B0DB80DC',0,'2020-05-17 19:57:52','DEFAULT'),('9bfa4dc8-6251-44d4-87ab-72784123c1e7','PRIVAT','privat-event Eins','784123C1E7',0,'2020-05-17 19:57:52','DEFAULT'),('d0cafd8d-542a-4b0e-a2d9-82bc8f9618cc','PRIVAT','event-privat test-fünf','BC8F9618CC',0,'2020-05-17 19:57:52','DEFAULT'),('dd97e1bf-f52a-4429-9443-0de9d96dac37','PRIVAT','privat event drei','E9D96DAC37',0,'2020-05-17 19:57:52','DEFAULT');
/*!40000 ALTER TABLE `VERANSTALTER` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `WETTBEWERBE`
--

LOCK TABLES `WETTBEWERBE` WRITE;
/*!40000 ALTER TABLE `WETTBEWERBE` DISABLE KEYS */;
/*!40000 ALTER TABLE `WETTBEWERBE` ENABLE KEYS */;
UNLOCK TABLES;

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

--
-- Dumping data for table `schema_version`
--

LOCK TABLES `schema_version` WRITE;
/*!40000 ALTER TABLE `schema_version` DISABLE KEYS */;
INSERT INTO `schema_version` VALUES (1,'1','<< Flyway Baseline >>','BASELINE','<< Flyway Baseline >>',NULL,'mk_wettbewerb','2020-04-12 11:44:20',0,1),(2,'2','table schulkollegien','SQL','V2__table_schulkollegien.sql',2095858003,'mk_wettbewerb','2020-04-12 11:51:14',18,1),(3,'3','table veranstalter','SQL','V3__table_veranstalter.sql',804445355,'mk_wettbewerb','2020-04-19 07:55:14',21,1),(4,'4','table wettbewerbe','SQL','V4__table_wettbewerbe.sql',1458260375,'mk_wettbewerb','2020-05-15 15:15:24',32,1),(5,'5','veranstalter rename column','SQL','V5__veranstalter_rename_column.sql',2042634501,'mk_wettbewerb','2020-05-15 15:15:24',11,1),(6,'6','veranstalter col zugang unterlagen','SQL','V6__veranstalter_col_zugang_unterlagen.sql',1046156931,'mk_wettbewerb','2020-05-15 19:47:43',16,1),(7,'7','table teilnahmen','SQL','V7__table_teilnahmen.sql',1292228015,'mk_wettbewerb','2020-05-15 20:05:01',22,1),(8,'8','teilnahmen unique key','SQL','V8__teilnahmen_unique_key.sql',-1965186420,'mk_wettbewerb','2020-05-16 04:01:33',32,1),(9,'9','teilnahmen col schulname','SQL','V9__teilnahmen_col_schulname.sql',-514522398,'mk_wettbewerb','2020-05-16 04:55:15',20,1),(10,'10','table events','SQL','V10__table_events.sql',161961137,'mk_wettbewerb','2020-05-16 07:18:03',24,1),(11,'11','teilnahmen col angemeldet durch','SQL','V11__teilnahmen_col_angemeldet_durch.sql',-1401978540,'mk_wettbewerb','2020-05-19 04:23:09',68,1);
/*!40000 ALTER TABLE `schema_version` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-05-21  8:44:40