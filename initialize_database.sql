DROP DATABASE IF EXISTS tongxiangdb;

-- Create the database
CREATE DATABASE tongxiangdb;
USE tongxiangdb;

-- Create the clients table
CREATE TABLE clients (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         phone VARCHAR(50),
                         email VARCHAR(255),
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create the businesses table
CREATE TABLE businesses (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            client_id BIGINT,
                            principal DOUBLE,
                            term_length INT,
                            interest_rate DOUBLE,
                            overdue_interest_rate DOUBLE,
                            monthly_payment DOUBLE,
                            payment_day INT,
                            next_payment_date DATE,
                            is_cleared BOOLEAN,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE
);

-- Create the assets table
CREATE TABLE assets (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        value DOUBLE,
                        description TEXT,
                        client_id BIGINT,
                        business_id BIGINT,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE,
                        FOREIGN KEY (business_id) REFERENCES businesses(id) ON DELETE SET NULL
);

-- Create the charges table
CREATE TABLE charges (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         payment_amount DOUBLE NOT NULL,
                         remaining_balance DOUBLE,
                         due_date DATE,
                         overdue_interest_rate DOUBLE,
                         status VARCHAR(50),
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create the payments table
CREATE TABLE payments (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          payment_amount DOUBLE NOT NULL,
                          payment_datetime DATETIME NOT NULL,
                          description TEXT,
                          charge_id BIGINT,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (charge_id) REFERENCES charges(id) ON DELETE SET NULL
);


-- Insert 5 sample clients
INSERT INTO clients (name, phone, email) VALUES
                                             ('Alice Smith', '111-222-3333', 'alice.smith@example.com'),
                                             ('Bob Johnson', '222-333-4444', 'bob.johnson@example.com'),
                                             ('Charlie Brown', '333-444-5555', 'charlie.brown@example.com'),
                                             ('Diana Prince', '444-555-6666', 'diana.prince@example.com'),
                                             ('Evan Wright', '555-666-7777', 'evan.wright@example.com');

-- Insert 2 assets for each client
-- Retrieve the auto-generated IDs for each client and insert associated assets
SET @client_id = (SELECT id FROM clients WHERE name = 'Alice Smith');
INSERT INTO assets (name, value, description, client_id) VALUES
                                                             ('Car', 20000.00, 'Toyota Sedan', @client_id),
                                                             ('House', 150000.00, '3-bedroom house', @client_id);

SET @client_id = (SELECT id FROM clients WHERE name = 'Bob Johnson');
INSERT INTO assets (name, value, description, client_id) VALUES
                                                             ('Bike', 500.00, 'Mountain bike', @client_id),
                                                             ('Laptop', 1200.00, 'Gaming laptop', @client_id);

SET @client_id = (SELECT id FROM clients WHERE name = 'Charlie Brown');
INSERT INTO assets (name, value, description, client_id) VALUES
                                                             ('Boat', 30000.00, 'Fishing boat', @client_id),
                                                             ('Watch', 800.00, 'Luxury watch', @client_id);

SET @client_id = (SELECT id FROM clients WHERE name = 'Diana Prince');
INSERT INTO assets (name, value, description, client_id) VALUES
                                                             ('Art Piece', 5000.00, 'Modern art sculpture', @client_id),
                                                             ('Motorcycle', 15000.00, 'Sport motorcycle', @client_id);

SET @client_id = (SELECT id FROM clients WHERE name = 'Evan Wright');
INSERT INTO assets (name, value, description, client_id) VALUES
                                                             ('Tablet', 600.00, 'High-performance tablet', @client_id),
                                                             ('Camera', 2500.00, 'Professional DSLR camera', @client_id);

-- Optional: Verify the inserted records
SELECT * FROM clients;
SELECT * FROM assets;
