# Developer Account Setup - Axel Drake Bagay

**Date:** May 25, 2026  
**Status:** ✅ COMPLETE

---

## Your Account Information

| Field | Value |
|-------|-------|
| **Name** | Axel Drake Bagay |
| **Student Number** | 2025-1020735 |
| **Email** | bagayam@students.nu-laguna.edu.ph |
| **School** | National University Laguna |
| **Domain** | @students.nu-laguna.edu.ph |
| **Role** | ADMIN |
| **Status** | VERIFIED |

---

## What Was Updated

### ✅ 1. SQL Database Files

All SQL files now include your account as the first verified entry:

```sql
INSERT INTO verified_identities (school_id, full_name, email)
VALUES ('2025-1020735', 'Axel Drake Bagay', 'bagayam@students.nu-laguna.edu.ph');
```

**Files Updated:**
- ✅ `kennelmart_schema.sql` - Main schema file
- ✅ `verified_identities.sql` - Verification table setup
- ✅ `data_entry_template.sql` - Data entry template

---

### ✅ 2. Email Domain Changed

The email domain has been updated **globally** from `@nu.edu.ph` to `@students.nu-laguna.edu.ph`

**Backend Files Updated:**
- ✅ `AuthServiceImpl.java` - Email validation logic
- ✅ `AuthController.java` - API documentation

**Test Files Updated:**
- ✅ `AuthServiceImplTest.java` - All test cases
- ✅ `JwtProviderTest.java` - All token tests

**Documentation Updated:**
- ✅ `DATABASE_SETUP.md` - Setup guide
- ✅ `PHASE_1_SUMMARY.md` - Phase 1 documentation
- ✅ `CHANGELOG.md` - Change log

---

### ✅ 3. Role Model Simplified

**Old Model:** BUYER, SELLER, ADMIN  
**New Model:** ADMIN, USER
- **ADMIN:** Full system access, manage users and moderation
- **USER:** Regular users who can buy AND sell

**Your Account:** ADMIN role  
**Other Users:** USER role (all can buy and sell)

---

## 🚀 How to Set Up PostgreSQL and KennelMart

### Step 1: Verify PostgreSQL Installation
```bash
# Check if PostgreSQL is running
psql --version

# If not installed, install it (Ubuntu/Debian):
sudo apt-get update
sudo apt-get install postgresql postgresql-contrib

# Start PostgreSQL service:
sudo service postgresql start

# Or on other systems:
sudo systemctl start postgresql
```

### Step 2: Create Database
```bash
# Create the database for KennelMart
createdb -U postgres kennelmart_db

# Verify creation:
psql -U postgres -d kennelmart_db -c "SELECT 1"
```

### Step 3: Run Database Schema
```bash
# Navigate to workspace
cd /workspaces/KennelMart

# Run the setup script (automated):
bash database/setup.sh

# Or manually run the schema:
psql -U postgres -d kennelmart_db -f database/kennelmart_schema.sql
```

### Step 4: Verify Database Setup
```bash
# Login to the database
psql -U postgres -d kennelmart_db

# Check tables created:
\dt

# Check verified identities (you should see yourself):
SELECT * FROM verified_identities WHERE email = 'bagayam@students.nu-laguna.edu.ph';

# Exit:
\q
```

**Expected output:**
```
     id     |  school_id  |     full_name      |           email            | status  | ...
 ---------- | ----------- | ------------------ | -------------------------- | ------- |
 [uuid]     | 2025-1020735| Axel Drake Bagay   | bagayam@students...edu.ph  | VERIFIED|
```

### Step 5: Update Application Configuration
Edit `kennelmart/src/main/resources/application.properties`:

```properties
# PostgreSQL Connection
spring.datasource.url=jdbc:postgresql://localhost:5432/kennelmart_db
spring.datasource.username=postgres
spring.datasource.password=postgres  # Change if your password is different

# Database settings
spring.jpa.hibernate.ddl-auto=update
```

### Step 6: Start the Spring Boot Application
```bash
# Navigate to backend
cd kennelmart

# Build and run
mvn spring-boot:run

# Or run with Maven wrapper
./mvnw spring-boot:run
```

**Expected output:**
```
... KennelmartApplication : Started KennelmartApplication in X seconds
... DispatcherServlet : Initializing Servlet 'dispatcherServlet'
```

### Step 7: Test Registration with Your Account
```bash
# Register with your credentials
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Axel",
    "lastName": "Bagay",
    "email": "bagayam@students.nu-laguna.edu.ph",
    "password": "YourSecurePassword123",
    "confirmPassword": "YourSecurePassword123",
    "schoolId": "2025-1020735"
  }'
```

**Expected Response (201 Created):**
```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "firstName": "Axel",
  "lastName": "Bagay",
  "email": "bagayam@students.nu-laguna.edu.ph",
  "role": "ADMIN",
  "verificationStatus": "VERIFIED",
  "accountStatus": "ACTIVE",
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400
}
```

### Step 8: Test Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "bagayam@students.nu-laguna.edu.ph",
    "password": "YourSecurePassword123"
  }'
```

**Expected Response (200 OK):**
```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "firstName": "Axel",
  "lastName": "Bagay",
  "email": "bagayam@students.nu-laguna.edu.ph",
  "role": "ADMIN",
  "verificationStatus": "VERIFIED",
  "accountStatus": "ACTIVE",
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400
}
```

### Step 9: Get Your Current User Info
```bash
# Use the token from login response
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

---

## 📋 Email Domain Information

**Old Domain:** `@nu.edu.ph`  
**New Domain:** `@students.nu-laguna.edu.ph` ✅

### Validation Rules
- ✅ Email **must** end with `@students.nu-laguna.edu.ph`
- ✅ Email **must** exist in verified_identities table
- ✅ Status **must** be 'VERIFIED'
- ✅ Password minimum 8 characters

---

## 🔐 Security Features

Your account includes:
- ✅ BCrypt password hashing (strength 12)
- ✅ JWT token authentication (24-hour expiration)
- ✅ Role-based access control (ADMIN role)
- ✅ Email verification via verified_identities table
- ✅ Account status management (ACTIVE by default)

---

## 👥 User Roles Explained

### ADMIN Role (Your Account)
- Full system access
- Can manage users
- Can handle moderation
- Can perform all marketplace operations

### USER Role (All Other Users)
- Can **buy** items on the marketplace
- Can **sell** items on the marketplace
- Can manage their own listings
- Cannot access admin endpoints

**Key Change:** All users can now both buy AND sell (no separate BUYER/SELLER roles)

---

## 📚 Sample Test Users

Besides your account, the database also includes these test users:

| Name | Email | Status | Role |
|------|-------|--------|------|
| Juan dela Cruz | juan.delacruz@students.nu-laguna.edu.ph | VERIFIED | USER |
| Maria Santos | maria.santos@students.nu-laguna.edu.ph | VERIFIED | USER |
| Pedro Reyes | pedro.reyes@students.nu-laguna.edu.ph | VERIFIED | USER |
| Dr. Robert Johnson | robert.johnson@students.nu-laguna.edu.ph | VERIFIED | USER |

You can test registration and login with any of these verified emails. They will all get the USER role.

---

## 📋 Endpoint Access Control

| Endpoint | PUBLIC | USER | ADMIN |
|----------|--------|------|-------|
| /api/auth/** | ✅ | ✅ | ✅ |
| /api/listings/** | ❌ | ✅ | ✅ |
| /api/cart/** | ❌ | ✅ | ✅ |
| /api/orders/** | ❌ | ✅ | ✅ |
| /api/admin/** | ❌ | ❌ | ✅ |

---

## ✅ Verification Checklist

Before starting development:

- [ ] PostgreSQL installed and running
- [ ] Database created: `kennelmart_db`
- [ ] Schema executed successfully
- [ ] Your account inserted in verified_identities table
- [ ] Spring Boot application can connect to database
- [ ] Successfully registered with your account
- [ ] JWT token received
- [ ] Able to login with credentials

---

## 🎯 Next Steps

1. **Database Setup** → Run the SQL schema file
2. **Verify Data** → Check your account in the database
3. **Update Config** → Update application.properties
4. **Start Backend** → Run `mvn spring-boot:run`
5. **Test Registration** → Use curl to register
6. **Test Login** → Use curl to login
7. **Proceed to Phase 2** → Product Listing Management

---

## 📞 Quick Reference

**Your Credentials:**
```
Email: bagayam@students.nu-laguna.edu.ph
Student ID: 2025-1020735
Domain: @students.nu-laguna.edu.ph (for all NU students)
```

**Database:**
```
Name: kennelmart_db
User: postgres
Table: verified_identities
Status: VERIFIED ✅
```

**Backend Validation:**
```
Email pattern: *@students.nu-laguna.edu.ph
Password: minimum 8 characters
Verified status: Must be 'VERIFIED' in database
```

---

**Your developer account is fully integrated into the KennelMart backend! 🎉**

Start by setting up the PostgreSQL database, then run the application to test your registration and login.
