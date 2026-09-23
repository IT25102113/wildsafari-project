-- ==========================================================
-- Wildlife Safari Trip Management System
-- SE2030 Software Engineering Group Project (Group 03)
-- Members & Contributions:
--  1. IT25101916 - Gallage S.P.         (Safari Package Management)
--  2. IT25102113 - Gamarachchi D.A.     (Booking & Reservation Management)
--  3. IT25100156 - Kalhara N.O.         (Guide & Vehicle Allocation)
--  4. IT25101131 - Gunawardhana M.S.    (Conservation & Compliance Management)
--  5. IT25101218 - Dimalsha K.G.T.      (Inventory & Equipment Management)
--  6. IT25101973 - Patabendi M.K.K.     (Payment & Invoice Processing)
-- Target Database: MySQL (XAMPP / MySQL Workbench)
-- Credentials: root / root123
-- ==========================================================

CREATE DATABASE IF NOT EXISTS `safari_db` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `safari_db`;

-- Drop existing tables in reverse dependency order
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `activity_logs`;
DROP TABLE IF EXISTS `invoices`;
DROP TABLE IF EXISTS `payments`;
DROP TABLE IF EXISTS `equipment_allocations`;
DROP TABLE IF EXISTS `equipment_inventory`;
DROP TABLE IF EXISTS `incident_reports`;
DROP TABLE IF EXISTS `wildlife_sightings`;
DROP TABLE IF EXISTS `park_permits`;
DROP TABLE IF EXISTS `trip_allocations`;
DROP TABLE IF EXISTS `vehicles`;
DROP TABLE IF EXISTS `guides`;
DROP TABLE IF EXISTS `booking_participants`;
DROP TABLE IF EXISTS `bookings`;
DROP TABLE IF EXISTS `safari_packages`;
DROP TABLE IF EXISTS `users`;
SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------------------------------------
-- 1. Users Table (Role-based Authentication)
-- ----------------------------------------------------------
CREATE TABLE `users` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `full_name` VARCHAR(150) NOT NULL,
  `email` VARCHAR(150) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  `phone` VARCHAR(20) NOT NULL,
  `role` VARCHAR(50) NOT NULL,
  `profile_picture` VARCHAR(255) DEFAULT NULL,
  `bio` TEXT DEFAULT NULL,
  `region` VARCHAR(100) DEFAULT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------
-- 2. Safari Packages Table (Module 1: Gallage S.P - IT25101916)
-- ----------------------------------------------------------
CREATE TABLE `safari_packages` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(200) NOT NULL,
  `national_park` VARCHAR(100) NOT NULL,
  `description` TEXT NOT NULL,
  `base_price` DECIMAL(10,2) NOT NULL,
  `duration_days` INT NOT NULL DEFAULT 1,
  `difficulty_level` VARCHAR(50) NOT NULL,
  `max_group_size` INT NOT NULL DEFAULT 6,
  `status` VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
  `cover_image` VARCHAR(255) NULL,
  `peak_season_multiplier` DECIMAL(4,2) NOT NULL DEFAULT 1.25,
  `itinerary` TEXT NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------
-- 3. Bookings Table (Module 2: Gamarachchi D.A - IT25102113)
-- ----------------------------------------------------------
CREATE TABLE `bookings` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `booking_reference` VARCHAR(50) NOT NULL UNIQUE,
  `package_id` BIGINT NOT NULL,
  `customer_id` BIGINT NULL,
  `customer_name` VARCHAR(150) NOT NULL,
  `customer_email` VARCHAR(150) NOT NULL,
  `customer_phone` VARCHAR(20) NOT NULL,
  `trip_date` DATE NOT NULL,
  `participant_count` INT NOT NULL,
  `special_requests` TEXT NULL,
  `total_price` DECIMAL(10,2) NOT NULL,
  `booking_status` VARCHAR(50) NOT NULL DEFAULT 'PENDING',
  `payment_status` VARCHAR(50) NOT NULL DEFAULT 'UNPAID',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT `fk_booking_package` FOREIGN KEY (`package_id`) REFERENCES `safari_packages` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------
-- 4. Booking Participants Table
-- ----------------------------------------------------------
CREATE TABLE `booking_participants` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `booking_id` BIGINT NOT NULL,
  `full_name` VARCHAR(150) NOT NULL,
  `id_or_passport` VARCHAR(50) NOT NULL,
  `age` INT NOT NULL,
  `nationality` VARCHAR(100) NOT NULL,
  `emergency_contact` VARCHAR(20) NOT NULL,
  CONSTRAINT `fk_participant_booking` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------
-- 5. Guides Table (Module 3: Kalhara N.O - IT25100156)
-- ----------------------------------------------------------
CREATE TABLE `guides` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `full_name` VARCHAR(150) NOT NULL,
  `license_number` VARCHAR(50) NOT NULL UNIQUE,
  `contact_number` VARCHAR(20) NOT NULL,
  `email` VARCHAR(150) NOT NULL,
  `languages` VARCHAR(200) NOT NULL,
  `experience_years` INT NOT NULL DEFAULT 5,
  `status` VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
  `daily_rate` DECIMAL(10,2) NOT NULL DEFAULT 4500.00,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------
-- 6. Vehicles Table (Module 3: Kalhara N.O - IT25100156)
-- ----------------------------------------------------------
CREATE TABLE `vehicles` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `registration_number` VARCHAR(50) NOT NULL UNIQUE,
  `vehicle_model` VARCHAR(100) NOT NULL,
  `capacity` INT NOT NULL DEFAULT 6,
  `condition_status` VARCHAR(50) NOT NULL DEFAULT 'EXCELLENT',
  `status` VARCHAR(50) NOT NULL DEFAULT 'AVAILABLE',
  `last_service_date` DATE NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------
-- 7. Trip Allocations Table (Module 3: Kalhara N.O - IT25100156)
-- ----------------------------------------------------------
CREATE TABLE `trip_allocations` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `booking_id` BIGINT NOT NULL UNIQUE,
  `guide_id` BIGINT NOT NULL,
  `vehicle_id` BIGINT NOT NULL,
  `allocation_date` DATE NOT NULL,
  `status` VARCHAR(50) NOT NULL DEFAULT 'ASSIGNED',
  `dispatch_notes` TEXT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT `fk_alloc_booking` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_alloc_guide` FOREIGN KEY (`guide_id`) REFERENCES `guides` (`id`),
  CONSTRAINT `fk_alloc_vehicle` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------
-- 8. Park Permits Table (Module 4: Gunawardhana M.S - IT25101131)
-- ----------------------------------------------------------
CREATE TABLE `park_permits` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `permit_number` VARCHAR(50) NOT NULL UNIQUE,
  `park_name` VARCHAR(100) NOT NULL,
  `booking_id` BIGINT NULL,
  `issue_date` DATE NOT NULL,
  `valid_date` DATE NOT NULL,
  `visitor_count` INT NOT NULL,
  `total_fee_lkr` DECIMAL(10,2) NOT NULL,
  `status` VARCHAR(50) NOT NULL DEFAULT 'ISSUED',
  `issuing_officer` VARCHAR(150) NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------
-- 9. Wildlife Sightings Table (Module 4: Gunawardhana M.S - IT25101131)
-- ----------------------------------------------------------
CREATE TABLE `wildlife_sightings` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `park_name` VARCHAR(100) NOT NULL,
  `species_name` VARCHAR(150) NOT NULL,
  `area_sector` VARCHAR(150) NOT NULL,
  `sighting_timestamp` DATETIME NOT NULL,
  `animal_count` INT NOT NULL DEFAULT 1,
  `observed_behavior` TEXT NOT NULL,
  `recorded_by_guide_id` BIGINT NULL,
  `status` VARCHAR(50) NOT NULL DEFAULT 'SUBMITTED',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------
-- 10. Incident Reports Table (Module 4: Gunawardhana M.S - IT25101131)
-- ----------------------------------------------------------
CREATE TABLE `incident_reports` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `incident_number` VARCHAR(50) NOT NULL UNIQUE,
  `park_name` VARCHAR(100) NOT NULL,
  `booking_id` BIGINT NULL,
  `incident_type` VARCHAR(100) NOT NULL,
  `severity` VARCHAR(50) NOT NULL,
  `description` TEXT NOT NULL,
  `action_taken` TEXT NOT NULL,
  `reported_date` DATE NOT NULL,
  `status` VARCHAR(50) NOT NULL DEFAULT 'SUBMITTED',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------
-- 11. Equipment Inventory Table (Module 5: Dimalsha K.G.T - IT25101218)
-- ----------------------------------------------------------
CREATE TABLE `equipment_inventory` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `item_code` VARCHAR(50) NOT NULL UNIQUE,
  `item_name` VARCHAR(150) NOT NULL,
  `category` VARCHAR(100) NOT NULL,
  `total_quantity` INT NOT NULL DEFAULT 0,
  `available_quantity` INT NOT NULL DEFAULT 0,
  `min_threshold` INT NOT NULL DEFAULT 2,
  `condition_status` VARCHAR(50) NOT NULL DEFAULT 'GOOD',
  `location` VARCHAR(100) NOT NULL DEFAULT 'Main Safari Depot',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------
-- 12. Equipment Allocations Table (Module 5: Dimalsha K.G.T - IT25101218)
-- ----------------------------------------------------------
CREATE TABLE `equipment_allocations` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `equipment_id` BIGINT NOT NULL,
  `booking_id` BIGINT NOT NULL,
  `allocated_quantity` INT NOT NULL DEFAULT 1,
  `issued_date` DATE NOT NULL,
  `return_date` DATE NULL,
  `status` VARCHAR(50) NOT NULL DEFAULT 'ISSUED',
  `remarks` TEXT NULL,
  CONSTRAINT `fk_eqalloc_eq` FOREIGN KEY (`equipment_id`) REFERENCES `equipment_inventory` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_eqalloc_booking` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------
-- 13. Payments Table (Module 6: Patabendi M.K.K - IT25101973)
-- ----------------------------------------------------------
CREATE TABLE `payments` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `payment_reference` VARCHAR(50) NOT NULL UNIQUE,
  `booking_id` BIGINT NOT NULL,
  `amount` DECIMAL(10,2) NOT NULL,
  `payment_method` VARCHAR(50) NOT NULL,
  `payment_status` VARCHAR(50) NOT NULL DEFAULT 'PAID',
  `transaction_date` DATETIME NOT NULL,
  `bank_slip_image` VARCHAR(255) NULL,
  `remarks` VARCHAR(255) NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT `fk_pay_booking` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------
-- 14. Invoices Table (Module 6: Patabendi M.K.K - IT25101973)
-- ----------------------------------------------------------
CREATE TABLE `invoices` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `invoice_number` VARCHAR(50) NOT NULL UNIQUE,
  `payment_id` BIGINT NOT NULL,
  `booking_id` BIGINT NOT NULL,
  `subtotal` DECIMAL(10,2) NOT NULL,
  `tax_amount` DECIMAL(10,2) NOT NULL,
  `total_amount` DECIMAL(10,2) NOT NULL,
  `invoice_date` DATE NOT NULL,
  `status` VARCHAR(50) NOT NULL DEFAULT 'ISSUED',
  `notes` VARCHAR(255) NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT `fk_inv_payment` FOREIGN KEY (`payment_id`) REFERENCES `payments` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_inv_booking` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------
-- 15. Activity Logs Table (Audit Trail for Admin)
-- ----------------------------------------------------------
CREATE TABLE `activity_logs` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_email` VARCHAR(150) NOT NULL,
  `role` VARCHAR(50) NOT NULL,
  `module_name` VARCHAR(100) NOT NULL,
  `action` VARCHAR(100) NOT NULL,
  `details` TEXT NOT NULL,
  `ip_address` VARCHAR(50) DEFAULT '127.0.0.1',
  `timestamp` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==========================================================
-- REALISTIC INITIAL SEED DATA
-- Sri Lankan Wildlife Safari Context (No AI placeholders)
-- ==========================================================

-- 1. Users
INSERT INTO `users` (`full_name`, `email`, `password`, `phone`, `role`) VALUES
('Ruwan Senanayake (Chief Administrator)', 'admin@safari.lk', 'admin123', '0771234567', 'ADMIN'),
('Dulanja Perera (Tour Operations Lead)', 'operator@safari.lk', 'operator123', '0714567890', 'TOUR_OPERATOR'),
('Chaminda Silva (Fleet Dispatcher)', 'ops@safari.lk', 'ops123', '0729876543', 'OPERATIONS_MANAGER'),
('Pradeep Bandara (DWC Ranger / Officer)', 'ranger@safari.lk', 'ranger123', '0761122334', 'CONSERVATION_OFFICER'),
('Nuwan Jayalath (Logistics Supervisor)', 'logistics@safari.lk', 'logistics123', '0785566778', 'LOGISTICS_STAFF'),
('Anjali Wickramasinghe (Finance Manager)', 'finance@safari.lk', 'finance123', '0759988776', 'FINANCE_OFFICER'),
('Kavinda Perera (Tourist)', 'kavinda.perera@gmail.com', 'pass123', '0777654321', 'CUSTOMER');

-- 2. Safari Packages
INSERT INTO `safari_packages` (`name`, `national_park`, `description`, `base_price`, `duration_days`, `difficulty_level`, `max_group_size`, `status`, `cover_image`, `peak_season_multiplier`, `itinerary`) VALUES
('Yala Big 4 Predator Expedition', 'Yala National Park', 'Intensive dawn-to-dusk safari tracking the elusive Sri Lankan Leopard (Panthera pardus kotiya), Sloth Bear, Mugger Crocodile, and Asian Elephant across Block 1 and Block 2.', 28500.00, 1, 'MODERATE', 6, 'ACTIVE', '/images/yala_leopard.jpg', 1.25, '05:30 AM: Palatupana Park Gate Entry\n06:30 AM: Leopard tracking along Buthawa coastal dunes\n10:30 AM: Menik Ganga resting site and packed naturalist breakfast\n02:00 PM: Deep forest tracking near Sithulpawwa ancient rock\n06:00 PM: Sunset exit and safari debrief'),
('Wilpattu Wilderness & Ancient Villu Trail', 'Wilpattu National Park', 'Journey through Sri Lanka’s largest national park featuring natural rainwater lakes (Villus). Famous for tranquil wilderness, high leopard density, barking deer, and magnificent bird life.', 34000.00, 2, 'EASY', 6, 'ACTIVE', '/images/wilpattu_lake.jpg', 1.20, 'Day 1: 06:00 AM Hunuwilagama gate entry, Kokmotte camp trail, afternoon leopard watch at Kumbuk Wila.\nDay 2: Morning birding expedition at Maradanmaduwa, lake-side picnic, departure by 05:00 PM.'),
('Udawalawe Elephant Sanctuary Safari', 'Udawalawe National Park', 'Guaranteed sightings of large elephant herds in open grassland savannas surrounding the reservoir. Perfect for photography enthusiasts and families.', 22000.00, 1, 'EASY', 8, 'ACTIVE', '/images/udawalawe_elephant.jpg', 1.15, '06:00 AM: Entry via Udawalawe Main Barrier\n08:00 AM: Reservoir lakeside elephant gathering observation\n11:30 AM: Visit to adjacent Elephant Transit Home during feeding session\n01:30 PM: Midday birding watch and tour wrap-up.'),
('Minneriya Great Elephant Gathering Explorer', 'Minneriya National Park', 'Witness Asia’s greatest natural wildlife spectacle where over 300 wild elephants congregate on the dried grass beds of Minneriya Tank during the dry season.', 24500.00, 1, 'EASY', 6, 'ACTIVE', '/images/minneriya_gathering.jpg', 1.30, '02:30 PM: Afternoon briefing & 4x4 entry\n03:30 PM: Panoramic Minneriya tank shoreline watch\n05:45 PM: Sunset elephant herd migration\n06:30 PM: Return to base camp.');

-- 3. Guides
INSERT INTO `guides` (`full_name`, `license_number`, `contact_number`, `email`, `languages`, `experience_years`, `status`, `daily_rate`) VALUES
('Sunil Bandara (Licensed Senior Naturalist)', 'DWC-LK-4091', '0772345678', 'sunil.bandara@gmail.com', 'English, Sinhala, German', 14, 'ACTIVE', 5500.00),
('Chathura Dissanayake (Expert Wildlife Tracker)', 'DWC-LK-4822', '0713456789', 'chathura.d@gmail.com', 'English, Sinhala, French', 9, 'ACTIVE', 4800.00),
('Kasun Rathnayake (Avian Specialist)', 'DWC-LK-5104', '0764567890', 'kasun.rathna@gmail.com', 'English, Sinhala', 6, 'ACTIVE', 4200.00);

-- 4. Vehicles
INSERT INTO `vehicles` (`registration_number`, `vehicle_model`, `capacity`, `condition_status`, `status`, `last_service_date`) VALUES
('WP-CAB-4821', 'Toyota Land Cruiser 79 Safari 4x4 (High Elevated)', 6, 'EXCELLENT', 'AVAILABLE', '2026-09-01'),
('CP-NA-9024', 'Land Rover Defender 110 Soft Top Edition', 6, 'EXCELLENT', 'AVAILABLE', '2026-08-15'),
('SP-GA-3188', 'Toyota Hilux Revo Custom Safari Cab', 8, 'GOOD', 'AVAILABLE', '2026-09-10');

-- 5. Equipment Inventory
INSERT INTO `equipment_inventory` (`item_code`, `item_name`, `category`, `total_quantity`, `available_quantity`, `min_threshold`, `condition_status`, `location`) VALUES
('EQ-OPT-01', 'Nikon Monarch 8x42 Waterproof Binoculars', 'OPTICS', 10, 8, 3, 'GOOD', 'Yala Base Depot'),
('EQ-NAV-01', 'Garmin inReach Explorer+ GPS Communicator', 'NAVIGATION', 6, 5, 2, 'GOOD', 'Wilpattu Operations Office'),
('EQ-SAF-01', 'Tactical Field Trauma First-Aid Backpack', 'SAFETY', 8, 8, 2, 'GOOD', 'Medical Emergency Bay'),
('EQ-COM-01', 'Motorola VHF Long-Range Bush Radios', 'COMMUNICATION', 12, 10, 4, 'GOOD', 'Equipment Lockup'),
('EQ-CMP-01', 'Outback 4-Person All-Weather Safari Tent', 'CAMPING', 5, 1, 2, 'GOOD', 'Camping Depot');

-- 6. Sample Bookings
INSERT INTO `bookings` (`booking_reference`, `package_id`, `customer_id`, `customer_name`, `customer_email`, `customer_phone`, `trip_date`, `participant_count`, `special_requests`, `total_price`, `booking_status`, `payment_status`) VALUES
('WS-2026-10492', 1, 7, 'Kavinda Perera', 'kavinda.perera@gmail.com', '0777654321', DATE_ADD(CURDATE(), INTERVAL 3 DAY), 2, 'Binoculars rental requested. Focus on high-resolution wildlife photography.', 57000.00, 'CONFIRMED', 'PAID'),
('WS-2026-10493', 3, 7, 'Ananya Jayawardena', 'ananya.j@gmail.com', '0718899221', DATE_ADD(CURDATE(), INTERVAL 7 DAY), 4, 'Elderly passenger onboard, request smooth track route.', 88000.00, 'PENDING', 'UNPAID');

-- 7. Trip Allocation for Confirmed Booking
INSERT INTO `trip_allocations` (`booking_id`, `guide_id`, `vehicle_id`, `allocation_date`, `status`, `dispatch_notes`) VALUES
(1, 1, 1, DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'ASSIGNED', 'VIP Guests. Senior Naturalist Sunil Bandara assigned with high-elevation Land Cruiser WP-CAB-4821.');

-- 8. Payments & Invoices for Confirmed Booking
INSERT INTO `payments` (`payment_reference`, `booking_id`, `amount`, `payment_method`, `payment_status`, `transaction_date`, `remarks`) VALUES
('PAY-2026-9041', 1, 57000.00, 'CARD_SANDBOX', 'PAID', NOW(), 'Online Card Payment Authorized (MasterCard ending in 4242)');

INSERT INTO `invoices` (`invoice_number`, `payment_id`, `booking_id`, `subtotal`, `tax_amount`, `total_amount`, `invoice_date`, `status`, `notes`) VALUES
('INV-2026-8801', 1, 1, 51818.18, 5181.82, 57000.00, CURDATE(), 'ISSUED', 'Includes DWC park entrance levies and 4x4 off-road permit fees.');

-- 9. Conservation Wildlife Sightings
INSERT INTO `wildlife_sightings` (`park_name`, `species_name`, `area_sector`, `sighting_timestamp`, `animal_count`, `observed_behavior`, `recorded_by_guide_id`, `status`) VALUES
('Yala National Park', 'Sri Lankan Leopard (Panthera pardus kotiya)', 'Buthawa Plains Sector 3', NOW() - INTERVAL 1 DAY, 1, 'Young adult male resting on rock outcrop in morning sun, healthy condition.', 1, 'SUBMITTED'),
('Wilpattu National Park', 'Sloth Bear (Melursus ursinus inornatus)', 'Kumbuk Wila Margin', NOW() - INTERVAL 2 DAY, 2, 'Mother and cub feeding on palu berries, undisturbed by vehicle.', 2, 'SUBMITTED');

-- 10. Park Permits
INSERT INTO `park_permits` (`permit_number`, `park_name`, `booking_id`, `issue_date`, `valid_date`, `visitor_count`, `total_fee_lkr`, `status`, `issuing_officer`) VALUES
('DWC-YAL-2026-891', 'Yala National Park', 1, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 2, 11500.00, 'ISSUED', 'Ranger Pradeep Bandara');

-- 11. Initial Activity Logs
INSERT INTO `activity_logs` (`user_email`, `role`, `module_name`, `action`, `details`) VALUES
('system', 'SYSTEM', 'Core System', 'INITIALIZE', 'System initialized with production seed data for SE2030 Group 03.'),
('admin@safari.lk', 'ADMIN', 'Safari Package Management', 'CREATE_PACKAGE', 'Published new safari expedition: Yala Big 4 Predator Expedition.'),
('kavinda.perera@gmail.com', 'CUSTOMER', 'Booking Management', 'CREATE_BOOKING', 'Created confirmed reservation WS-2026-10492 for Yala Safari.');
