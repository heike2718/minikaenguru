-- MariaDB dump 10.17  Distrib 10.4.12-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: mk_wettbewerb
-- ------------------------------------------------------
-- Server version	10.4.12-MariaDB-1:10.4.12+maria~stretch-log

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
/*!40000 ALTER TABLE `SCHULKOLLEGIEN` ENABLE KEYS */;
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
  `TEILNAHMEKUERZEL` varchar(1000) CHARACTER SET utf8 DEFAULT NULL COMMENT 'Kommaseparierte Liste der Kürzel der Schulen oder Privatteilnahmen',
  `VERSION` int(10) DEFAULT 0,
  `DATE_MODIFIED` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `VERANSTALTER`
--

LOCK TABLES `VERANSTALTER` WRITE;
/*!40000 ALTER TABLE `VERANSTALTER` DISABLE KEYS */;
/*!40000 ALTER TABLE `VERANSTALTER` ENABLE KEYS */;
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
INSERT INTO `schema_version` VALUES (1,'1','<< Flyway Baseline >>','BASELINE','<< Flyway Baseline >>',NULL,'mk_wettbewerb','2020-04-12 11:44:20',0,1),(2,'2','table schulkollegien','SQL','V2__table_schulkollegien.sql',2095858003,'mk_wettbewerb','2020-04-12 11:51:14',18,1),(3,'3','table veranstalter','SQL','V3__table_veranstalter.sql',804445355,'mk_wettbewerb','2020-04-19 07:55:14',21,1);
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

-- Dump completed on 2020-04-19  9:55:19
