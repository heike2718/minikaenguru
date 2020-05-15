-- MariaDB dump 10.17  Distrib 10.4.13-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: mk_gateway
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
-- Current Database: `mk_gateway`
--

/*!40000 DROP DATABASE IF EXISTS `mk_gateway`*/;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `mk_gateway` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `mk_gateway`;

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
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='speichert events aus der domain';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `PACEMAKERS`
--

DROP TABLE IF EXISTS `PACEMAKERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PACEMAKERS` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `MONITOR_ID` varchar(50) CHARACTER SET utf8 NOT NULL,
  `WERT` varchar(36) CHARACTER SET utf8 NOT NULL,
  `VERSION` int(10) DEFAULT 0,
  `DATE_MODIFIED` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`ID`),
  UNIQUE KEY `uk_pacemakers_1` (`MONITOR_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='registriert herzschrittmacher für mk_gateway';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `USERS`
--

DROP TABLE IF EXISTS `USERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `USERS` (
  `UUID` varchar(36) CHARACTER SET utf8 NOT NULL COMMENT 'UUID aus authprovider',
  `ROLE` varchar(100) CHARACTER SET utf8 NOT NULL,
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

-- Dump completed on 2020-05-15 21:17:59
