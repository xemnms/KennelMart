# KennelMart Database Files

This folder contains all SQL scripts and documentation for setting up and managing the KennelMart database.

---

## 📁 Files Overview

### 1. **kennelmart_schema.sql** (Start Here)
**Purpose:** Complete database schema setup  
**Contains:**
- User registration and verification workflow documentation
- CREATE TABLE statements for `users` and `verified_identities`
- Index creation for performance optimization
- Sample test data
- Query examples for backend integration

**How to use:**
```bash
# Create database first
createdb -U postgres kennelmart_db

# Run this file
psql -U postgres -d kennelmart_db -f database/kennelmart_schema.sql
```

---

### 2. **verified_identities.sql**
**Purpose:** Standalone verified identities table setup  
**Contains:**
- Detailed verified_identities table creation
- Comprehensive column documentation
- Status constraint checks
- Performance indexes
- Useful query examples for backend

**Use case:** If you want to set up just the verification table separately

---

### 3. **data_entry_template.sql**
**Purpose:** Easy template for manually adding verified students/faculty  
**Contains:**
- Blank INSERT templates
- Bulk insert examples
- Verification queries to check your data
- Step-by-step instructions

**How to use:**
1. Copy template
2. Fill in student/faculty data
3. Paste into pgAdmin or psql
4. Execute

---

## 📋 Database Tables

### Table 1: verified_identities
**Purpose:** Master list of verified NU Laguna students and faculty

| Column | Type | Key | Nullable | Description |
|--------|------|-----|----------|-------------|
| id | UUID | PRIMARY | NO | Unique identifier |
| school_id | VARCHAR(50) | UNIQUE | NO | Student/Faculty ID from ID card |
| full_name | VARCHAR(150) | | NO | Full name from ID card |
| email | VARCHAR(255) | UNIQUE | NO | NU email (@nu.edu.ph) |

**Indexes:**
- `idx_verified_identities_email` (email WHERE status = 'VERIFIED')
- `idx_verified_identities_school_id` (school_id WHERE status = 'VERIFIED')
- `idx_verified_identities_status` (status)
- `idx_verified_identities_email_status` (email, status)

**Key Points:**
- Email and school_id must be UNIQUE
- Only entries with status = 'VERIFIED' can register
- Indexed for fast lookups during registration

---

### Table 2: users
**Purpose:** Registered platform users

| Column | Type | Key | Nullable | Description |
|--------|------|-----|----------|-------------|
| id | UUID | PRIMARY | NO | Unique identifier |
| first_name | VARCHAR(50) | | NO | User's first name |
| last_name | VARCHAR(50) | | NO | User's last name |
| email | VARCHAR(255) | UNIQUE | NO | User's email |
| password | VARCHAR(255) | | NO | BCrypt hashed password |
| school_id | VARCHAR(50) | | YES | NU ID number |
| profile_image | VARCHAR(500) | | YES | Profile photo URL |
| role | VARCHAR(50) | | NO | BUYER, SELLER, or ADMIN |
| verification_status | VARCHAR(50) | | NO | PENDING, VERIFIED, REJECTED |
| account_status | VARCHAR(50) | | NO | ACTIVE or SUSPENDED |
| created_at | TIMESTAMP | | NO | Account creation time |
| updated_at | TIMESTAMP | | NO | Last update time |

**Indexes:**
- `idx_users_email` (email)
- `idx_users_school_id` (school_id)
- `idx_users_role` (role)
- `idx_users_verification_status` (verification_status)

**Key Points:**
- Created when user registers via REST API
- Password is hashed with BCrypt before storage
- Email must exist in verified_identities table to register

---

## 🔄 Registration Flow

```
1. User Registration Request (POST /api/auth/register)
   ├── Email provided: juan.delacruz@nu.edu.ph
   │
2. Backend Verification Check
   ├── Query: SELECT * FROM verified_identities 
   │          WHERE email = 'juan.delacruz@nu.edu.ph' 
   │          AND status = 'VERIFIED'
   │
3. Result Check
   ├── ✓ Found & Verified → Allow registration
   │   └── Create user entry in 'users' table
   │   └── Set verification_status = VERIFIED
   │   └── Return JWT token
   │
   └── ✗ Not Found or Not Verified → Reject registration
       └── Show error: "Email not in verified NU list"
```

---

## 📊 Data Entry Checklist

When adding verified students/faculty:

- [ ] Student ID is unique
- [ ] Email ends with @nu.edu.ph
- [ ] Email is unique
- [ ] Name matches ID card exactly
- [ ] Status is one of: VERIFIED, PENDING, REJECTED, INACTIVE
- [ ] Department is documented
- [ ] No typos in email

---

## 🧪 Testing Your Setup

### 1. Verify Tables Exist
```sql
-- In psql
\dt
-- Should show: verified_identities, users
```

### 2. Check Sample Data
```sql
SELECT COUNT(*) FROM verified_identities;
-- Should return: 7 (if using sample data)
```

### 3. Test Registration Query
```sql
SELECT * FROM verified_identities 
WHERE email = 'juan.delacruz@nu.edu.ph' 
AND status = 'VERIFIED';
-- Should return: 1 record
```

### 4. Test API Registration
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Juan",
    "lastName": "dela Cruz",
    "email": "juan.delacruz@students.nu-laguna.edu.ph",
    "password": "SecurePass123",
    "confirmPassword": "SecurePass123",
    "schoolId": "2024-00001"
  }'
# Should return: 201 Created with JWT token
```

---

## ⚙️ Setup Steps Summary

1. **Create Database:**
   ```bash
   createdb -U postgres kennelmart_db
   ```

2. **Run Schema:**
   ```bash
   psql -U postgres -d kennelmart_db -f database/kennelmart_schema.sql
   ```

3. **Add Verified Students** (using data_entry_template.sql):
   - Open pgAdmin
   - Query Tool
   - Paste INSERT statement
   - Execute

4. **Update application.properties:**
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/kennelmart_db
   spring.datasource.username=postgres
   spring.datasource.password=your_password
   ```

5. **Restart Spring Boot:**
   ```bash
   mvn spring-boot:run
   ```

6. **Test Registration:**
   - Register with verified email
   - Should succeed and return JWT token

---

## 🔐 Security Considerations

1. **Email Validation:** Backend validates @nu.edu.ph domain
2. **Password Hashing:** All passwords stored as BCrypt hashes
3. **Uniqueness:** Email and school_id are unique constraints
4. **Status Verification:** Only VERIFIED records can register
5. **Account Suspension:** Admins can suspend accounts by setting account_status = SUSPENDED
6. **Audit Trail:** created_at and updated_at track all changes

---

## 🛠️ Common SQL Operations

### Add Single Student
```sql
INSERT INTO verified_identities (school_id, full_name, email, status, department, program, academic_year)
VALUES ('2024-99999', 'John Smith', 'john.smith@nu.edu.ph', 'VERIFIED', 'College of Engineering', 'CS', '2023-2024');
```

### Add Multiple Students
```sql
INSERT INTO verified_identities (school_id, full_name, email, status, department, program, academic_year) VALUES
('2024-99001', 'Name 1', 'email1@nu.edu.ph', 'VERIFIED', 'College', 'Program', '2023-2024'),
('2024-99002', 'Name 2', 'email2@nu.edu.ph', 'VERIFIED', 'College', 'Program', '2023-2024');
```

### Update Status
```sql
UPDATE verified_identities SET status = 'VERIFIED' WHERE email = 'student@nu.edu.ph';
```

### Delete Record
```sql
DELETE FROM verified_identities WHERE email = 'student@nu.edu.ph';
```

### Suspend User
```sql
UPDATE users SET account_status = 'SUSPENDED' WHERE email = 'user@nu.edu.ph';
```

### View All Verified
```sql
SELECT school_id, full_name, email FROM verified_identities WHERE status = 'VERIFIED' ORDER BY full_name;
```

---

## 📖 Additional Documentation

- **DATABASE_SETUP.md** - Step-by-step setup guide
- **CHANGELOG.md** - Feature implementation history
- **PHASE_1_SUMMARY.md** - Authentication module details

---

## ✅ Quick Verification

```bash
# Connect to database
psql -U postgres -d kennelmart_db

# Check tables
\dt

# Count verified students
SELECT COUNT(*) FROM verified_identities WHERE status = 'VERIFIED';

# View sample data
SELECT school_id, full_name, email FROM verified_identities LIMIT 5;

# Exit
\q
```

---

**Database setup is ready! You can now manually input your verified students/faculty data.**

Use `data_entry_template.sql` for easy data entry via pgAdmin or psql command line.
