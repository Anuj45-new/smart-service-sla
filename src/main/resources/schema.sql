-- ====================================================================
-- Smart Service Request & SLA Escalation System
-- Database Schema for MySQL 8+
-- Target Database: smart_service_db
-- ====================================================================

CREATE DATABASE IF NOT EXISTS smart_service_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE smart_service_db;

-- --------------------------------------------------------------------
-- 1. Table: users
-- --------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role ENUM('ADMIN', 'AGENT', 'USER') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_role (role)
) ENGINE=InnoDB;

-- --------------------------------------------------------------------
-- 2. Table: categories
-- --------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- --------------------------------------------------------------------
-- 3. Table: service_requests
-- --------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS service_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    category_id BIGINT NOT NULL,
    priority ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') NOT NULL,
    status ENUM('OPEN', 'ASSIGNED', 'IN_PROGRESS', 'RESOLVED', 'CLOSED', 'ESCALATED') NOT NULL DEFAULT 'OPEN',
    created_by BIGINT NOT NULL,
    assigned_to BIGINT NULL,
    sla_hours INT NOT NULL,
    sla_deadline DATETIME NOT NULL,
    is_sla_breached BOOLEAN NOT NULL DEFAULT FALSE,
    resolved_at DATETIME NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_sr_category FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT fk_sr_creator FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_sr_assignee FOREIGN KEY (assigned_to) REFERENCES users(id),
    INDEX idx_sr_status (status),
    INDEX idx_sr_priority (priority),
    INDEX idx_sr_deadline (sla_deadline),
    INDEX idx_sr_breached (is_sla_breached)
) ENGINE=InnoDB;

-- --------------------------------------------------------------------
-- 4. Table: request_history
-- --------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS request_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL,
    old_value VARCHAR(100) NULL,
    new_value VARCHAR(100) NULL,
    remarks VARCHAR(500) NULL,
    performed_by BIGINT NOT NULL,
    performed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rh_request FOREIGN KEY (request_id) REFERENCES service_requests(id) ON DELETE CASCADE,
    CONSTRAINT fk_rh_user FOREIGN KEY (performed_by) REFERENCES users(id),
    INDEX idx_rh_request (request_id)
) ENGINE=InnoDB;

-- --------------------------------------------------------------------
-- 5. Table: escalations
-- --------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS escalations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_id BIGINT NOT NULL UNIQUE,
    reason VARCHAR(255) NOT NULL,
    breached_at DATETIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_esc_request FOREIGN KEY (request_id) REFERENCES service_requests(id) ON DELETE CASCADE,
    INDEX idx_esc_breached_at (breached_at)
) ENGINE=InnoDB;

-- --------------------------------------------------------------------
-- Sample Seed Data
-- Default password for all seed users is: password123
-- BCrypt hash below generated with work factor 12
-- --------------------------------------------------------------------
INSERT INTO categories (name, description) VALUES
('Hardware', 'Issues related to laptops, monitors, keyboards, printers, etc.'),
('Software & Access', 'OS issues, software installations, access permissions, VPN'),
('Network & Infrastructure', 'Wi-Fi, Ethernet, DNS, server accessibility problems'),
('Facilities & Workplace', 'Power outlets, air conditioning, seating and desk maintenance')
ON DUPLICATE KEY UPDATE name=name;

-- Passwords: 'password123' hashed with BCrypt
INSERT INTO users (username, password_hash, full_name, email, role) VALUES
('admin', '$2a$12$e7k1U9r3pQ1jVvOxW4mJreWsmqH8Mcfk5xOQY0xWffZJ6n36GZqU.', 'System Administrator', 'admin@smartservice.org', 'ADMIN'),
('agent_john', '$2a$12$e7k1U9r3pQ1jVvOxW4mJreWsmqH8Mcfk5xOQY0xWffZJ6n36GZqU.', 'John Doe (IT Support)', 'john.doe@smartservice.org', 'AGENT'),
('agent_sarah', '$2a$12$e7k1U9r3pQ1jVvOxW4mJreWsmqH8Mcfk5xOQY0xWffZJ6n36GZqU.', 'Sarah Connor (Network Eng)', 'sarah.connor@smartservice.org', 'AGENT'),
('user_alice', '$2a$12$e7k1U9r3pQ1jVvOxW4mJreWsmqH8Mcfk5xOQY0xWffZJ6n36GZqU.', 'Alice Smith', 'alice.smith@smartservice.org', 'USER'),
('user_bob', '$2a$12$e7k1U9r3pQ1jVvOxW4mJreWsmqH8Mcfk5xOQY0xWffZJ6n36GZqU.', 'Bob Williams', 'bob.williams@smartservice.org', 'USER')
ON DUPLICATE KEY UPDATE username=username;
