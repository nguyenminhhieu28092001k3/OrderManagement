-- PostgreSQL initialization script for Docker
-- This script will be executed automatically when the container starts

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('admin', 'customer_admin', 'staff')),
    status BOOLEAN DEFAULT true,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create index for performance
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);

-- Insert sample data
INSERT INTO users (username, password, role, status, full_name, email) VALUES
-- Admin user (password: admin123)
('admin', 'admin123', 'admin', true, 'Administrator', 'admin@example.com'),

-- Customer admin users (password: customer123)
('customer_admin1', 'customer123', 'customer_admin', true, 'Nguyễn Văn A', 'customer1@example.com'),
('customer_admin2', 'customer123', 'customer_admin', true, 'Trần Thị B', 'customer2@example.com'),

-- Staff users (password: staff123)
('staff1', 'staff123', 'staff', true, 'Lê Văn C', 'staff1@example.com'),
('staff2', 'staff123', 'staff', true, 'Phạm Thị D', 'staff2@example.com'),
('staff3', 'staff123', 'staff', false, 'Hoàng Văn E', 'staff3@example.com') -- Disabled account
ON CONFLICT (username) DO NOTHING;

-- Show initialization success message
DO $$ 
BEGIN
    RAISE NOTICE 'Database initialized successfully with % users', (SELECT COUNT(*) FROM users);
END $$; 