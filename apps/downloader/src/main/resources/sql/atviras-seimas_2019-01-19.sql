# ************************************************************
# Sequel Pro SQL dump
# Version 4541
#
# http://www.sequelpro.com/
# https://github.com/sequelpro/sequelpro
#
# Host: 127.0.0.1 (MySQL 5.7.24)
# Database: atviras-seimas
# Generation Time: 2019-01-19 14:59:51 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table faction
# ------------------------------------------------------------

CREATE TABLE `faction` (
  `faction_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL DEFAULT '',
  `faction_acronym` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`faction_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



# Dump of table plenary
# ------------------------------------------------------------

CREATE TABLE `plenary` (
  `plenary_id` int(11) NOT NULL AUTO_INCREMENT,
  `session_id` int(11) NOT NULL,
  `number` varchar(255) NOT NULL DEFAULT '',
  `plenary_type` varchar(255) NOT NULL DEFAULT '',
  `time_start` datetime DEFAULT NULL,
  `time_finish` datetime DEFAULT NULL,
  PRIMARY KEY (`plenary_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



# Dump of table plenary_question
# ------------------------------------------------------------

CREATE TABLE `plenary_question` (
  `plenary_question_id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL DEFAULT '',
  `time_from` time DEFAULT NULL,
  `time_to` time DEFAULT NULL,
  `number` varchar(255) NOT NULL DEFAULT '',
  `plenary_question_group_id` varchar(255) NOT NULL DEFAULT '',
  `status` int(11) NOT NULL,
  `document_link` varchar(255) NOT NULL DEFAULT '',
  `speakers` text NOT NULL,
  PRIMARY KEY (`plenary_question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



# Dump of table session
# ------------------------------------------------------------

CREATE TABLE `session` (
  `session_id` int(11) NOT NULL AUTO_INCREMENT,
  `term_of_office_id` int(11) NOT NULL,
  `number` varchar(255) NOT NULL DEFAULT '',
  `name` varchar(255) NOT NULL DEFAULT '',
  `date_from` date NOT NULL,
  `date_to` date DEFAULT NULL,
  PRIMARY KEY (`session_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



# Dump of table term_of_office
# ------------------------------------------------------------

CREATE TABLE `term_of_office` (
  `term_of_office_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL DEFAULT '',
  `date_from` date NOT NULL,
  `date_to` date DEFAULT NULL,
  PRIMARY KEY (`term_of_office_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



# Dump of table vote
# ------------------------------------------------------------

CREATE TABLE `vote` (
  `time` datetime NOT NULL,
  `vote_total` int(11) NOT NULL,
  `vote_total_max` int(11) NOT NULL,
  `vote_for` int(11) NOT NULL,
  `vote_against` int(11) NOT NULL,
  `vote_abstained` int(11) NOT NULL,
  `comment` text,
  `person_id` int(11) NOT NULL,
  `faction_acronym` varchar(255) NOT NULL DEFAULT '',
  `vote` int(11) NOT NULL,
  `person_name` varchar(255) NOT NULL DEFAULT '',
  `person_surname` varchar(255) NOT NULL DEFAULT '',
  `vote_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;




/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
