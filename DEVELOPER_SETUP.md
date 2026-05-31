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

## �️ All Terminal Commands — Quick Reference

> **Copy and run these in your IDE terminal in order.**

```bash
# ── 1. CLONE (first time only) ───────────────────────────────────────────────────
git clone https://github.com/xemnms/KennelMart.git
cd KennelMart

# ── 2. DATABASE (first time) ────────────────────────────────────────────────
docker run -d --name postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=kennelmart_db -p 5432:5432 postgres:15
docker exec -i postgres psql -U postgres -d kennelmart_db < database/verified_identities.sql

# ── 3. DATABASE (every session) ──────────────────────────────────────────────
docker start postgres

# ── 4. BACKEND (Terminal 1) ────────────────────────────────────────────────
cd kennelmart
mvn spring-boot:run

# ── 5. FRONTEND (Terminal 2) ──────────────────────────────────────────────
cd kennelmart-ui
npm install      # first time only
npm run dev
```

---

## �🚀 How to Set Up KennelMart Locally (IDE)

### Prerequisites

| Tool | Version | Notes |
|---|---|---|
| Java | 21+ | Use SDKMAN or Adoptium |
| Maven | 3.8+ | Bundled `mvnw` wrapper works too |
| Node.js | 18+ | For the React frontend |
| Docker Desktop | Latest | Only requirement for the database |
| IDE | IntelliJ IDEA / VS Code | Any Java-capable IDE |

---

### Step 1: Clone the Repository

> **▶ Run in terminal:**

```bash
git clone https://github.com/xemnms/KennelMart.git
cd KennelMart
```

---

### Step 2: Start the Database (Docker)

> **▶ Run in terminal:**

```bash
# First time only — creates the container
docker run -d \
  --name postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=kennelmart_db \
  -p 5432:5432 \
  postgres:15

# Subsequent sessions — just start the existing container
docker start postgres
```

Seed the verified identities table (run once after creating the container):

> **▶ Run in terminal:**

```bash
docker exec -i postgres psql -U postgres -d kennelmart_db < database/verified_identities.sql
```

> All other tables are created automatically by Hibernate when the backend starts.

---

### Step 3: Backend Configuration

No changes needed. `application.properties` is already configured for Docker:

```
spring.datasource.url=jdbc:postgresql://localhost:5432/kennelmart_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

---

### Step 4: Start the Spring Boot Backend

> **▶ Run in terminal (Terminal 1):**

```bash
cd kennelmart
mvn spring-boot:run
```

Or using the Maven wrapper:

> **▶ Run in terminal (Terminal 1):**

```bash
cd kennelmart
./mvnw spring-boot:run
```

Wait for: `Started KennelmartApplication in X.XXX seconds`

Backend runs at: `http://localhost:8080`

---

### Step 5: Start the React Frontend

> **▶ Run in terminal (Terminal 2):**

```bash
cd kennelmart-ui
npm install       # first time only
npm run dev
```

Frontend runs at: `http://localhost:5173`

---

### Step 6: Register Your Account

> **▶ Run in terminal:**

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Axel Drake Bagay",
    "email": "bagayam@students.nu-laguna.edu.ph",
    "password": "YourSecurePassword123",
    "confirmPassword": "YourSecurePassword123",
    "studentOrFacultyId": "2025-1020735"
  }'
```

**Expected Response (201 Created):**
```json
{
  "userId": "...",
  "name": "Axel Drake Bagay",
  "email": "bagayam@students.nu-laguna.edu.ph",
  "role": "ADMIN",
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400
}
```

### Step 7: Login

> **▶ Run in terminal:**

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "bagayam@students.nu-laguna.edu.ph",
    "password": "YourSecurePassword123"
  }'
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

- [ ] Docker Desktop installed and running
- [ ] Postgres container created and seeded
- [ ] `docker start postgres` — container is up
- [ ] Backend starts: `mvn spring-boot:run` in `kennelmart/`
- [ ] Frontend starts: `npm run dev` in `kennelmart-ui/`
- [ ] Successfully registered with admin account
- [ ] JWT token received on login

---

## 🎯 Next Steps

1. **Start Docker** → `docker start postgres`
2. **Start Backend** → `cd kennelmart && mvn spring-boot:run`
3. **Start Frontend** → `cd kennelmart-ui && npm run dev`
4. **Open browser** → `http://localhost:5173`

---

## 📞 Quick Reference

**Your Credentials:**
```
Name: Axel Drake Bagay
Email: bagayam@students.nu-laguna.edu.ph
Student ID: 2025-1020735
Role: ADMIN
```

**Database (Docker):**
```
Container: postgres
DB name:   kennelmart_db
User:      postgres
Password:  postgres
Port:      5432
```

**URLs:**
```
Backend:  http://localhost:8080
Frontend: http://localhost:5173
```

---

**Your developer account is fully integrated into KennelMart! 🎉**
