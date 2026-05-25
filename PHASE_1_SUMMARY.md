# Phase 1: Authentication Module - Implementation Summary

**Status:** ✅ COMPLETE  
**Date:** May 25, 2026  
**Sprint:** Phase 1 - Foundation

---

## Overview

Phase 1 establishes the foundational authentication and security infrastructure for KennelMart. This phase implements:
- User registration with NU email validation
- JWT-based authentication and authorization
- Role-based access control (RBAC)
- Secure password handling with BCrypt
- Spring Security integration
- Comprehensive unit tests

---

## What Was Built

### 1. Core Domain Model

#### Enumerations (8 total)
| Enum | Purpose | Values |
|------|---------|--------|
| `UserRole` | User access levels | ADMIN, USER |
| `VerificationStatus` | Identity verification states | PENDING, VERIFIED, REJECTED |
| `AccountStatus` | Account management | ACTIVE, SUSPENDED |
| `OrderStatus` | Order lifecycle | ORDER_RECEIVED, ACCEPTED, PREPARING, SHIPPED, DELIVERED, CANCELLED |
| `PaymentMethod` | Payment options | COD, GCASH, CAMPUS_MEETUP |
| `ProductStatus` | Product listing states | ACTIVE, INACTIVE, SOLD_OUT, PENDING_APPROVAL |
| `ReportStatus` | Moderation states | PENDING, REVIEWING, RESOLVED, DISMISSED |
| `ListingCategory` | Product categories | ELECTRONICS, BOOKS, CLOTHING, FURNITURE, SERVICES, FOOD_BEVERAGE, SPORTS_RECREATION, STATIONERY, OTHERS |

#### Entity Model
```
BaseEntity (Abstract)
├── id: UUID
├── createdAt: LocalDateTime
└── updatedAt: LocalDateTime

User extends BaseEntity
├── firstName: String
├── lastName: String
├── email: String (unique)
├── password: String (hashed)
├── studentOrFacultyId: String
├── profileImage: String
├── role: UserRole
├── verificationStatus: VerificationStatus
└── accountStatus: AccountStatus
```

### 2. API Layer (REST Endpoints)

| Method | Endpoint | Authentication | Response |
|--------|----------|-----------------|----------|
| POST | `/api/auth/register` | Public | 201 Created + AuthResponse |
| POST | `/api/auth/login` | Public | 200 OK + AuthResponse |
| GET | `/api/auth/me` | JWT Required | 200 OK + User Info |

### 3. Authentication Flow

```
User Registration:
1. POST /api/auth/register with credentials
2. Validate NU Laguna email (@students.nu-laguna.edu.ph)
3. Hash password with BCrypt (strength 12)
4. Save user to database (VERIFIED if in verified_identities table)
5. Generate JWT token
6. Return AuthResponse with token

User Login:
1. POST /api/auth/login with email/password
2. Authenticate via Spring SecurityAuthenticationManager
3. Validate account status (must be ACTIVE)
4. Generate JWT token
5. Return AuthResponse with token

Protected Requests:
1. Client sends Authorization: Bearer <token>
2. JwtAuthenticationFilter extracts token
3. JwtProvider validates token
4. Load user details from database
5. Set SecurityContext
6. Allow request to proceed
```

### 4. Database Schema

**users table:**
```sql
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    student_or_faculty_id VARCHAR(50),
    profile_image VARCHAR(500),
    role VARCHAR(50) NOT NULL,
    verification_status VARCHAR(50) NOT NULL,
    account_status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    INDEX idx_email (email),
    INDEX idx_student_faculty_id (student_or_faculty_id)
);
```

### 5. Security Implementation

#### Password Security
- BCryptPasswordEncoder with strength 12
- Passwords hashed before storage
- Never stored in plain text

#### JWT Token Security
- Algorithm: HMAC-SHA512
- Expiration: 24 hours (configurable)
- Claims: username, issued_at, expiration
- Secret key: Environment-based (must be changed in production)

#### Authorization
- Stateless authentication (no server-side sessions)
- Role-based access control:
  - Public endpoints: `/api/auth/**`
  - Protected endpoints: `/api/cart/**`, `/api/orders/**` (requires any authenticated role)
  - Admin endpoints: `/api/admin/**` (requires ADMIN role)
  - Seller endpoints: `/api/listings/**` (requires SELLER or ADMIN)

#### CORS Configuration
- Allowed origins: localhost:5173, localhost:3000 (frontend dev servers)
- Allowed methods: GET, POST, PUT, DELETE, OPTIONS
- Credentials: Enabled for cookie-based auth compatibility

### 6. Code Quality

#### OOP Principles Demonstrated

| Principle | Implementation |
|-----------|-----------------|
| **Encapsulation** | Private fields in User entity with getters/setters |
| **Inheritance** | User extends BaseEntity for common audit fields |
| **Abstraction** | AuthService interface, abstract BaseEntity class |
| **Composition** | User has UserRole, VerificationStatus, AccountStatus |
| **Polymorphism** | Spring Security UserDetailsService implementation |
| **Single Responsibility** | Each class has one clear purpose |
| **Dependency Injection** | Constructor-based with @RequiredArgsConstructor |
| **Interface Segregation** | Service interfaces define only required methods |

#### Validation
- **Email validation:** Must be NU Laguna email (@students.nu-laguna.edu.ph)
- **Password validation:** Minimum 8 characters, must match confirmation
- **Verified Identity:** Email must exist in verified_identities table with status = VERIFIED
- **Uniqueness:** Email must be unique in database
- **Account status:** Must be ACTIVE to login
- **DTO validation:** Jakarta validation annotations on request objects

#### Error Handling
- Custom exception messages for user feedback
- Proper HTTP status codes (201, 200, 400, 401, 403, 500)
- Logging at appropriate levels (INFO, WARN, DEBUG, ERROR)

### 7. Testing

#### Test Coverage

**AuthServiceImplTest** (9 test cases)
- ✅ Registration success
- ✅ Duplicate email rejection
- ✅ Password mismatch detection
- ✅ Short password rejection
- ✅ Non-NU email rejection
- ✅ Login success
- ✅ Suspended account rejection
- ✅ Invalid credentials rejection
- ✅ Mocking and verification

**JwtProviderTest** (7 test cases)
- ✅ Token generation
- ✅ Username extraction
- ✅ Token validation
- ✅ Token expiration detection
- ✅ Expiration time retrieval
- ✅ Claims extraction
- ✅ Invalid token rejection

**Total:** 16 unit tests covering core authentication logic

### 8. Configuration Files

#### application.properties
```properties
# Database: PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/kennelmart_db
spring.datasource.username=postgres
spring.datasource.password=postgres

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update

# JWT
jwt.secret=<your-secret-key>
jwt.expiration=86400000 (24 hours)

# CORS
cors.allowed-origins=http://localhost:5173,http://localhost:3000

# Logging
logging.level.root=INFO
logging.level.com.kennel.mart.kennelmart=DEBUG
```

#### Dependencies Added
```xml
<!-- JWT -->
<jjwt-api>0.12.3</jjwt-api>
<jjwt-impl>0.12.3</jjwt-impl>
<jjwt-jackson>0.12.3</jjwt-jackson>

<!-- Testing -->
<junit>4.13.2</junit>
<spring-boot-starter-test></spring-boot-starter-test>
```

---

## Architecture Diagram

```
HTTP Request
    ↓
SpringSecurityFilterChain
    ↓
JwtAuthenticationFilter (extracts & validates JWT)
    ↓
AuthController
    ├── POST /register → AuthService.register()
    ├── POST /login → AuthService.login()
    └── GET /me → Current user info
    ↓
AuthServiceImpl
    ├── Password encoding (BCryptPasswordEncoder)
    ├── User validation & persistence
    └── JWT token generation
    ↓
UserRepository (JPA)
    ↓
PostgreSQL Database
```

---

## Validation Rules

### Registration Constraints
- **Email:** Must match pattern `*@students.nu-laguna.edu.ph`
- **Password:** Minimum 8 characters, must match confirmation
- **First/Last Name:** 2-50 characters each
- **Student/Faculty ID:** Required, must be non-blank
- **Verified Identity:** Email must exist in verified_identities table with status = VERIFIED
- **Uniqueness:** Email must not already exist

### Login Constraints
- **Email:** Valid email format, must exist in database
- **Password:** Must match stored (hashed) password
- **Account Status:** Must be ACTIVE (not SUSPENDED)

### JWT Token Constraints
- **Expiration:** 24 hours from issuance
- **Algorithm:** HMAC-SHA512
- **Signature:** Verified using environment secret key
- **Header Format:** "Bearer <token>"

---

## File Structure

```
src/main/java/com/kennel/mart/kennelmart/
├── config/
│   ├── SecurityConfig.java
│   └── UserDetailsConfig.java
├── controller/
│   └── AuthController.java
├── dto/
│   ├── AuthResponse.java
│   ├── LoginRequest.java
│   └── RegisterRequest.java
├── entity/
│   ├── BaseEntity.java
│   └── User.java
├── enums/
│   ├── AccountStatus.java
│   ├── ListingCategory.java
│   ├── OrderStatus.java
│   ├── PaymentMethod.java
│   ├── ProductStatus.java
│   ├── ReportStatus.java
│   ├── UserRole.java
│   └── VerificationStatus.java
├── repository/
│   └── UserRepository.java
├── security/
│   ├── JwtAuthenticationFilter.java
│   └── JwtProvider.java
├── service/
│   ├── AuthService.java
│   └── impl/
│       └── AuthServiceImpl.java
└── KennelmartApplication.java

src/test/java/com/kennel/mart/kennelmart/
├── security/
│   └── JwtProviderTest.java
└── service/impl/
    └── AuthServiceImplTest.java
```

---

## How to Use

### Register a New User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Axel",
    "lastName": "Bagay",
    "email": "bagayam@students.nu-laguna.edu.ph",
    "password": "SecurePass123",
    "confirmPassword": "SecurePass123",
    "studentOrFacultyId": "2025-1020735"
  }'
```

Response (201 Created):
```json
{
  "userId": "uuid-here",
  "firstName": "Axel",
  "lastName": "Bagay",
  "email": "bagayam@students.nu-laguna.edu.ph",
  "role": "ADMIN",
  "verificationStatus": "VERIFIED",
  "accountStatus": "ACTIVE",
  "accessToken": "eyJhbGc...",
  "tokenType": "Bearer",
  "expiresIn": 86400
}
```

### Login User
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@nu.edu.ph",
    "password": "SecurePass123"
  }'
```

### Access Protected Endpoint
```bash
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer eyJhbGc..."
```

---

## Known Limitations & Future Improvements

### Current Phase 1 Limitations
- ❌ No email verification (manual verification by admin)
- ❌ No password reset functionality
- ❌ No two-factor authentication
- ❌ No refresh token mechanism
- ❌ No rate limiting on login attempts

### Planned for Future Phases
- ✅ Identity verification module (Phase 2)
- ✅ Email verification with OTP
- ✅ Password reset functionality
- ✅ Refresh token implementation
- ✅ Login attempt rate limiting
- ✅ Two-factor authentication

---

## Testing & Validation

### How to Run Tests
```bash
cd kennelmart
mvn test
```

### How to Run Application
```bash
cd kennelmart
mvn spring-boot:run
```

### Swagger/API Documentation
Once the application is running:
- API docs available at: `/v3/api-docs`
- Swagger UI available at: `/swagger-ui.html` (if springdoc-openapi is added)

---

## Dependencies

| Dependency | Version | Purpose |
|------------|---------|---------|
| Spring Boot | 4.0.6 | Framework |
| Java | 21 | Language |
| PostgreSQL | Latest | Database |
| JJWT | 0.12.3 | JWT handling |
| Lombok | Latest | Boilerplate reduction |
| Spring Security | 4.0.6 | Security framework |
| JUnit | 4.13.2 | Unit testing |

---

## Next Phase: Product Listing Management

Phase 2 will implement:
- ✅ ProductListing entity
- ✅ ProductImage entity
- ✅ ListingService for CRUD operations
- ✅ REST API for product management
- ✅ Seller dashboard endpoints
- ✅ Category filtering

---

**Implemented by:** KennelMart Development Team  
**Review Status:** Ready for Code Review  
**QA Status:** Unit Tests Passing  
**Deployment Ready:** Yes (requires database setup)
