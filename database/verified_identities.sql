-- ============================================================================
-- Verified Identities – Reference data for allowed NU Laguna students/faculty
-- ============================================================================

-- Create the table if it doesn't exist (matches VerifiedIdentity entity)
CREATE TABLE IF NOT EXISTS verified_identities (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id VARCHAR(50) NOT NULL UNIQUE,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);

-- Indexes for faster lookups
CREATE INDEX IF NOT EXISTS idx_verified_identities_email ON verified_identities(email);
CREATE INDEX IF NOT EXISTS idx_verified_identities_school_id ON verified_identities(school_id);

-- ============================================================================
-- SAMPLE DATA (for testing)
-- ============================================================================
INSERT INTO verified_identities (school_id, full_name, email) VALUES
    ('2025-1020735', 'Axel Drake Bagay', 'bagayam@students.nu-laguna.edu.ph'),
    ('2024-00001', 'Juan dela Cruz', 'juan.delacruz@students.nu-laguna.edu.ph'),
    ('2024-00002', 'Maria Santos', 'maria.santos@students.nu-laguna.edu.ph'),
    ('2024-00003', 'Pedro Reyes', 'pedro.reyes@students.nu-laguna.edu.ph'),
    ('FAC-00001', 'Dr. Robert Johnson', 'robert.johnson@students.nu-laguna.edu.ph'),
    ('FAC-00002', 'Prof. Sarah Williams', 'sarah.williams@students.nu-laguna.edu.ph')
ON CONFLICT (email) DO NOTHING;