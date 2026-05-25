# KennelMart Database Schema - Entity Relationship Diagram

## 📊 Current Phase 1 Schema

```
┌────────────────────────────────────────┐
│      verified_identities               │
│      (NU Master List)                  │
├────────────────────────────────────────┤
│ PK │ id: UUID                          │
│    │ school_id: VARCHAR(50) UNIQUE    │◄──┐
│    │ full_name: VARCHAR(150)          │   │
│ U  │ email: VARCHAR(255) UNIQUE       │   │ Referenced during
│    │ status: VARCHAR(50) DEFAULT VERIFIED│ │ registration to
│    │ department: VARCHAR(100)         │   │ verify user identity
│    │ program: VARCHAR(100)            │   │
│    │ academic_year: VARCHAR(10)       │   │
│    │ created_at: TIMESTAMP            │   │
│    │ updated_at: TIMESTAMP            │   │
└────────────────────────────────────────┘   │
                                             │
                  Registration Check         │
                  (email + status verify)    │
                           │                │
                           ▼                │
         ┌─────────────────────────────────────┐
         │         users                       │
         │    (Registered Platform Users)      │
         ├─────────────────────────────────────┤
         │ PK │ id: UUID                       │
         │    │ first_name: VARCHAR(50)        │
         │    │ last_name: VARCHAR(50)         │
         │ U  │ email: VARCHAR(255) UNIQUE  ◄──┼──────┐
         │    │ password: VARCHAR(255)         │       │ Must exist in
         │    │ student_or_faculty_id: VARCHAR │       │ verified_identities
         │    │ profile_image: VARCHAR(500)    │       │ with status=VERIFIED
         │    │ role: VARCHAR(50)              │       │
         │    │ verification_status: VARCHAR   │       │
         │    │ account_status: VARCHAR        │       │
         │    │ created_at: TIMESTAMP          │       │
         │    │ updated_at: TIMESTAMP          │       │
         └─────────────────────────────────────┘       │
                                                       │
                    Constraint Check:                  │
            Email in verified_identities? ────────────┘

Status Values:
┌──────────────────────────────────────────┐
│ verified_identities.status               │
├──────────────────────────────────────────┤
│ VERIFIED ........... Can register        │
│ PENDING ............ Under review        │
│ REJECTED ........... Cannot register     │
│ INACTIVE ........... No longer active    │
└──────────────────────────────────────────┘

┌──────────────────────────────────────────┐
│ users.role                               │
├──────────────────────────────────────────┤
│ BUYER ............. Can purchase items   │
│ SELLER ............ Can sell items       │
│ ADMIN ............. Full platform access │
└──────────────────────────────────────────┘

┌──────────────────────────────────────────┐
│ users.account_status                     │
├──────────────────────────────────────────┤
│ ACTIVE ............ Account is usable    │
│ SUSPENDED ......... Admin blocked user   │
└──────────────────────────────────────────┘
```

---

## 🔑 Key Relationships

### Relationship 1: Email Verification
```
verified_identities.email ──(UNIQUE)──┐
                                       │
                                       └──► users.email (UNIQUE)
                                       
Constraint: User can only register if their email exists 
in verified_identities table with status = 'VERIFIED'
```

### Relationship 2: School ID Reference
```
verified_identities.school_id ───────┐
                                      │
                                      └──► users.student_or_faculty_id
                                      
Purpose: Link verified identity to registered user
Used for: Additional verification and profile matching
```

---

## 📈 Database Flow Diagram

```
User Visits Registration Page
           │
           ▼
┌─────────────────────┐
│ Enters Email        │
└─────────────────────┘
           │
           ▼
┌─────────────────────────────────────────┐
│ Backend Checks:                         │
│ SELECT * FROM verified_identities       │
│ WHERE email = ?                         │
│ AND status = 'VERIFIED'                 │
└─────────────────────────────────────────┘
           │
      ┌────┴────┐
      │          │
     YES        NO
      │          │
      ▼          ▼
   ┌──────┐  ┌─────────────────┐
   │Allow │  │Show Error:      │
   │Reg.  │  │"Not in verified │
   └──────┘  │list. Contact    │
      │      │admin"           │
      │      └─────────────────┘
      ▼
┌──────────────────────┐
│ Completes Form       │
│ - Password           │
│ - Confirm Password   │
│ - First/Last Name    │
└──────────────────────┘
      │
      ▼
┌──────────────────────┐
│ Password Hashed      │
│ (BCrypt)             │
└──────────────────────┘
      │
      ▼
┌──────────────────────────────┐
│ INSERT INTO users            │
│ - email                      │
│ - hashed_password            │
│ - role = BUYER (default)     │
│ - verification_status = YES  │
│ - account_status = ACTIVE    │
└──────────────────────────────┘
      │
      ▼
┌──────────────────────┐
│ Generate JWT Token   │
└──────────────────────┘
      │
      ▼
┌──────────────────────┐
│ Registration Success │
│ Return Token         │
└──────────────────────┘
```

---

## 🔍 Query Relationships

### Query 1: Registration Verification (Most Important)
```sql
-- When user tries to register:
SELECT * FROM verified_identities 
WHERE email = 'user@nu.edu.ph' 
AND status = 'VERIFIED';

-- Result:
--   If FOUND  → User can register
--   If NOT FOUND → Registration rejected
```

### Query 2: Get Verified User Info
```sql
-- Retrieve verified user details:
SELECT school_id, full_name, email 
FROM verified_identities 
WHERE email = 'user@nu.edu.ph' 
AND status = 'VERIFIED';

-- Used to: Pre-populate registration form with verified name
```

### Query 3: Check Duplicate Email
```sql
-- Ensure email is not registered twice:
SELECT COUNT(*) FROM users 
WHERE email = 'user@nu.edu.ph';

-- If count > 0 → Email already registered
-- If count = 0 → Email available
```

### Query 4: Login Verification
```sql
-- When user logs in:
SELECT * FROM users 
WHERE email = 'user@nu.edu.ph' 
AND account_status = 'ACTIVE';

-- Result:
--   If FOUND  → Verify password, return JWT
--   If NOT FOUND → Login rejected (account suspended or not found)
```

---

## 📋 Data Integrity Constraints

### Primary Key Constraints
```
verified_identities.id ──► UUID (auto-generated)
users.id ──► UUID (auto-generated)
```

### Unique Constraints
```
verified_identities.email ──► Must be unique
verified_identities.school_id ──► Must be unique
users.email ──► Must be unique
```

### Check Constraints
```
verified_identities.status ──► IN ('VERIFIED', 'PENDING', 'REJECTED', 'INACTIVE')
users.role ──► IN ('BUYER', 'SELLER', 'ADMIN')
users.account_status ──► IN ('ACTIVE', 'SUSPENDED')
users.verification_status ──► IN ('PENDING', 'VERIFIED', 'REJECTED')
```

### Not Null Constraints
```
verified_identities: school_id, full_name, email, status, created_at, updated_at
users: first_name, last_name, email, password, role, verification_status, 
       account_status, created_at, updated_at
```

---

## 🗂️ Index Strategy

### Performance Indexes

```
verified_identities Indexes (for registration checks):
├── idx_verified_identities_email (email) WHERE status = 'VERIFIED'
│   └── Used for: Fastest registration email check
├── idx_verified_identities_school_id (school_id) WHERE status = 'VERIFIED'
│   └── Used for: School ID verification
├── idx_verified_identities_status (status)
│   └── Used for: Admin dashboard filtering
└── idx_verified_identities_email_status (email, status)
    └── Used for: Combined email + status queries

users Indexes:
├── idx_users_email (email)
│   └── Used for: Login lookups
├── idx_users_student_faculty_id (student_or_faculty_id)
│   └── Used for: User profile lookups
├── idx_users_role (role)
│   └── Used for: Permission filtering
└── idx_users_verification_status (verification_status)
    └── Used for: Admin verification dashboard
```

---

## 🔐 Security Features

### Registration Security
```
1. Email Domain Check (Backend)
   └─ Email must end with @nu.edu.ph

2. Verified List Check (Database)
   └─ Email must exist in verified_identities table
   └─ Status must be 'VERIFIED'

3. Uniqueness Check (Database)
   └─ Email must be unique in both tables
   └─ School_id must be unique in verified_identities

4. Password Security
   └─ Hashed with BCrypt (strength 12) before storage
   └─ Never stored in plain text
```

### Login Security
```
1. Email Lookup
   └─ Find user by email in users table

2. Password Verification
   └─ Compare provided password with stored hash

3. Account Status Check
   └─ User account must be ACTIVE (not SUSPENDED)

4. Token Generation
   └─ JWT token with HS512 signature
   └─ 24-hour expiration
```

---

## 📊 Table Statistics

```
verified_identities:
├─ Typical Records: 1,000 - 10,000 (all NU students/faculty)
├─ Queries: Very frequent (every registration + login verification)
├─ Growth: Slow (only when new students/faculty added)
└─ Index Strategy: Optimized for read-heavy operations

users:
├─ Typical Records: 100 - 1,000 (active platform users)
├─ Queries: Frequent (every login, profile lookup)
├─ Growth: Fast (one new record per registration)
└─ Index Strategy: Optimized for email lookups
```

---

## 🚀 Future Schema Extensions (Phase 2+)

```
Phase 2 (Marketplace):
├─ product_listings
├─ product_images
├─ cart
├─ cart_items
└─ orders & order_items

Phase 3 (Admin Moderation):
├─ reports
└─ audit_logs

Phase 4 (Advanced Features):
├─ messages
├─ reviews
├─ notifications
└─ seller_ratings
```

Each new table will have proper foreign key relationships to `users` table.

---

## ✅ Schema Verification Checklist

- [ ] verified_identities table created
- [ ] users table created
- [ ] All indexes created
- [ ] Sample data inserted
- [ ] Email uniqueness working
- [ ] Status constraints enforced
- [ ] Timestamps auto-populated
- [ ] Connection tested from Spring Boot
- [ ] Registration queries working
- [ ] Login queries working

---

**Current Phase 1 Schema is production-ready!**
