# KennelMart Backend Changelog

## [Phase 1: Authentication Module] - 2026-05-25

### Added

#### Dependencies
- JJWT (JSON Web Token) library v0.12.3 for JWT authentication
- JUnit 4.13.2 and Spring Boot Test for unit testing

#### Enumerations
- `UserRole` - ADMIN, USER roles (simplified: all users can buy and sell)
- `VerificationStatus` - PENDING, VERIFIED, REJECTED for identity verification
- `AccountStatus` - ACTIVE, SUSPENDED for account management
- `OrderStatus` - ORDER_RECEIVED, ACCEPTED, PREPARING, SHIPPED, DELIVERED, CANCELLED for order lifecycle
- `PaymentMethod` - COD, GCASH, CAMPUS_MEETUP for payment options
- `ProductStatus` - ACTIVE, INACTIVE, SOLD_OUT, PENDING_APPROVAL for product listings
- `ReportStatus` - PENDING, REVIEWING, RESOLVED, DISMISSED for moderation
- `ListingCategory` - ELECTRONICS, BOOKS, CLOTHING, FURNITURE, SERVICES, FOOD_BEVERAGE, SPORTS_RECREATION, STATIONERY, OTHERS

#### Entities
- `BaseEntity` - Abstract base class with UUID id, createdAt, updatedAt timestamps
- `User` - Core user entity with fields: firstName, lastName, email, password, studentOrFacultyId, profileImage, role, verificationStatus, accountStatus
  - Demonstrates OOP: Encapsulation, Inheritance, helper methods (getFullName, isVerified, isActive)
  - Database indexes on email (unique) and studentOrFacultyId

#### Repositories
- `UserRepository` - JPA repository for User persistence
  - Methods: findByEmail(), existsByEmail(), findByStudentOrFacultyId()

#### DTOs (Data Transfer Objects)
- `RegisterRequest` - User registration input with validation
- `LoginRequest` - User login credentials input with validation
- `AuthResponse` - Authentication response with JWT token and user details

#### Services
- `AuthService` (interface) - Authentication service contract
- `AuthServiceImpl` - Implementation of authentication service
  - register() - Register new user with email validation, password hashing, JWT token generation
  - login() - Authenticate user, validate account status, generate JWT token
  - Includes validation: NU Laguna email check (@students.nu-laguna.edu.ph), password matching, account status verification

#### Security Components
- `JwtProvider` - JWT token generation, validation, and claims extraction
  - Uses HS512 algorithm with BCrypt compatible secret keys
  - Configurable expiration time (default: 24 hours)
  - Methods: generateToken(), validateToken(), getUsernameFromToken(), getClaimsFromToken(), isTokenExpired()

- `JwtAuthenticationFilter` - Spring Security filter for JWT authentication
  - Extracts JWT from Authorization header (Bearer scheme)
  - Validates token and loads user details
  - Sets SecurityContext for authenticated requests

#### Configuration
- `SecurityConfig` - Spring Security configuration
  - BCryptPasswordEncoder with strength 12
  - JWT authentication filter integration
  - CORS configuration for frontend (http://localhost:5173, http://localhost:3000)
  - HTTP security policies: stateless session management, public/protected endpoint rules
  - Authorization rules: public auth endpoints, protected user/seller endpoints, admin-only endpoints

- `UserDetailsConfig` - User details service configuration
  - Loads users from database for Spring Security authentication

#### Controllers
- `AuthController` - REST API endpoints for authentication
  - POST /api/auth/register - Register new user (201 Created)
  - POST /api/auth/login - Login with credentials (200 OK)
  - GET /api/auth/me - Get current authenticated user info (200 OK)
  - Full endpoint documentation and validation rules

#### Tests
- `AuthServiceImplTest` - Unit tests for authentication service
  - Tests: registration success, duplicate email, password validation, email validation, login success, suspended account, invalid credentials
  - Uses Mockito for dependency mocking

- `JwtProviderTest` - Unit tests for JWT provider
  - Tests: token generation, username extraction, token validation, token expiration, expiration time retrieval, claims extraction

#### Configuration Files
- `application.properties` - Application configuration
  - PostgreSQL database: jdbc:postgresql://localhost:5432/kennelmart_db
  - JPA/Hibernate: PostgreSQL dialect, DDL auto update, batch processing
  - JWT: configurable secret and expiration time
  - Logging: DEBUG level for application, Spring Security, and Hibernate
  - CORS: configured for local development
  - Server: context path, port 8080, error details

### Architecture Highlights

#### Layered Architecture
```
Controller (AuthController)
    ↓
Service (AuthService/AuthServiceImpl)
    ↓
Repository (UserRepository)
    ↓
Database (PostgreSQL)
```

#### OOP Principles Demonstrated
1. **Encapsulation** - Private fields with getters/setters in User entity
2. **Inheritance** - User extends BaseEntity for common fields
3. **Abstraction** - Interface-based services, abstract BaseEntity class
4. **Single Responsibility** - Each class has one clear purpose
5. **Composition** - User has Role and VerificationStatus enums
6. **Dependency Injection** - Constructor-based injection with @RequiredArgsConstructor
7. **Polymorphism** - Strategy pattern with Spring Security UserDetailsService

#### Security Features
- **Password Security** - BCrypt hashing with strength 12
- **Token Security** - JWT with HS512 algorithm and strong secret keys
- **Email Validation** - NU Laguna email requirement (@students.nu-laguna.edu.ph)
- **Account Status** - Active/Suspended account management
- **Role-Based Access Control** - ADMIN (full access), USER (marketplace access)
- **Stateless Authentication** - JWT-based, no session state

#### Validation
- Backend validation on all inputs
- DTO-based validation with Jakarta validation annotations
- Email uniqueness check
- Password strength requirements (minimum 8 characters)
- Account status verification during login
- NU email domain validation

### Next Steps
- Phase 2: Product Listing Management
- Phase 3: Marketplace Browsing
- Phase 4: Cart and Order System
- Phase 5: Admin Moderation
