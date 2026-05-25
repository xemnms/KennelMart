-- ============================================================================
-- KennelMart PostgreSQL Database Schema
-- Purpose: Complete database setup for MVP
-- ============================================================================

-- ============================================================================
-- DATABASE INITIALIZATION
-- ============================================================================
-- Create database (if not exists) - run this separately
-- CREATE DATABASE kennelmart_db OWNER postgres;

-- ============================================================================
-- ENABLE EXTENSIONS
-- ============================================================================
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";  -- For UUID generation

-- ============================================================================
-- TABLE 1: users
-- Description: Platform users (buyers, sellers, admins)
-- ============================================================================
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    -- Personal Information
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    student_or_faculty_id VARCHAR(50),
    profile_image VARCHAR(500),
    
    -- Account Status
    role VARCHAR(50) NOT NULL DEFAULT 'USER',           -- ADMIN, USER (all users can buy and sell)
    verification_status VARCHAR(50) NOT NULL,           -- PENDING, VERIFIED, REJECTED
    account_status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, SUSPENDED
    
    -- Audit Fields
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    
    -- Constraints
    CONSTRAINT ck_user_role CHECK (role IN ('ADMIN', 'USER')),
    CONSTRAINT ck_verification_status CHECK (verification_status IN ('PENDING', 'VERIFIED', 'REJECTED')),
    CONSTRAINT ck_account_status CHECK (account_status IN ('ACTIVE', 'SUSPENDED'))
);

-- Create indexes for users table
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_student_faculty_id ON users(student_or_faculty_id);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_verification_status ON users(verification_status);

-- ============================================================================
-- TABLE 2: verified_identities
-- Description: Master list of verified NU Laguna students and faculty
--              Used to validate users during registration
-- ============================================================================
CREATE TABLE IF NOT EXISTS verified_identities (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    -- Student/Faculty Information from ID Card
    school_id VARCHAR(50) NOT NULL UNIQUE,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    
    -- Status: Whether this person can register
    status VARCHAR(50) NOT NULL DEFAULT 'VERIFIED',  -- VERIFIED, PENDING, REJECTED, INACTIVE
    
    -- Optional Information
    department VARCHAR(100),
    program VARCHAR(100),
    academic_year VARCHAR(10),
    
    -- Audit Fields
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    
    -- Constraints
    CONSTRAINT ck_verified_status CHECK (status IN ('VERIFIED', 'PENDING', 'REJECTED', 'INACTIVE'))
);

-- Create indexes for verified_identities table
CREATE INDEX idx_verified_identities_email 
    ON verified_identities(email) WHERE status = 'VERIFIED';
CREATE INDEX idx_verified_identities_school_id 
    ON verified_identities(school_id) WHERE status = 'VERIFIED';
CREATE INDEX idx_verified_identities_status ON verified_identities(status);
CREATE INDEX idx_verified_identities_email_status 
    ON verified_identities(email, status);

-- ============================================================================
-- REGISTRATION & VERIFICATION WORKFLOW
-- ============================================================================
/*
FLOW:

1. USER VISITS REGISTRATION PAGE
   ↓
2. USER ENTERS EMAIL
   ↓
3. SYSTEM CHECKS: SELECT * FROM verified_identities WHERE email = ? AND status = 'VERIFIED'
   ↓
4a. EMAIL FOUND → Allow registration
    - User continues with registration form
    - Registration creates entry in 'users' table with verification_status = 'VERIFIED'
    
4b. EMAIL NOT FOUND → Show error
    - "Your email is not in the verified NU list. Contact admin."
    ↓
5. USER COMPLETES REGISTRATION
   ↓
6. PASSWORD HASHED with BCrypt
   ↓
7. USER SAVED TO 'users' TABLE
   ↓
8. JWT TOKEN GENERATED
   ↓
9. USER CAN NOW LOGIN

LOGIN WORKFLOW:
1. USER ENTERS EMAIL + PASSWORD
2. SYSTEM CHECKS: SELECT * FROM users WHERE email = ? AND account_status = 'ACTIVE'
3. PASSWORD VERIFIED
4. JWT TOKEN GENERATED
5. LOGIN SUCCESS
*/

-- ============================================================================
-- SAMPLE DATA FOR TESTING
-- ============================================================================

-- Insert verified students and faculty
INSERT INTO verified_identities 
    (school_id, full_name, email, status, department, program, academic_year)
VALUES
    -- Developer Account
    ('2025-1020735', 'Axel Drake Bagay', 'bagayam@students.nu-laguna.edu.ph', 'VERIFIED', 'College of Engineering', 'Computer Science', '2024-2025'),
    
    -- Verified Students (Sample Data for Testing)
    ('2024-00001', 'Juan dela Cruz', 'juan.delacruz@students.nu-laguna.edu.ph', 'VERIFIED', 'College of Engineering', 'Computer Science', '2023-2024'),
    ('2024-00002', 'Maria Santos', 'maria.santos@students.nu-laguna.edu.ph', 'VERIFIED', 'College of Liberal Arts', 'English Education', '2023-2024'),
    ('2024-00003', 'Pedro Reyes', 'pedro.reyes@students.nu-laguna.edu.ph', 'VERIFIED', 'College of Business', 'Accountancy', '2023-2024'),
    ('2024-00004', 'Ana Garcia', 'ana.garcia@students.nu-laguna.edu.ph', 'PENDING', 'College of Engineering', 'Civil Engineering', '2023-2024'),
    ('2024-00005', 'Carlos Lopez', 'carlos.lopez@students.nu-laguna.edu.ph', 'REJECTED', 'College of Medicine', 'Medicine', '2022-2023'),
    
    -- Verified Faculty
    ('FAC-00001', 'Dr. Robert Johnson', 'robert.johnson@students.nu-laguna.edu.ph', 'VERIFIED', 'College of Engineering', 'Faculty', '2023-2024'),
    ('FAC-00002', 'Prof. Sarah Williams', 'sarah.williams@students.nu-laguna.edu.ph', 'VERIFIED', 'College of Liberal Arts', 'Faculty', '2023-2024')
ON CONFLICT (email) DO NOTHING;

-- ============================================================================
-- VERIFICATION QUERIES FOR BACKEND
-- ============================================================================

/*
QUERY 1: Check if email is verified during registration
Purpose: Before allowing registration, verify the email exists in verified list

SELECT * FROM verified_identities 
WHERE email = $1 AND status = 'VERIFIED' LIMIT 1;

Parameters: [userEmail]
Returns: Record if verified, null if not found/not verified
Action: 
  - If found → Allow user to continue registration
  - If not found → Show error and prevent registration
*/

/*
QUERY 2: Get verified user details
Purpose: Retrieve user info from verification table

SELECT school_id, full_name, email FROM verified_identities 
WHERE email = $1 AND status = 'VERIFIED' LIMIT 1;

Parameters: [userEmail]
Returns: Verified user details
Action: Can pre-fill user name and ID in registration form
*/

/*
QUERY 3: Verify with both email and school_id
Purpose: Extra validation layer

SELECT * FROM verified_identities 
WHERE email = $1 AND school_id = $2 AND status = 'VERIFIED' LIMIT 1;

Parameters: [userEmail, studentId]
Returns: Record if both match, null otherwise
Action: Security check - ensure provided ID matches verified records
*/

/*
QUERY 4: Get admin statistics
Purpose: Dashboard for admin verification

SELECT COUNT(*) as total_verified FROM verified_identities WHERE status = 'VERIFIED';
SELECT COUNT(*) as total_pending FROM verified_identities WHERE status = 'PENDING';
SELECT COUNT(*) as total_registered FROM users;
*/

-- ============================================================================
-- HOW TO RUN THIS SETUP
-- ============================================================================
/*
STEP 1: Create Database (if not exists)
  psql -U postgres
  CREATE DATABASE kennelmart_db;
  \q

STEP 2: Run this SQL file
  psql -U postgres -d kennelmart_db -f database/kennelmart_schema.sql

STEP 3: Verify tables created
  psql -U postgres -d kennelmart_db
  \dt  -- List all tables
  \d verified_identities  -- Show table structure
  SELECT COUNT(*) FROM verified_identities;  -- Check data

STEP 4: Update connection string in application.properties
  spring.datasource.url=jdbc:postgresql://localhost:5432/kennelmart_db
  spring.datasource.username=postgres
  spring.datasource.password=your_password
*/

-- ============================================================================
-- END OF SCHEMA
-- ============================================================================
