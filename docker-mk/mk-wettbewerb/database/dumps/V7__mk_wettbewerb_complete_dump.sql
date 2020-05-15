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
INSERT INTO `SCHULKOLLEGIEN` VALUES ('6XOA2A11','[{\"uuid\":\"2f09da36-07c6-4033-a2f1-5e110c804026\",\"fullName\":\"Tolle Lehrerin\"}]',0,'2020-05-15 19:15:39'),('EEGEECP6','[{\"uuid\":\"24f4c507-454a-4cb1-bc0c-4da5df4a4a1b\",\"fullName\":\"event lehrer test eins\"},{\"uuid\":\"4e4ce045-cd02-4fbd-93cb-00d9f559f8e1\",\"fullName\":\"event lehrer Zwei\"}]',1,'2020-05-15 19:15:12');
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
  `TEILNAHMENUMMER` varchar(36) CHARACTER SET utf8 NOT NULL COMMENT 'Schulkürzel oder Teilnahmenummer eines privaten Veranstalters',
  `WETTBEWERB_UUID` varchar(36) CHARACTER SET utf8 NOT NULL COMMENT 'uuid des Wettbewerbs zu dieser Teilnahme',
  `VERSION` int(10) DEFAULT 0,
  `DATE_MODIFIED` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`UUID`)
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
INSERT INTO `VERANSTALTER` VALUES ('07e89a7f-06f3-4e07-93d7-f8293d6c4256','PRIVAT','event-privat test-sechs',NULL,0,'2020-05-15 19:33:53','DEFAULT'),('24f4c507-454a-4cb1-bc0c-4da5df4a4a1b','LEHRER','event lehrer test eins','EEGEECP6',0,'2020-05-15 19:14:23','DEFAULT'),('2cdf18eb-dcd7-4e9d-8091-0e6340db0f11','PRIVAT','event-privat test-eins',NULL,0,'2020-05-15 19:13:18','DEFAULT'),('2f09da36-07c6-4033-a2f1-5e110c804026','LEHRER','Tolle Lehrerin','6XOA2A11',0,'2020-05-15 19:15:39','DEFAULT'),('444c50cc-4d57-47dc-9dbf-579663fac8e9','PRIVAT','event-privat test-drei',NULL,0,'2020-05-15 19:12:25','DEFAULT'),('4e4ce045-cd02-4fbd-93cb-00d9f559f8e1','LEHRER','event lehrer Zwei','EEGEECP6',0,'2020-05-15 19:15:12','DEFAULT'),('503034a5-155e-40d3-aca4-591dfaea4dc9','PRIVAT','event-privat test-vier',NULL,0,'2020-05-15 19:13:40','DEFAULT'),('82d7f4b0-1c2e-4d1d-b661-86f92a3fb820','PRIVAT','privat event Zwei',NULL,0,'2020-05-15 19:35:09','DEFAULT'),('8efc761c-83e7-42e3-bd2c-ba29b0db80dc','PRIVAT','Gretel von Hänsel',NULL,0,'2020-05-15 19:35:57','DEFAULT'),('9bfa4dc8-6251-44d4-87ab-72784123c1e7','PRIVAT','privat-event Eins',NULL,0,'2020-05-15 19:34:27','DEFAULT'),('d0cafd8d-542a-4b0e-a2d9-82bc8f9618cc','PRIVAT','event-privat test-fünf',NULL,0,'2020-05-15 19:13:58','DEFAULT'),('dd97e1bf-f52a-4429-9443-0de9d96dac37','PRIVAT','privat event drei',NULL,0,'2020-05-15 19:35:46','DEFAULT');
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
INSERT INTO `schema_version` VALUES (1,'1','<< Flyway Baseline >>','BASELINE','<< Flyway Baseline >>',NULL,'mk_wettbewerb','2020-04-12 11:44:20',0,1),(2,'2','table schulkollegien','SQL','V2__table_schulkollegien.sql',2095858003,'mk_wettbewerb','2020-04-12 11:51:14',18,1),(3,'3','table veranstalter','SQL','V3__table_veranstalter.sql',804445355,'mk_wettbewerb','2020-04-19 07:55:14',21,1),(4,'4','table wettbewerbe','SQL','V4__table_wettbewerbe.sql',1458260375,'mk_wettbewerb','2020-05-15 15:15:24',32,1),(5,'5','veranstalter rename column','SQL','V5__veranstalter_rename_column.sql',2042634501,'mk_wettbewerb','2020-05-15 15:15:24',11,1),(6,'6','veranstalter col zugang unterlagen','SQL','V6__veranstalter_col_zugang_unterlagen.sql',1046156931,'mk_wettbewerb','2020-05-15 19:47:43',16,1),(7,'7','table teilnahmen','SQL','V7__table_teilnahmen.sql',1292228015,'mk_wettbewerb','2020-05-15 20:05:01',22,1);
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

-- Dump completed on 2020-05-15 22:05:48
