-- 1. Create the Database 
CREATE DATABASE new_data;
USE new_data;

-- 2. Create the Parent Table: Department
CREATE TABLE department (
    dept_id INT AUTO_INCREMENT PRIMARY KEY,
    dept_name VARCHAR(50) NOT NULL UNIQUE
);

-- 3. Create the Child Table: Subscription
CREATE TABLE subscription (
    subs_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    dept_id INT NOT NULL,
    monthly_cost DECIMAL(10, 2) NOT NULL,
    billing_cycle VARCHAR(30) NOT NULL,
    renewal_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    seat_count INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (dept_id) REFERENCES department(dept_id) 
    ON DELETE RESTRICT ON UPDATE CASCADE
);

-- 4. Seed Initial Data: Insert your exact corporate department records
INSERT INTO department (dept_id, dept_name) VALUES 
(2267, 'Administration'),
(2491, 'Business Development'),
(1533, 'Customer Support'),
(1187, 'Finance & Accounting'),
(1042, 'Human Resources (HR)'),
(1679, 'Information Technology (IT)'),
(1910, 'Legal'),
(2148, 'Logistics/Supply Chain'),
(1398, 'Marketing'),
(1456, 'Operations'),
(2035, 'Procurement/Purchasing'),
(1821, 'Production/Manufacturing'),
(2389, 'Quality Assurance (QA)'),
(1744, 'Research & Development (R&D)'),
(1265, 'Sales');