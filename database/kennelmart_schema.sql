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

============================================================================
CREATE TABLE IF NOT EXISTS users (
   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   name VARCHAR(150) NOT NULL,
   idnumber VARCHAR(50) NOT NULL,
   email VARCHAR(255) NOT NULL UNIQUE,
   role VARCHAR(10) NOT NULL DEFAULT 'USER' -- 'ADMIN' or 'USER'
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_idnumber ON users(idnumber);
CREATE INDEX idx_users_role ON users(role);

-- ============================================================================

============================================================================
CREATE TABLE IF NOT EXISTS verified_identities (
   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   idnumber VARCHAR(50) NOT NULL UNIQUE, -- Idnumber
   name VARCHAR(150) NOT NULL,           -- Name
   email VARCHAR(255) NOT NULL UNIQUE    -- Official NU Email
);

CREATE INDEX idx_verified_identities_email ON verified_identities(email);
CREATE INDEX idx_verified_identities_idnumber ON verified_identities(idnumber);
CREATE INDEX idx_verified_identities_name ON verified_identities(name);

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

-- Insert verified identities (sample)
INSERT INTO verified_identities (idnumber, name, email) VALUES
   ('2025-1020735', 'Axel Drake Bagay', 'bagayam@students.nu-laguna.edu.ph'),
   ('2024-00001', 'Juan dela Cruz', 'juan.delacruz@students.nu-laguna.edu.ph'),
   ('2024-00002', 'Maria Santos', 'maria.santos@students.nu-laguna.edu.ph'),
   ('2024-00003', 'Pedro Reyes', 'pedro.reyes@students.nu-laguna.edu.ph');

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
