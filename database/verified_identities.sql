-- ============================================================================
-- KennelMart Identity Verification Database
-- Purpose: Store verified NU Laguna students/faculty for account registration
-- ============================================================================

-- ============================================================================
-- TABLE: verified_identities
-- Description: Contains list of verified NU Laguna students and faculty
--              Used during registration and login to verify user identity
-- ============================================================================
CREATE TABLE IF NOT EXISTS verified_identities (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    -- Student/Faculty Information from ID Card
    school_id VARCHAR(50) NOT NULL UNIQUE,           -- Student ID card number (e.g., "2024-12345")
    full_name VARCHAR(150) NOT NULL,                 -- Full name from ID card
    email VARCHAR(255) NOT NULL UNIQUE,              -- NU email address (e.g., "student@nu.edu.ph")
    
    -- Verification Status
    status VARCHAR(50) NOT NULL DEFAULT 'VERIFIED',  -- Status: VERIFIED, PENDING, REJECTED, INACTIVE
    
    -- Program/Department Information (Optional)
    department VARCHAR(100),                         -- Department (e.g., "College of Engineering")
    program VARCHAR(100),                            -- Program/Course
    academic_year VARCHAR(10),                       -- Academic year (e.g., "2023-2024")
    
    -- Audit Fields
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    
    -- Constraints
    CONSTRAINT ck_status CHECK (status IN ('VERIFIED', 'PENDING', 'REJECTED', 'INACTIVE'))
);

-- ============================================================================
-- INDEXES - Optimize queries for registration and login
-- ============================================================================

-- Index for email lookups during registration (most frequent query)
CREATE INDEX idx_verified_identities_email 
    ON verified_identities(email) 
    WHERE status = 'VERIFIED';

-- Index for school_id lookups (secondary verification method)
CREATE INDEX idx_verified_identities_school_id 
    ON verified_identities(school_id) 
    WHERE status = 'VERIFIED';

-- Index for status filtering (for admin verification dashboard)
CREATE INDEX idx_verified_identities_status 
    ON verified_identities(status);

-- Combined index for registration check (email + status)
CREATE INDEX idx_verified_identities_email_status 
    ON verified_identities(email, status);

-- ============================================================================
-- COMMENTS - Table and Column Documentation
-- ============================================================================
COMMENT ON TABLE verified_identities IS 
    'Stores verified NU Laguna students and faculty. Used for account verification during registration and login.';

COMMENT ON COLUMN verified_identities.id IS 
    'Unique identifier (UUID)';

COMMENT ON COLUMN verified_identities.school_id IS 
    'Student or Faculty ID from NU ID card. Unique identifier for each person.';

COMMENT ON COLUMN verified_identities.full_name IS 
    'Full name as shown on NU ID card';

COMMENT ON COLUMN verified_identities.email IS 
    'Official NU email address. Must end with @nu.edu.ph';

COMMENT ON COLUMN verified_identities.status IS 
    'Verification status: 
     - VERIFIED: Approved for registration
     - PENDING: Awaiting admin approval
     - REJECTED: Not eligible to register
     - INACTIVE: Was verified but account is no longer active';

COMMENT ON COLUMN verified_identities.department IS 
    'Academic or administrative department';

COMMENT ON COLUMN verified_identities.program IS 
    'Academic program or course';

COMMENT ON COLUMN verified_identities.academic_year IS 
    'Academic year when record was added (e.g., 2023-2024)';

COMMENT ON COLUMN verified_identities.created_at IS 
    'Timestamp when record was created';

COMMENT ON COLUMN verified_identities.updated_at IS 
    'Timestamp when record was last updated';

-- ============================================================================
-- SAMPLE DATA FOR TESTING
-- ============================================================================
-- Insert verified students and faculty
INSERT INTO verified_identities 
    (school_id, full_name, email, status, department, program, academic_year)
VALUES
    -- Developer Account
    ('2025-1020735', 'Axel Drake Bagay', 'bagayam@students.nu-laguna.edu.ph', 'VERIFIED', 'College of Engineering', 'Computer Science', '2024-2025'),
    
    -- Test Students
    ('2024-00001', 'Juan dela Cruz', 'juan.delacruz@students.nu-laguna.edu.ph', 'VERIFIED', 'College of Engineering', 'Computer Science', '2023-2024'),
    ('2024-00002', 'Maria Santos', 'maria.santos@students.nu-laguna.edu.ph', 'VERIFIED', 'College of Liberal Arts', 'English Education', '2023-2024'),
    ('2024-00003', 'Pedro Reyes', 'pedro.reyes@students.nu-laguna.edu.ph', 'VERIFIED', 'College of Business', 'Accountancy', '2023-2024'),
    ('2024-00004', 'Ana Garcia', 'ana.garcia@students.nu-laguna.edu.ph', 'PENDING', 'College of Engineering', 'Civil Engineering', '2023-2024'),
    ('2024-00005', 'Carlos Lopez', 'carlos.lopez@students.nu-laguna.edu.ph', 'REJECTED', 'College of Medicine', 'Medicine', '2022-2023'),
    
    -- Test Faculty
    ('FAC-00001', 'Dr. Robert Johnson', 'robert.johnson@students.nu-laguna.edu.ph', 'VERIFIED', 'College of Engineering', 'Faculty', '2023-2024'),
    ('FAC-00002', 'Prof. Sarah Williams', 'sarah.williams@students.nu-laguna.edu.ph', 'VERIFIED', 'College of Liberal Arts', 'Faculty', '2023-2024')
ON CONFLICT (email) DO NOTHING;  -- Ignore if email already exists

-- ============================================================================
-- USEFUL QUERIES FOR BACKEND INTEGRATION
-- ============================================================================

-- ============================================================================
-- Query 1: Check if email is verified during registration
-- Usage: SELECT * FROM verified_identities WHERE email = ? AND status = 'VERIFIED';
-- ============================================================================
-- Purpose: Validate that email exists in verified list before allowing registration

-- ============================================================================
-- Query 2: Get verified user details by email
-- Usage: SELECT school_id, full_name, email FROM verified_identities 
--        WHERE email = ? AND status = 'VERIFIED';
-- ============================================================================
-- Purpose: Retrieve user info after successful registration

-- ============================================================================
-- Query 3: Check if school_id matches email
-- Usage: SELECT * FROM verified_identities 
--        WHERE email = ? AND school_id = ? AND status = 'VERIFIED';
-- ============================================================================
-- Purpose: Additional verification using school_id

-- ============================================================================
-- Query 4: Get count of verified users by department
-- Usage: SELECT department, COUNT(*) as count FROM verified_identities 
--        WHERE status = 'VERIFIED' GROUP BY department;
-- ============================================================================
-- Purpose: Admin dashboard statistics

-- ============================================================================
-- Query 5: Get pending verification requests (for admin)
-- Usage: SELECT * FROM verified_identities WHERE status = 'PENDING' ORDER BY created_at;
-- ============================================================================
-- Purpose: Admin verification dashboard

-- ============================================================================
-- Query 6: Update status to VERIFIED (admin action)
-- Usage: UPDATE verified_identities SET status = 'VERIFIED', updated_at = NOW() 
--        WHERE id = ?;
-- ============================================================================
-- Purpose: Admin approves verification

-- ============================================================================
-- SETUP INSTRUCTIONS
-- ============================================================================
/*
HOW TO USE THIS FILE:

1. INITIAL SETUP (First time only):
   - Connect to PostgreSQL database for KennelMart
   - Run this entire SQL file: psql kennelmart_db < verified_identities.sql

2. INSERT PRODUCTION DATA:
   - Remove or modify the SAMPLE DATA section above
   - Add actual verified students/faculty data from your NU database
   - Example:
     INSERT INTO verified_identities (school_id, full_name, email, status)
     VALUES 
       ('2024-00100', 'John Smith', 'john.smith@nu.edu.ph', 'VERIFIED'),
       ('2024-00101', 'Jane Doe', 'jane.doe@nu.edu.ph', 'VERIFIED');

3. VERIFY SETUP:
   - Check if table created: SELECT COUNT(*) FROM verified_identities;
   - Check indexes: \d verified_identities (in psql)

4. BACKEND INTEGRATION:
   - The AuthService will query this table during registration
   - The LoginService will query this table during login
   - See queries above for exact SQL needed

5. MAINTENANCE:
   - Add new verified students regularly
   - Update status as needed (VERIFIED, PENDING, REJECTED, INACTIVE)
   - Archive old records if needed
*/

-- ============================================================================
-- END OF VERIFIED IDENTITIES SQL SETUP
-- ============================================================================
