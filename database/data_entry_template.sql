-- ============================================================================
-- KennelMart Verified Identities - Data Entry Template
-- ============================================================================
-- Copy and modify these INSERT statements to add your verified students/faculty
-- ============================================================================

-- ============================================================================
-- ADD YOUR VERIFIED STUDENTS HERE
-- ============================================================================
-- Template: Replace values and uncomment to add
--
-- Syntax: 
-- INSERT INTO verified_identities (school_id, full_name, email, status, department, program, academic_year)
-- VALUES ('STUDENT_ID', 'Full Name', 'email@nu.edu.ph', 'VERIFIED', 'Department', 'Program', '2023-2024');

-- Example entries (uncomment to use):
--
-- INSERT INTO verified_identities (school_id, full_name, email, status, department, program, academic_year) VALUES
-- ('2025-1020735', 'Axel Drake Bagay', 'bagayam@students.nu-laguna.edu.ph', 'VERIFIED', 'College of Engineering', 'BS Computer Science', '2024-2025'),
-- ('2024-00001', 'Juan dela Cruz', 'juan.delacruz@students.nu-laguna.edu.ph', 'VERIFIED', 'College of Engineering', 'BS Computer Science', '2023-2024'),
-- ('2024-00002', 'Maria Santos', 'maria.santos@students.nu-laguna.edu.ph', 'VERIFIED', 'College of Liberal Arts', 'BS Education', '2023-2024'),
-- ('2024-00003', 'Pedro Reyes', 'pedro.reyes@students.nu-laguna.edu.ph', 'VERIFIED', 'College of Business', 'BS Accountancy', '2023-2024');

-- ============================================================================
-- INSTRUCTIONS FOR MANUAL DATA ENTRY
-- ============================================================================
--
-- 1. Copy the template below
-- 2. Replace the values:
--    - school_id: Student ID from ID card (e.g., "2024-12345")
--    - full_name: Full name (e.g., "John Smith")
--    - email: NU email (must end with @nu.edu.ph)
--    - status: VERIFIED, PENDING, REJECTED, or INACTIVE
--    - department: Academic department
--    - program: Program/Course
--    - academic_year: Year (e.g., "2023-2024")
--
-- 3. Paste into pgAdmin Query Tool or psql
-- 4. Execute the query
-- 5. Verify data was inserted: SELECT * FROM verified_identities;

-- ============================================================================
-- BLANK TEMPLATE - Copy and fill in
-- ============================================================================

-- INSERT INTO verified_identities (school_id, full_name, email, status, department, program, academic_year) VALUES
-- ('', '', '@nu.edu.ph', 'VERIFIED', '', '', '2023-2024');

-- ============================================================================
-- BULK INSERT TEMPLATE - Add multiple students at once
-- ============================================================================

-- INSERT INTO verified_identities (school_id, full_name, email, status, department, program, academic_year) VALUES
-- ('SCHOOL_ID_1', 'Student Name 1', 'email1@students.nu-laguna.edu.ph', 'VERIFIED', 'Department', 'Program', '2023-2024'),
-- ('SCHOOL_ID_2', 'Student Name 2', 'email2@students.nu-laguna.edu.ph', 'VERIFIED', 'Department', 'Program', '2023-2024'),
-- ('SCHOOL_ID_3', 'Student Name 3', 'email3@students.nu-laguna.edu.ph', 'VERIFIED', 'Department', 'Program', '2023-2024');

-- ============================================================================
-- VERIFICATION QUERIES - Check your data
-- ============================================================================

-- Count total verified students
-- SELECT COUNT(*) as total_verified FROM verified_identities WHERE status = 'VERIFIED';

-- View all verified students
-- SELECT school_id, full_name, email, status FROM verified_identities WHERE status = 'VERIFIED' ORDER BY full_name;

-- Find specific student
-- SELECT * FROM verified_identities WHERE email = 'student@nu.edu.ph';

-- Check for duplicates
-- SELECT email, COUNT(*) FROM verified_identities GROUP BY email HAVING COUNT(*) > 1;

-- ============================================================================
-- END OF DATA ENTRY TEMPLATE
-- ============================================================================
