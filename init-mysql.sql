-- One-shot SQL script to create the 'stayfinder' database, tables, and seed data for the MVP homestay finder app.
-- Run this in MySQL command line: mysql -u root -p < init-mysql.sql
-- Assumes password is 'root' as per previous config. Adjust if needed.
-- This creates the database, tables (matching JPA entities), and seeds a host user and 5 homestays in Andhra Pradesh locations.
-- Password for host is encoded with BCrypt (hostpass -> $2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi)

CREATE DATABASE IF NOT EXISTS stayfinder CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE stayfinder;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    role ENUM('USER', 'HOST', 'ADMIN') DEFAULT 'USER'
);

-- Homestays table
CREATE TABLE IF NOT EXISTS homestays (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    host_id BIGINT NOT NULL,
    location VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    price DOUBLE NOT NULL,
    FOREIGN KEY (host_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Available dates for homestays (one-to-many)
CREATE TABLE IF NOT EXISTS homestay_available_dates (
    homestay_id BIGINT NOT NULL,
    available_date DATE NOT NULL,
    PRIMARY KEY (homestay_id, available_date),
    FOREIGN KEY (homestay_id) REFERENCES homestays(id) ON DELETE CASCADE
);

-- Bookings table
CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    homestay_id BIGINT NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'CANCELLED') DEFAULT 'PENDING',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (homestay_id) REFERENCES homestays(id) ON DELETE CASCADE
);

-- Seed data: Create host user (password: hostpass, BCrypt encoded)
INSERT INTO users (email, password, name, role) VALUES 
('host@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Community Host', 'HOST');

-- Get host ID (assume it's 1 after insert)
SET @host_id = (SELECT id FROM users WHERE email = 'host@example.com');

-- Seed 5 homestays in Andhra Pradesh
INSERT INTO homestays (host_id, location, description, price) VALUES 
(@host_id, 'Visakhapatnam', 'Cozy homestay near RK Beach, authentic Andhra hospitality.', 1500.0),
(@host_id, 'Vijayawada', 'Traditional home with home-cooked meals, near Kanaka Durga Temple.', 1200.0),
(@host_id, 'Guntur', 'Family-friendly stay in the heart of Andhra, close to local markets.', 1000.0),
(@host_id, 'Tirupati', 'Spiritual retreat near Tirumala Temple, peaceful and clean.', 2000.0),
(@host_id, 'Kurnool', 'Rustic village homestay with views of Belum Caves nearby.', 800.0);

-- Seed available dates for each homestay (future dates for MVP testing)
-- Homestay 1 (Visakhapatnam)
INSERT INTO homestay_available_dates (homestay_id, available_date) VALUES 
(1, '2025-10-01'), (1, '2025-10-02'), (1, '2025-10-03');

-- Homestay 2 (Vijayawada)
INSERT INTO homestay_available_dates (homestay_id, available_date) VALUES 
(2, '2025-10-05'), (2, '2025-10-06');

-- Homestay 3 (Guntur)
INSERT INTO homestay_available_dates (homestay_id, available_date) VALUES 
(3, '2025-10-10'), (3, '2025-10-11'), (3, '2025-10-12');

-- Homestay 4 (Tirupati)
INSERT INTO homestay_available_dates (homestay_id, available_date) VALUES 
(4, '2025-10-15'), (4, '2025-10-16');

-- Homestay 5 (Kurnool)
INSERT INTO homestay_available_dates (homestay_id, available_date) VALUES 
(5, '2025-10-20'), (5, '2025-10-21'), (5, '2025-10-22');

-- Optional: Seed a regular user for testing
INSERT INTO users (email, password, name, role) VALUES 
('user@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Test User', 'USER');

-- Note: After running this script, restart the Spring Boot app. JPA will handle further updates.
-- Login with: host@example.com / hostpass or user@example.com / hostpass