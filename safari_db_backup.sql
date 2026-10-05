-- ==========================================================
-- Wildlife Safari Management System (SE2030 Group Project)
-- Database Initialization & Backup Script
-- Database Name: safari_db
-- Compatibility: MySQL 8.0+, MySQL 9.x, MariaDB 10.4+, XAMPP / WAMP
-- ==========================================================

CREATE DATABASE IF NOT EXISTS `safari_db` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `safari_db`;

-- MySQL dump 10.13  Distrib 9.7.0, for macos15 (arm64)
--
-- Host: localhost    Database: safari_db
-- ------------------------------------------------------
-- Server version	9.7.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `activity_logs`
--

DROP TABLE IF EXISTS `activity_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `activity_logs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_email` varchar(150) NOT NULL,
  `role` varchar(50) NOT NULL,
  `module_name` varchar(100) NOT NULL,
  `action` varchar(100) NOT NULL,
  `details` text NOT NULL,
  `ip_address` varchar(50) DEFAULT '127.0.0.1',
  `timestamp` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=88 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activity_logs`
--

LOCK TABLES `activity_logs` WRITE;
/*!40000 ALTER TABLE `activity_logs` DISABLE KEYS */;
INSERT INTO `activity_logs` VALUES (1,'system','SYSTEM','Core System','INITIALIZE','System initialized with production seed data for SE2030 Group 03.','127.0.0.1','2026-10-04 11:11:55'),(2,'admin@safari.lk','ADMIN','Safari Package Management','CREATE_PACKAGE','Published new safari expedition: Yala Big 4 Predator Expedition.','127.0.0.1','2026-10-04 11:11:55'),(3,'kavinda.perera@gmail.com','CUSTOMER','Booking Management','CREATE_BOOKING','Created confirmed reservation WS-2026-10492 for Yala Safari.','127.0.0.1','2026-10-04 11:11:55'),(4,'sunil.bandara@gmail.com','GUIDE','Authentication','LOGIN_SUCCESS','User Sunil Bandara (Licensed Senior Naturalist & Driver) successfully signed in.','127.0.0.1','2026-10-04 06:09:39'),(5,'ops@safari.lk','OPERATIONS_MANAGER','Authentication','LOGIN_SUCCESS','User Chaminda Silva (Fleet Dispatcher) successfully signed in.','127.0.0.1','2026-10-04 06:10:24'),(6,'ops@safari.lk','OPERATIONS_MANAGER','Guide & Vehicle Allocation','CREW_ASSIGNED','Booking WS-2026-10493 assigned Guide Sunil Bandara (Licensed Senior Naturalist) and Vehicle CP-NA-9024','127.0.0.1','2026-10-04 06:10:50'),(7,'sunil.bandara@gmail.com','GUIDE','Authentication','LOGIN_SUCCESS','User Sunil Bandara (Licensed Senior Naturalist & Driver) successfully signed in.','127.0.0.1','2026-10-04 06:10:56'),(8,'kavinda.perera@gmail.com','CUSTOMER','Authentication','LOGIN_SUCCESS','User Kavinda Perera (Tourist) successfully signed in.','127.0.0.1','2026-10-04 06:11:45'),(9,'kavinda.perera@gmail.com','CUSTOMER','Booking & Reservation','CREATE_BOOKING','Reservation WS-2026-45688 booked for Wilpattu Wilderness & Ancient Villu Trail on 2026-10-08','127.0.0.1','2026-10-04 06:12:15'),(10,'kavinda.perera@gmail.com','CUSTOMER','Inventory & Equipment','REQUEST_EQUIPMENT','Requested 2x Motorola VHF Long-Range Bush Radios for booking WS-2026-45688','127.0.0.1','2026-10-04 06:12:20'),(11,'kavinda.perera@gmail.com','CUSTOMER','Booking & Reservation','UPDATE_BOOKING','Booking WS-2026-45688 updated — 3 pax, date: 2026-10-08, new total: LKR 102000.00','127.0.0.1','2026-10-04 06:12:36'),(12,'kavinda.perera@gmail.com','CUSTOMER','Booking & Reservation','UPDATE_BOOKING','Booking WS-2026-45688 updated — 1 pax, date: 2026-10-08, new total: LKR 34000.00','127.0.0.1','2026-10-04 06:12:42'),(13,'kavinda.perera@gmail.com','FINANCE_OFFICER','Payment & Invoice','INVOICE_GENERATED','Generated Tax Invoice INV-2026-3706 for amount LKR 34000.00','127.0.0.1','2026-10-04 06:12:56'),(14,'kavinda.perera@gmail.com','FINANCE_OFFICER','Payment & Invoice','PAYMENT_SUCCESS','Payment PAY-2026-63146 completed for booking WS-2026-45688 (LKR 34000.00)','127.0.0.1','2026-10-04 06:12:56'),(15,'kavinda.perera@gmail.com','CUSTOMER','Authentication','LOGIN_SUCCESS','User Kavinda Perera (Tourist) successfully signed in.','127.0.0.1','2026-10-04 08:10:50'),(16,'kavinda.perera@gmail.com','CUSTOMER','Booking & Reservation','CREATE_BOOKING','Reservation WS-2026-33728 booked for Yala Big 4 Predator Expedition on 2026-10-15','127.0.0.1','2026-10-04 08:11:23'),(17,'kavinda.perera@gmail.com','CUSTOMER','Inventory & Equipment','REQUEST_EQUIPMENT','Requested 4x Motorola VHF Long-Range Bush Radios for booking WS-2026-33728','127.0.0.1','2026-10-04 08:11:33'),(18,'kavinda.perera@gmail.com','CUSTOMER','Booking & Reservation','CREATE_BOOKING','Reservation WS-2026-81923 booked for Yala Big 4 Predator Expedition on 2026-10-15','127.0.0.1','2026-10-04 08:11:54'),(19,'kavinda.perera@gmail.com','FINANCE_OFFICER','Payment & Invoice','INVOICE_GENERATED','Generated Tax Invoice INV-2026-8605 for amount LKR 57000.00','127.0.0.1','2026-10-04 08:15:08'),(20,'kavinda.perera@gmail.com','FINANCE_OFFICER','Payment & Invoice','PAYMENT_SUCCESS','Payment PAY-2026-96327 completed for booking WS-2026-81923 (LKR 57000.00)','127.0.0.1','2026-10-04 08:15:08'),(21,'kavinda.perera@gmail.com','CUSTOMER','Authentication','LOGIN_SUCCESS','User Kavinda Perera (Tourist) successfully signed in.','127.0.0.1','2026-10-04 08:22:57'),(22,'kavinda.perera@gmail.com','CUSTOMER','Authentication','LOGIN_SUCCESS','User Kavinda Perera (Tourist) successfully signed in.','127.0.0.1','2026-10-04 08:32:52'),(23,'kavinda.perera@gmail.com','CUSTOMER','Authentication','LOGIN_SUCCESS','User Kavinda Perera (Tourist) successfully signed in.','127.0.0.1','2026-10-04 08:41:50'),(24,'kavinda.perera@gmail.com','CUSTOMER','Authentication','LOGIN_SUCCESS','User Kavinda Perera (Tourist) successfully signed in.','127.0.0.1','2026-10-04 13:29:07'),(25,'kavinda.perera@gmail.com','CUSTOMER','Authentication','LOGIN_SUCCESS','User Kavinda Perera (Tourist) successfully signed in.','127.0.0.1','2026-10-04 14:38:21'),(26,'kavinda.perera@gmail.com','CUSTOMER','Booking & Reservation','CANCEL_BOOKING','Reservation WS-2026-81923 marked as CANCELLED.','127.0.0.1','2026-10-04 14:38:49'),(27,'kavinda.perera@gmail.com','CUSTOMER','Booking & Reservation','CREATE_BOOKING','Reservation WS-2026-13615 booked for Yala Big 4 Predator Expedition on 2028-02-12','127.0.0.1','2026-10-04 16:21:45'),(28,'kavinda.perera@gmail.com','CUSTOMER','Booking & Reservation','UPDATE_BOOKING','Booking WS-2026-13615 updated — 5 pax, date: 2028-07-15, new total: LKR 178125.00','127.0.0.1','2026-10-04 16:22:23'),(29,'finance@safari.lk','FINANCE_OFFICER','Authentication','LOGIN_SUCCESS','User Anjali Wickramasinghe (Finance Manager) successfully signed in.','127.0.0.1','2026-10-04 16:22:51'),(30,'finance@safari.lk','FINANCE_OFFICER','Payment & Invoice','REFUND_PROCESSED','Processed refund for payment PAY-2026-96327 (LKR 57000.00)','127.0.0.1','2026-10-04 16:23:27'),(31,'kavinda.perera@gmail.com','CUSTOMER','Authentication','LOGIN_SUCCESS','User Kavinda Perera (Tourist) successfully signed in.','127.0.0.1','2026-10-04 16:23:35'),(32,'ops@safari.lk','OPERATIONS_MANAGER','Authentication','LOGIN_SUCCESS','User Chaminda Silva (Fleet Dispatcher) successfully signed in.','127.0.0.1','2026-10-04 16:23:59'),(33,'ops@safari.lk','OPERATIONS_MANAGER','Guide & Vehicle Allocation','CREW_ASSIGNED','Booking WS-2026-33728 assigned Guide Kasun Rathnayake (Avian Specialist) and Vehicle CP-NA-9024','127.0.0.1','2026-10-04 16:24:17'),(34,'kavinda.perera@gmail.com','CUSTOMER','Authentication','LOGIN_SUCCESS','User Kavinda Perera (Tourist) successfully signed in.','127.0.0.1','2026-10-04 16:25:50'),(35,'sunil.bandara@gmail.com','GUIDE','Authentication','LOGIN_SUCCESS','User Sunil Bandara (Licensed Senior Naturalist & Driver) successfully signed in.','127.0.0.1','2026-10-04 16:26:29'),(36,'sunil.bandara@gmail.com','GUIDE','Authentication','LOGIN_SUCCESS','User Sunil Bandara (Licensed Senior Naturalist & Driver) successfully signed in.','127.0.0.1','2026-10-04 16:43:32'),(37,'admin@safari.lk','ADMIN','Authentication','LOGIN_SUCCESS','User Ruwan Senanayake (Chief Administrator) successfully signed in.','127.0.0.1','2026-10-04 16:47:29'),(38,'admin@safari.lk','ADMIN','Authentication','LOGIN_SUCCESS','User Ruwan Senanayake (Chief Administrator) successfully signed in.','127.0.0.1','2026-10-04 16:48:16'),(39,'operator@safari.lk','TOUR_OPERATOR','Authentication','LOGIN_SUCCESS','User Dulanja Perera (Tour Operations Lead) successfully signed in.','127.0.0.1','2026-10-04 16:49:01'),(40,'operator@safari.lk','TOUR_OPERATOR','Safari Package Management','TOGGLE_STATUS','Package \'Yala Big 4 Predator Expedition\' status changed to INACTIVE.','127.0.0.1','2026-10-04 16:49:06'),(41,'operator@safari.lk','TOUR_OPERATOR','Safari Package Management','TOGGLE_STATUS','Package \'Yala Big 4 Predator Expedition\' status changed to ACTIVE.','127.0.0.1','2026-10-04 16:49:09'),(42,'ops@safari.lk','OPERATIONS_MANAGER','Authentication','LOGIN_SUCCESS','User Chaminda Silva (Fleet Dispatcher) successfully signed in.','127.0.0.1','2026-10-04 16:49:14'),(43,'ops@safari.lk','OPERATIONS_MANAGER','Guide & Vehicle Allocation','VEHICLE_MAINTENANCE','Vehicle WP-CAB-4821 set to MAINTENANCE','127.0.0.1','2026-10-04 16:49:32'),(44,'ops@safari.lk','OPERATIONS_MANAGER','Guide & Vehicle Allocation','VEHICLE_MAINTENANCE','Vehicle WP-CAB-4821 set to AVAILABLE','127.0.0.1','2026-10-04 16:49:33'),(45,'ops@safari.lk','OPERATIONS_MANAGER','Guide & Vehicle Allocation','VEHICLE_MAINTENANCE','Vehicle CP-NA-9024 set to MAINTENANCE','127.0.0.1','2026-10-04 16:49:34'),(46,'ops@safari.lk','OPERATIONS_MANAGER','Guide & Vehicle Allocation','VEHICLE_MAINTENANCE','Vehicle CP-NA-9024 set to AVAILABLE','127.0.0.1','2026-10-04 16:49:35'),(47,'ops@safari.lk','OPERATIONS_MANAGER','Guide & Vehicle Allocation','VEHICLE_MAINTENANCE','Vehicle SP-GA-3188 set to MAINTENANCE','127.0.0.1','2026-10-04 16:49:36'),(48,'ops@safari.lk','OPERATIONS_MANAGER','Guide & Vehicle Allocation','VEHICLE_MAINTENANCE','Vehicle SP-GA-3188 set to AVAILABLE','127.0.0.1','2026-10-04 16:49:36'),(49,'ops@safari.lk','OPERATIONS_MANAGER','Guide & Vehicle Allocation','CREW_ASSIGNED','Booking WS-2026-10492 assigned Guide Chathura Dissanayake (Expert Wildlife Tracker) and Vehicle CP-NA-9024','127.0.0.1','2026-10-04 16:50:39'),(50,'ranger@safari.lk','CONSERVATION_OFFICER','Authentication','LOGIN_SUCCESS','User Pradeep Bandara (DWC Ranger / Officer) successfully signed in.','127.0.0.1','2026-10-04 16:50:48'),(51,'logistics@safari.lk','LOGISTICS_STAFF','Authentication','LOGIN_SUCCESS','User Nuwan Jayalath (Logistics Supervisor) successfully signed in.','127.0.0.1','2026-10-04 16:51:00'),(52,'logistics@safari.lk','LOGISTICS_STAFF','Inventory & Equipment','APPROVE_EQUIPMENT','Approved 2x Motorola VHF Long-Range Bush Radios to booking WS-2026-45688','127.0.0.1','2026-10-04 16:51:16'),(53,'logistics@safari.lk','LOGISTICS_STAFF','Inventory & Equipment','DECOMMISSION_EQUIPMENT','Decommissioned equipment: Garmin inReach Explorer+ GPS Communicator (EQ-NAV-01)','127.0.0.1','2026-10-04 16:52:00'),(54,'logistics@safari.lk','LOGISTICS_STAFF','Inventory & Equipment','ADD_EQUIPMENT','Equipment item: sa (Code: ssas, Total: 2)','127.0.0.1','2026-10-04 16:52:16'),(55,'ops@safari.lk','OPERATIONS_MANAGER','Guide & Vehicle Allocation','UPDATE_GUIDE','Updated guide profile: Sunil Bandara (Licensed Senior Naturalist) (License: DWC-LK-4091)','127.0.0.1','2026-10-04 17:06:51'),(56,'ops@safari.lk','OPERATIONS_MANAGER','Guide & Vehicle Allocation','UPDATE_VEHICLE','Updated vehicle: WP-CAB-4821 (Toyota Land Cruiser 79 Safari 4x4 (High Elevated))','127.0.0.1','2026-10-04 17:06:53'),(57,'ops@safari.lk','OPERATIONS_MANAGER','Authentication','LOGIN_SUCCESS','User Chaminda Silva (Fleet Dispatcher) successfully signed in.','127.0.0.1','2026-10-04 17:10:31'),(58,'ops@safari.lk','OPERATIONS_MANAGER','Guide & Vehicle Allocation','VEHICLE_MAINTENANCE','Vehicle WP-CAB-4821 set to MAINTENANCE','127.0.0.1','2026-10-04 17:10:36'),(59,'ops@safari.lk','OPERATIONS_MANAGER','Guide & Vehicle Allocation','VEHICLE_MAINTENANCE','Vehicle WP-CAB-4821 set to AVAILABLE','127.0.0.1','2026-10-04 17:10:37'),(60,'ops@safari.lk','OPERATIONS_MANAGER','Guide & Vehicle Allocation','UPDATE_VEHICLE','Updated vehicle: WP-CAB-4821 (Toyota Land Cruiser 79 Safari 4x4 (High Elevated))','127.0.0.1','2026-10-04 17:10:43'),(61,'ops@safari.lk','OPERATIONS_MANAGER','Guide & Vehicle Allocation','UPDATE_GUIDE','Updated guide profile: Sunil Bandara (Licensed Senior Naturalist) (License: DWC-LK-4091)','127.0.0.1','2026-10-04 17:10:52'),(62,'finance@safari.lk','FINANCE_OFFICER','Authentication','LOGIN_SUCCESS','User Anjali Wickramasinghe (Finance Manager) successfully signed in.','127.0.0.1','2026-10-04 17:11:10'),(63,'ops@safari.lk','OPERATIONS_MANAGER','Authentication','LOGIN_SUCCESS','User Chaminda Silva (Fleet Dispatcher) successfully signed in.','127.0.0.1','2026-10-04 17:11:32'),(64,'kavinda.perera@gmail.com','CUSTOMER','Booking & Reservation','CREATE_BOOKING','Reservation WS-2026-62531 booked for Wilpattu Wilderness & Ancient Villu Trail on 2026-10-07','127.0.0.1','2026-10-05 07:06:59'),(65,'kavinda.perera@gmail.com','FINANCE_OFFICER','Payment & Invoice','BANK_TRANSFER_SUBMITTED','Bank transfer slip uploaded for WS-2026-62531','127.0.0.1','2026-10-05 07:07:02'),(66,'finance@safari.lk','FINANCE_OFFICER','Payment & Invoice','INVOICE_GENERATED','Generated Tax Invoice INV-2026-9030 for amount LKR 34000.00','127.0.0.1','2026-10-05 07:07:55'),(67,'finance@safari.lk','FINANCE_OFFICER','Payment & Invoice','PAYMENT_APPROVED','Payment PAY-BT-2026-6665 verified and approved.','127.0.0.1','2026-10-05 07:07:55'),(68,'kavinda.perera@gmail.com','CUSTOMER','Authentication','LOGIN_SUCCESS','User Kavinda Perera (Tourist) successfully signed in.','127.0.0.1','2026-10-05 07:13:53'),(69,'dula@gmail.com','CUSTOMER','User Management','REGISTER','New account created for dulaara with role CUSTOMER','127.0.0.1','2026-10-05 07:14:46'),(70,'dula@gmail.com','CUSTOMER','Booking & Reservation','CREATE_BOOKING','Reservation WS-2026-13496 booked for Yala Big 4 Predator Expedition on 2026-12-03','127.0.0.1','2026-10-05 07:15:22'),(71,'dula@gmail.com','CUSTOMER','Inventory & Equipment','REQUEST_EQUIPMENT','Requested 3x Outback 4-Person All-Weather Safari Tent for booking WS-2026-13496','127.0.0.1','2026-10-05 07:15:27'),(72,'dula@gmail.com','FINANCE_OFFICER','Payment & Invoice','INVOICE_GENERATED','Generated Tax Invoice INV-2026-1212 for amount LKR 35625.00','127.0.0.1','2026-10-05 07:15:53'),(73,'dula@gmail.com','FINANCE_OFFICER','Payment & Invoice','PAYMENT_SUCCESS','Payment PAY-2026-46461 completed for booking WS-2026-13496 (LKR 35625.00)','127.0.0.1','2026-10-05 07:15:53'),(74,'dula@gmail.com','CUSTOMER','Booking & Reservation','CREATE_BOOKING','Reservation WS-2026-36726 booked for Wilpattu Wilderness & Ancient Villu Trail on 2026-10-07','127.0.0.1','2026-10-05 07:16:01'),(75,'dula@gmail.com','FINANCE_OFFICER','Payment & Invoice','BANK_TRANSFER_SUBMITTED','Bank transfer slip uploaded for WS-2026-36726','127.0.0.1','2026-10-05 07:16:17'),(76,'finance@safari.lk','FINANCE_OFFICER','Authentication','LOGIN_SUCCESS','User Anjali Wickramasinghe (Finance Manager) successfully signed in.','127.0.0.1','2026-10-05 07:16:35'),(77,'finance@safari.lk','FINANCE_OFFICER','Payment & Invoice','INVOICE_GENERATED','Generated Tax Invoice INV-2026-5435 for amount LKR 34000.00','127.0.0.1','2026-10-05 07:17:27'),(78,'finance@safari.lk','FINANCE_OFFICER','Payment & Invoice','PAYMENT_APPROVED','Payment PAY-BT-2026-9841 verified and approved.','127.0.0.1','2026-10-05 07:17:27'),(79,'dula@gmail.com','CUSTOMER','Authentication','LOGIN_SUCCESS','User dulaara successfully signed in.','127.0.0.1','2026-10-05 07:17:52'),(80,'dula@gmail.com','CUSTOMER','Booking & Reservation','CANCEL_BOOKING','Reservation WS-2026-36726 marked as CANCELLED.','127.0.0.1','2026-10-05 07:18:07'),(81,'finance@safari.lk','FINANCE_OFFICER','Authentication','LOGIN_SUCCESS','User Anjali Wickramasinghe (Finance Manager) successfully signed in.','127.0.0.1','2026-10-05 07:18:14'),(82,'finance@safari.lk','FINANCE_OFFICER','Payment & Invoice','REFUND_PROCESSED','Processed refund for payment PAY-BT-2026-9841 (LKR 34000.00)','127.0.0.1','2026-10-05 07:18:18'),(83,'dula@gmail.com','CUSTOMER','Authentication','LOGIN_SUCCESS','User dulaara successfully signed in.','127.0.0.1','2026-10-05 07:18:40'),(84,'logistics@safari.lk','LOGISTICS_STAFF','Authentication','LOGIN_SUCCESS','User Nuwan Jayalath (Logistics Supervisor) successfully signed in.','127.0.0.1','2026-10-05 07:18:57'),(85,'logistics@safari.lk','LOGISTICS_STAFF','Inventory & Equipment','ADD_EQUIPMENT','Equipment item: helmat (Code: 221, Total: 3)','127.0.0.1','2026-10-05 07:19:15'),(86,'logistics@safari.lk','LOGISTICS_STAFF','Inventory & Equipment','APPROVE_EQUIPMENT','Approved 4x Motorola VHF Long-Range Bush Radios to booking WS-2026-33728','127.0.0.1','2026-10-05 07:19:19'),(87,'logistics@safari.lk','LOGISTICS_STAFF','Inventory & Equipment','RETURN_EQUIPMENT','Returned 2x Motorola VHF Long-Range Bush Radios from booking WS-2026-45688','127.0.0.1','2026-10-05 07:19:36');
/*!40000 ALTER TABLE `activity_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `booking_participants`
--

DROP TABLE IF EXISTS `booking_participants`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `booking_participants` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `booking_id` bigint NOT NULL,
  `full_name` varchar(150) NOT NULL,
  `id_or_passport` varchar(50) NOT NULL,
  `age` int NOT NULL,
  `nationality` varchar(100) NOT NULL,
  `emergency_contact` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_participant_booking` (`booking_id`),
  CONSTRAINT `fk_participant_booking` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `booking_participants`
--

LOCK TABLES `booking_participants` WRITE;
/*!40000 ALTER TABLE `booking_participants` DISABLE KEYS */;
/*!40000 ALTER TABLE `booking_participants` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bookings`
--

DROP TABLE IF EXISTS `bookings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bookings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `booking_reference` varchar(50) NOT NULL,
  `package_id` bigint NOT NULL,
  `customer_id` bigint DEFAULT NULL,
  `customer_name` varchar(150) NOT NULL,
  `customer_email` varchar(150) NOT NULL,
  `customer_phone` varchar(20) NOT NULL,
  `trip_date` date NOT NULL,
  `participant_count` int NOT NULL,
  `special_requests` text,
  `total_price` decimal(38,2) NOT NULL,
  `booking_status` varchar(50) NOT NULL DEFAULT 'PENDING',
  `payment_status` varchar(50) NOT NULL DEFAULT 'UNPAID',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `booking_reference` (`booking_reference`),
  KEY `fk_booking_package` (`package_id`),
  CONSTRAINT `fk_booking_package` FOREIGN KEY (`package_id`) REFERENCES `safari_packages` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bookings`
--

LOCK TABLES `bookings` WRITE;
/*!40000 ALTER TABLE `bookings` DISABLE KEYS */;
INSERT INTO `bookings` VALUES (1,'WS-2026-10492',1,7,'Kavinda Perera','kavinda.perera@gmail.com','0777654321','2026-10-07',2,'Binoculars rental requested. Focus on high-resolution wildlife photography.',57000.00,'CONFIRMED','PAID','2026-10-04 05:41:55'),(2,'WS-2026-10493',3,7,'Ananya Jayawardena','ananya.j@gmail.com','0718899221','2026-10-11',4,'Elderly passenger onboard, request smooth track route.',88000.00,'CONFIRMED','UNPAID','2026-10-04 05:41:55'),(3,'WS-2026-45688',2,8,'Kavinda Perera (Tourist)','kavinda.perera@gmail.com','0777654321','2026-10-08',1,'sasas',34000.00,'CONFIRMED','PAID','2026-10-04 00:42:15'),(4,'WS-2026-33728',1,8,'Kavinda Perera (Tourist)','kavinda.perera@gmail.com','2299232332','2026-10-15',2,'maru bn',57000.00,'CONFIRMED','UNPAID','2026-10-04 02:41:23'),(5,'WS-2026-81923',1,8,'Kavinda Perera (Tourist)','kavinda.perera@gmail.com','0777654321','2026-10-15',2,'hhhhh',57000.00,'CANCELLED','REFUNDED','2026-10-04 02:41:54'),(6,'WS-2026-13615',1,8,'Kavinda Perera (Tourist)','kavinda.perera@gmail.com','0777654321','2028-07-15',5,'hihii',178125.00,'PENDING','UNPAID','2026-10-04 10:51:45'),(7,'WS-2026-62531',2,8,'Kavinda Perera (Tourist)','kavinda.perera@gmail.com','0777654321','2026-10-07',1,'',34000.00,'CONFIRMED','PAID','2026-10-05 01:36:59'),(8,'WS-2026-13496',1,9,'dulaara','dula@gmail.com','1234323232','2026-12-03',1,'',35625.00,'CONFIRMED','PAID','2026-10-05 01:45:22'),(9,'WS-2026-36726',2,9,'dulaara','dula@gmail.com','1234323232','2026-10-07',1,'dsdsd',34000.00,'CANCELLED','REFUNDED','2026-10-05 01:46:01');
/*!40000 ALTER TABLE `bookings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `equipment_allocations`
--

DROP TABLE IF EXISTS `equipment_allocations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `equipment_allocations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `equipment_id` bigint NOT NULL,
  `booking_id` bigint NOT NULL,
  `allocated_quantity` int NOT NULL DEFAULT '1',
  `issued_date` date NOT NULL,
  `return_date` date DEFAULT NULL,
  `status` varchar(50) NOT NULL DEFAULT 'ISSUED',
  `remarks` text,
  PRIMARY KEY (`equipment_id`,`booking_id`),
  UNIQUE KEY `uk_equipment_booking_composite` (`equipment_id`,`booking_id`),
  KEY `idx_eqalloc_id` (`id`),
  KEY `fk_eqalloc_booking` (`booking_id`),
  CONSTRAINT `fk_eqalloc_booking` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_eqalloc_eq` FOREIGN KEY (`equipment_id`) REFERENCES `equipment_inventory` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `equipment_allocations`
--

LOCK TABLES `equipment_allocations` WRITE;
/*!40000 ALTER TABLE `equipment_allocations` DISABLE KEYS */;
INSERT INTO `equipment_allocations` VALUES (1,4,3,2,'2026-10-04','2026-10-05','RETURNED','Customer request via voucher | Return condition: Good condition'),(2,4,4,4,'2026-10-05',NULL,'ISSUED','Customer request via voucher'),(3,5,8,3,'2026-10-05',NULL,'REQUESTED','Customer request via voucher');
/*!40000 ALTER TABLE `equipment_allocations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `equipment_inventory`
--

DROP TABLE IF EXISTS `equipment_inventory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `equipment_inventory` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `item_code` varchar(50) NOT NULL,
  `item_name` varchar(150) NOT NULL,
  `category` varchar(100) NOT NULL,
  `total_quantity` int NOT NULL DEFAULT '0',
  `available_quantity` int NOT NULL DEFAULT '0',
  `min_threshold` int NOT NULL DEFAULT '2',
  `condition_status` varchar(50) NOT NULL DEFAULT 'GOOD',
  `location` varchar(100) NOT NULL DEFAULT 'Main Safari Depot',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `item_code` (`item_code`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `equipment_inventory`
--

LOCK TABLES `equipment_inventory` WRITE;
/*!40000 ALTER TABLE `equipment_inventory` DISABLE KEYS */;
INSERT INTO `equipment_inventory` VALUES (1,'EQ-OPT-01','Nikon Monarch 8x42 Waterproof Binoculars','OPTICS',10,8,3,'GOOD','Yala Base Depot','2026-10-04 05:41:55'),(3,'EQ-SAF-01','Tactical Field Trauma First-Aid Backpack','SAFETY',8,8,2,'GOOD','Medical Emergency Bay','2026-10-04 05:41:55'),(4,'EQ-COM-01','Motorola VHF Long-Range Bush Radios','COMMUNICATION',12,6,4,'GOOD','Equipment Lockup','2026-10-04 05:41:55'),(5,'EQ-CMP-01','Outback 4-Person All-Weather Safari Tent','CAMPING',5,1,2,'GOOD','Camping Depot','2026-10-04 05:41:55'),(6,'ssas','sa','SAFETY',2,2,2,'GOOD','Main Safari Depot','2026-10-04 11:22:16'),(7,'221','helmat','CAMPING',3,3,2,'GOOD','Main Safari Depot','2026-10-05 01:49:15');
/*!40000 ALTER TABLE `equipment_inventory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `guide_availability`
--

DROP TABLE IF EXISTS `guide_availability`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `guide_availability` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `guide_id` bigint NOT NULL,
  `unavailable_date` date NOT NULL,
  `reason` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK499f4t5r2fc0vllu7natp5mvy` (`guide_id`,`unavailable_date`),
  CONSTRAINT `fk_guide_avail_g` FOREIGN KEY (`guide_id`) REFERENCES `guides` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `guide_availability`
--

LOCK TABLES `guide_availability` WRITE;
/*!40000 ALTER TABLE `guide_availability` DISABLE KEYS */;
/*!40000 ALTER TABLE `guide_availability` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `guides`
--

DROP TABLE IF EXISTS `guides`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `guides` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `full_name` varchar(150) NOT NULL,
  `license_number` varchar(50) NOT NULL,
  `contact_number` varchar(20) NOT NULL,
  `email` varchar(150) NOT NULL,
  `languages` varchar(200) NOT NULL,
  `experience_years` int NOT NULL DEFAULT '5',
  `status` varchar(50) NOT NULL DEFAULT 'ACTIVE',
  `daily_rate` decimal(38,2) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `license_number` (`license_number`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `guides`
--

LOCK TABLES `guides` WRITE;
/*!40000 ALTER TABLE `guides` DISABLE KEYS */;
INSERT INTO `guides` VALUES (1,'Sunil Bandara (Licensed Senior Naturalist)','DWC-LK-4091','0772345678','sunil.bandara@gmail.com','English, Sinhala, German,tamil',14,'ACTIVE',5500.00,'2026-10-04 05:41:55'),(2,'Chathura Dissanayake (Expert Wildlife Tracker)','DWC-LK-4822','0713456789','chathura.d@gmail.com','English, Sinhala, French',9,'ACTIVE',4800.00,'2026-10-04 05:41:55'),(3,'Kasun Rathnayake (Avian Specialist)','DWC-LK-5104','0764567890','kasun.rathna@gmail.com','English, Sinhala',6,'ACTIVE',4200.00,'2026-10-04 05:41:55');
/*!40000 ALTER TABLE `guides` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `incident_reports`
--

DROP TABLE IF EXISTS `incident_reports`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `incident_reports` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `incident_number` varchar(50) NOT NULL,
  `park_name` varchar(100) NOT NULL,
  `booking_id` bigint DEFAULT NULL,
  `incident_type` varchar(100) NOT NULL,
  `severity` varchar(50) NOT NULL,
  `description` text NOT NULL,
  `action_taken` text NOT NULL,
  `reported_date` date NOT NULL,
  `status` varchar(50) NOT NULL DEFAULT 'SUBMITTED',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `incident_number` (`incident_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `incident_reports`
--

LOCK TABLES `incident_reports` WRITE;
/*!40000 ALTER TABLE `incident_reports` DISABLE KEYS */;
/*!40000 ALTER TABLE `incident_reports` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `invoices`
--

DROP TABLE IF EXISTS `invoices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `invoices` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `invoice_number` varchar(50) NOT NULL,
  `payment_id` bigint NOT NULL,
  `booking_id` bigint NOT NULL,
  `subtotal` decimal(38,2) NOT NULL,
  `tax_amount` decimal(38,2) NOT NULL,
  `total_amount` decimal(38,2) NOT NULL,
  `invoice_date` date NOT NULL,
  `status` varchar(50) NOT NULL DEFAULT 'ISSUED',
  `notes` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `invoice_number` (`invoice_number`),
  KEY `fk_inv_payment` (`payment_id`),
  KEY `fk_inv_booking` (`booking_id`),
  CONSTRAINT `fk_inv_booking` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_inv_payment` FOREIGN KEY (`payment_id`) REFERENCES `payments` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invoices`
--

LOCK TABLES `invoices` WRITE;
/*!40000 ALTER TABLE `invoices` DISABLE KEYS */;
INSERT INTO `invoices` VALUES (1,'INV-2026-8801',1,1,51818.18,5181.82,57000.00,'2026-10-04','ISSUED','Includes DWC park entrance levies and 4x4 off-road permit fees.','2026-10-04 05:41:55'),(2,'INV-2026-3706',2,3,30909.09,3090.91,34000.00,'2026-10-04','ISSUED','Includes DWC park conservation fee and certified 4x4 naturalist safari services.','2026-10-04 00:42:56'),(3,'INV-2026-8605',3,5,51818.18,5181.82,57000.00,'2026-10-04','ISSUED','Includes DWC park conservation fee and certified 4x4 naturalist safari services.','2026-10-04 02:45:08'),(4,'INV-2026-9030',4,7,30909.09,3090.91,34000.00,'2026-10-05','ISSUED','Includes DWC park conservation fee and certified 4x4 naturalist safari services.','2026-10-05 01:37:55'),(5,'INV-2026-1212',5,8,32386.36,3238.64,35625.00,'2026-10-05','ISSUED','Includes DWC park conservation fee and certified 4x4 naturalist safari services.','2026-10-05 01:45:53'),(6,'INV-2026-5435',6,9,30909.09,3090.91,34000.00,'2026-10-05','ISSUED','Includes DWC park conservation fee and certified 4x4 naturalist safari services.','2026-10-05 01:47:27');
/*!40000 ALTER TABLE `invoices` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `recipient_email` varchar(150) NOT NULL,
  `title` varchar(255) NOT NULL,
  `message` text NOT NULL,
  `type` varchar(50) NOT NULL DEFAULT 'INFO',
  `is_read` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
INSERT INTO `notifications` VALUES (1,'sunil.bandara@gmail.com','New Safari Assignment 🌿','You have been assigned to Booking WS-2026-10493 on 2026-10-11. Package: Udawalawe Elephant Sanctuary Safari. Participants: 4. Vehicle: CP-NA-9024','SUCCESS',1,'2026-10-04 00:40:50'),(2,'logistics@safari.lk','New Equipment Request: Motorola VHF Long-Range Bush Radios 📦','4x Motorola VHF Long-Range Bush Radios requested for Booking WS-2026-33728 by Kavinda Perera (Tourist) (kavinda.perera@gmail.com). Please inspect depot and approve.','EQUIPMENT_REQUEST',0,'2026-10-04 02:41:33'),(3,'kasun.rathna@gmail.com','New Safari Assignment 🌿','You have been assigned to Booking WS-2026-33728 on 2026-10-15. Package: Yala Big 4 Predator Expedition. Participants: 2. Vehicle: CP-NA-9024','SUCCESS',0,'2026-10-04 10:54:17'),(4,'kavinda.perera@gmail.com','Safari Crew Assigned: Guide & Cruiser Confirmed 🚙🌿','Your safari expedition (WS-2026-33728) on 2026-10-15 has been assigned dedicated naturalist guide Kasun Rathnayake (Avian Specialist) (Contact: 0764567890) and 4x4 Safari Cruiser CP-NA-9024 (Land Rover Defender 110 Soft Top Edition).','CREW_ASSIGNED',0,'2026-10-04 10:54:17'),(5,'chathura.d@gmail.com','New Safari Assignment 🌿','You have been assigned to Booking WS-2026-10492 on 2026-10-07. Package: Yala Big 4 Predator Expedition. Participants: 2. Vehicle: CP-NA-9024','SUCCESS',0,'2026-10-04 11:20:39'),(6,'kavinda.perera@gmail.com','Safari Crew Assigned: Guide & Cruiser Confirmed 🚙🌿','Your safari expedition (WS-2026-10492) on 2026-10-07 has been assigned dedicated naturalist guide Chathura Dissanayake (Expert Wildlife Tracker) (Contact: 0713456789) and 4x4 Safari Cruiser CP-NA-9024 (Land Rover Defender 110 Soft Top Edition).','CREW_ASSIGNED',0,'2026-10-04 11:20:39'),(7,'kavinda.perera@gmail.com','Equipment Request Approved: Motorola VHF Long-Range Bush Radios ✅','Your request for 2x Motorola VHF Long-Range Bush Radios for Booking WS-2026-45688 has been approved by the depot.','EQUIPMENT_APPROVED',0,'2026-10-04 11:21:16'),(8,'logistics@safari.lk','New Equipment Request: Outback 4-Person All-Weather Safari Tent 📦','3x Outback 4-Person All-Weather Safari Tent requested for Booking WS-2026-13496 by dulaara (dula@gmail.com). Please inspect depot and approve.','EQUIPMENT_REQUEST',0,'2026-10-05 01:45:27'),(9,'kavinda.perera@gmail.com','Equipment Request Approved: Motorola VHF Long-Range Bush Radios ✅','Your request for 4x Motorola VHF Long-Range Bush Radios for Booking WS-2026-33728 has been approved by the depot.','EQUIPMENT_APPROVED',0,'2026-10-05 01:49:19');
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `park_permits`
--

DROP TABLE IF EXISTS `park_permits`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `park_permits` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `permit_number` varchar(50) NOT NULL,
  `park_name` varchar(100) NOT NULL,
  `booking_id` bigint DEFAULT NULL,
  `issue_date` date NOT NULL,
  `valid_date` date NOT NULL,
  `visitor_count` int NOT NULL,
  `total_fee_lkr` decimal(38,2) NOT NULL,
  `status` varchar(50) NOT NULL DEFAULT 'ISSUED',
  `issuing_officer` varchar(150) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `permit_number` (`permit_number`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `park_permits`
--

LOCK TABLES `park_permits` WRITE;
/*!40000 ALTER TABLE `park_permits` DISABLE KEYS */;
INSERT INTO `park_permits` VALUES (1,'DWC-YAL-2026-891','Yala National Park',1,'2026-10-04','2026-10-07',2,11500.00,'ISSUED','Ranger Pradeep Bandara','2026-10-04 05:41:55');
/*!40000 ALTER TABLE `park_permits` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `payment_reference` varchar(50) NOT NULL,
  `booking_id` bigint NOT NULL,
  `amount` decimal(38,2) NOT NULL,
  `payment_method` varchar(50) NOT NULL,
  `payment_status` varchar(50) NOT NULL DEFAULT 'PAID',
  `transaction_date` datetime NOT NULL,
  `bank_slip_image` varchar(255) DEFAULT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `payment_reference` (`payment_reference`),
  KEY `fk_pay_booking` (`booking_id`),
  CONSTRAINT `fk_pay_booking` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
INSERT INTO `payments` VALUES (1,'PAY-2026-9041',1,57000.00,'CARD_SANDBOX','PAID','2026-10-04 11:11:55',NULL,'Online Card Payment Authorized (MasterCard ending in 4242)','2026-10-04 05:41:55'),(2,'PAY-2026-63146',3,34000.00,'CARD_SANDBOX','PAID','2026-10-04 06:12:56',NULL,'Authorized via Sandbox Gateway (Card ending in 2121)','2026-10-04 00:42:56'),(3,'PAY-2026-96327',5,57000.00,'CARD_SANDBOX','REFUNDED','2026-10-04 08:15:08',NULL,'Authorized via Sandbox Gateway (Card ending in 9999)','2026-10-04 02:45:08'),(4,'PAY-BT-2026-6665',7,34000.00,'BANK_TRANSFER','PAID','2026-10-05 07:07:02',NULL,'Bank Transfer Slip Reference: BOC-DEP-998877','2026-10-05 01:37:02'),(5,'PAY-2026-46461',8,35625.00,'CARD_SANDBOX','PAID','2026-10-05 07:15:53',NULL,'Authorized via Sandbox Gateway (Card ending in 2222)','2026-10-05 01:45:53'),(6,'PAY-BT-2026-9841',9,34000.00,'BANK_TRANSFER','REFUNDED','2026-10-05 07:16:17','/uploads/slip_88389e92.jpg','Bank Transfer Slip Reference: dsd','2026-10-05 01:46:17');
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `safari_packages`
--

DROP TABLE IF EXISTS `safari_packages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `safari_packages` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(200) NOT NULL,
  `national_park` varchar(100) NOT NULL,
  `description` text NOT NULL,
  `base_price` decimal(38,2) NOT NULL,
  `duration_days` int NOT NULL DEFAULT '1',
  `difficulty_level` varchar(50) NOT NULL,
  `max_group_size` int NOT NULL DEFAULT '6',
  `status` varchar(50) NOT NULL DEFAULT 'ACTIVE',
  `cover_image` varchar(255) DEFAULT NULL,
  `peak_season_multiplier` decimal(38,2) NOT NULL,
  `itinerary` text NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `safari_packages`
--

LOCK TABLES `safari_packages` WRITE;
/*!40000 ALTER TABLE `safari_packages` DISABLE KEYS */;
INSERT INTO `safari_packages` VALUES (1,'Yala Big 4 Predator Expedition','Yala National Park','Intensive dawn-to-dusk safari tracking the elusive Sri Lankan Leopard (Panthera pardus kotiya), Sloth Bear, Mugger Crocodile, and Asian Elephant across Block 1 and Block 2.',28500.00,1,'MODERATE',6,'ACTIVE','/images/yala_leopard.jpg',1.25,'05:30 AM: Palatupana Park Gate Entry\n06:30 AM: Leopard tracking along Buthawa coastal dunes\n10:30 AM: Menik Ganga resting site and packed naturalist breakfast\n02:00 PM: Deep forest tracking near Sithulpawwa ancient rock\n06:00 PM: Sunset exit and safari debrief','2026-10-04 05:41:55'),(2,'Wilpattu Wilderness & Ancient Villu Trail','Wilpattu National Park','Journey through Sri Lanka’s largest national park featuring natural rainwater lakes (Villus). Famous for tranquil wilderness, high leopard density, barking deer, and magnificent bird life.',34000.00,2,'EASY',6,'ACTIVE','/images/wilpattu_lake.jpg',1.20,'Day 1: 06:00 AM Hunuwilagama gate entry, Kokmotte camp trail, afternoon leopard watch at Kumbuk Wila.\nDay 2: Morning birding expedition at Maradanmaduwa, lake-side picnic, departure by 05:00 PM.','2026-10-04 05:41:55'),(3,'Udawalawe Elephant Sanctuary Safari','Udawalawe National Park','Guaranteed sightings of large elephant herds in open grassland savannas surrounding the reservoir. Perfect for photography enthusiasts and families.',22000.00,1,'EASY',8,'ACTIVE','/images/udawalawe_elephant.jpg',1.15,'06:00 AM: Entry via Udawalawe Main Barrier\n08:00 AM: Reservoir lakeside elephant gathering observation\n11:30 AM: Visit to adjacent Elephant Transit Home during feeding session\n01:30 PM: Midday birding watch and tour wrap-up.','2026-10-04 05:41:55'),(4,'Minneriya Great Elephant Gathering Explorer','Minneriya National Park','Witness Asia’s greatest natural wildlife spectacle where over 300 wild elephants congregate on the dried grass beds of Minneriya Tank during the dry season.',24500.00,1,'EASY',6,'ACTIVE','/images/minneriya_gathering.jpg',1.30,'02:30 PM: Afternoon briefing & 4x4 entry\n03:30 PM: Panoramic Minneriya tank shoreline watch\n05:45 PM: Sunset elephant herd migration\n06:30 PM: Return to base camp.','2026-10-04 05:41:55');
/*!40000 ALTER TABLE `safari_packages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trip_allocations`
--

DROP TABLE IF EXISTS `trip_allocations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `trip_allocations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `booking_id` bigint NOT NULL,
  `guide_id` bigint NOT NULL,
  `vehicle_id` bigint NOT NULL,
  `allocation_date` date NOT NULL,
  `status` varchar(50) NOT NULL DEFAULT 'ASSIGNED',
  `dispatch_notes` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `booking_id` (`booking_id`),
  KEY `fk_alloc_guide` (`guide_id`),
  KEY `fk_alloc_vehicle` (`vehicle_id`),
  CONSTRAINT `fk_alloc_booking` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_alloc_guide` FOREIGN KEY (`guide_id`) REFERENCES `guides` (`id`),
  CONSTRAINT `fk_alloc_vehicle` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trip_allocations`
--

LOCK TABLES `trip_allocations` WRITE;
/*!40000 ALTER TABLE `trip_allocations` DISABLE KEYS */;
INSERT INTO `trip_allocations` VALUES (1,1,2,2,'2026-10-07','ASSIGNED','m','2026-10-04 05:41:55'),(2,2,1,2,'2026-10-11','ASSIGNED','','2026-10-04 00:40:50'),(3,4,3,2,'2026-10-15','ASSIGNED','jj','2026-10-04 10:54:17');
/*!40000 ALTER TABLE `trip_allocations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `full_name` varchar(150) NOT NULL,
  `email` varchar(150) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phone` varchar(20) NOT NULL,
  `role` varchar(50) NOT NULL,
  `profile_picture` varchar(255) DEFAULT NULL,
  `bio` text,
  `region` varchar(100) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'Ruwan Senanayake (Chief Administrator)','admin@safari.lk','admin123','0771234567','ADMIN',NULL,NULL,NULL,'2026-10-04 05:41:55'),(2,'Dulanja Perera (Tour Operations Lead)','operator@safari.lk','operator123','0714567890','TOUR_OPERATOR',NULL,NULL,NULL,'2026-10-04 05:41:55'),(3,'Chaminda Silva (Fleet Dispatcher)','ops@safari.lk','ops123','0729876543','OPERATIONS_MANAGER',NULL,NULL,NULL,'2026-10-04 05:41:55'),(4,'Pradeep Bandara (DWC Ranger / Officer)','ranger@safari.lk','ranger123','0761122334','CONSERVATION_OFFICER',NULL,NULL,NULL,'2026-10-04 05:41:55'),(5,'Nuwan Jayalath (Logistics Supervisor)','logistics@safari.lk','logistics123','0785566778','LOGISTICS_STAFF',NULL,NULL,NULL,'2026-10-04 05:41:55'),(6,'Anjali Wickramasinghe (Finance Manager)','finance@safari.lk','finance123','0759988776','FINANCE_OFFICER',NULL,NULL,NULL,'2026-10-04 05:41:55'),(7,'Sunil Bandara (Licensed Senior Naturalist & Driver)','sunil.bandara@gmail.com','guide123','0772345678','GUIDE',NULL,NULL,NULL,'2026-10-04 05:41:55'),(8,'Kavinda Perera (Tourist)','kavinda.perera@gmail.com','pass123','0777654321','CUSTOMER',NULL,NULL,NULL,'2026-10-04 05:41:55'),(9,'dulaara','dula@gmail.com','1111','1234323232','CUSTOMER',NULL,NULL,NULL,'2026-10-05 01:44:46');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vehicles`
--

DROP TABLE IF EXISTS `vehicles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vehicles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `registration_number` varchar(50) NOT NULL,
  `vehicle_model` varchar(100) NOT NULL,
  `capacity` int NOT NULL DEFAULT '6',
  `condition_status` varchar(50) NOT NULL DEFAULT 'EXCELLENT',
  `status` varchar(50) NOT NULL DEFAULT 'AVAILABLE',
  `last_service_date` date NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `registration_number` (`registration_number`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vehicles`
--

LOCK TABLES `vehicles` WRITE;
/*!40000 ALTER TABLE `vehicles` DISABLE KEYS */;
INSERT INTO `vehicles` VALUES (1,'WP-CAB-4821','Toyota Land Cruiser 79 Safari 4x4 (High Elevated)',5,'GOOD','AVAILABLE','2026-09-01','2026-10-04 05:41:55'),(2,'CP-NA-9024','Land Rover Defender 110 Soft Top Edition',6,'GOOD','AVAILABLE','2026-08-15','2026-10-04 05:41:55'),(3,'SP-GA-3188','Toyota Hilux Revo Custom Safari Cab',8,'GOOD','AVAILABLE','2026-09-10','2026-10-04 05:41:55');
/*!40000 ALTER TABLE `vehicles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wildlife_sightings`
--

DROP TABLE IF EXISTS `wildlife_sightings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wildlife_sightings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `park_name` varchar(100) NOT NULL,
  `species_name` varchar(150) NOT NULL,
  `area_sector` varchar(150) NOT NULL,
  `sighting_timestamp` datetime NOT NULL,
  `animal_count` int NOT NULL DEFAULT '1',
  `observed_behavior` text NOT NULL,
  `recorded_by_guide_id` bigint DEFAULT NULL,
  `status` varchar(50) NOT NULL DEFAULT 'SUBMITTED',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wildlife_sightings`
--

LOCK TABLES `wildlife_sightings` WRITE;
/*!40000 ALTER TABLE `wildlife_sightings` DISABLE KEYS */;
INSERT INTO `wildlife_sightings` VALUES (1,'Yala National Park','Sri Lankan Leopard (Panthera pardus kotiya)','Buthawa Plains Sector 3','2026-10-03 11:11:55',1,'Young adult male resting on rock outcrop in morning sun, healthy condition.',1,'SUBMITTED','2026-10-04 05:41:55'),(2,'Wilpattu National Park','Sloth Bear (Melursus ursinus inornatus)','Kumbuk Wila Margin','2026-10-02 11:11:55',2,'Mother and cub feeding on palu berries, undisturbed by vehicle.',2,'SUBMITTED','2026-10-04 05:41:55');
/*!40000 ALTER TABLE `wildlife_sightings` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-05 12:59:40
