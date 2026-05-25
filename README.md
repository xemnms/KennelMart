# KennelMart - Campus Marketplace for NU Laguna

## Overview

KennelMart is a peer-to-peer campus marketplace platform designed for National University Laguna students and faculty. Users can **buy and sell** items within the campus community with secure payments and transaction management.

**Tagline:** Treat on your way. 🎓📦

---

## 🚀 Quick Start

**Get the backend running in 3 commands:**

```bash
# 1. Setup PostgreSQL database
bash database/setup.sh

# 2. Start backend (from kennelmart directory)
cd kennelmart && mvn spring-boot:run

# 3. Backend is ready at http://localhost:8080
```

**See [QUICK_START.md](QUICK_START.md) for detailed instructions.**

---

## 📋 Features - Phase 1 (Complete ✅)

### Authentication & Authorization
- ✅ User registration with email verification (@students.nu-laguna.edu.ph)
- ✅ Secure login with JWT tokens (24-hour expiration)
- ✅ Password hashing with BCrypt (strength 12)
- ✅ Role-based access control (ADMIN, USER)
- ✅ Account status management (ACTIVE, SUSPENDED)

### User Types
- **ADMIN** - Full system access (you: Axel Drake Bagay)
- **USER** - Can buy and sell on marketplace (other users)

### Verified Identity System
- ✅ Email verification against NU Laguna master list
- ✅ Student/Faculty ID validation
- ✅ Status tracking (VERIFIED, PENDING, REJECTED)

---

## 🏗️ Architecture

### Backend Stack
- **Framework:** Spring Boot 4.0.6
- **Language:** Java 21
- **Authentication:** JWT + Spring Security
- **Database:** PostgreSQL 12+
- **ORM:** Spring Data JPA + Hibernate
- **Security:** BCrypt password hashing, HS512 JWT

### Frontend Stack
- **Framework:** React + TypeScript
- **Build Tool:** Vite
- **State Management:** Zustand
- **HTTP Client:** Axios
- **Styling:** TailwindCSS/Material UI

### Database
- **Engine:** PostgreSQL
- **Tables:** 
  - `users` - Registered users
  - `verified_identities` - NU Laguna master list
- **Indexes:** Optimized for email, student ID, and role lookups

---

## 📖 Documentation

| Document | Purpose |
|----------|---------|
| [QUICK_START.md](QUICK_START.md) | 5-minute setup guide |
| [DEVELOPER_SETUP.md](DEVELOPER_SETUP.md) | Detailed setup with PostgreSQL |
| [SETUP_COMPLETE.md](SETUP_COMPLETE.md) | What was completed and how |
| [PHASE_1_SUMMARY.md](PHASE_1_SUMMARY.md) | Authentication module details |
| [DATABASE_SETUP.md](DATABASE_SETUP.md) | Database schema and setup |
| [INTEGRATION_COMPLETE.md](INTEGRATION_COMPLETE.md) | Integration summary |

---

## 👥 User Roles

### ADMIN Role
- Full system access
- Manage users and accounts
- Access moderation dashboard
- Can buy and sell items
- **Your Account:** bagayam@students.nu-laguna.edu.ph

### USER Role
- Buy items on marketplace
- Sell items on marketplace
- Manage own listings
- Place and track orders
- **Other verified users**

---

## 🔐 Security Features

- ✅ **Email Validation:** @students.nu-laguna.edu.ph (NU Laguna domain)
- ✅ **Password Security:** BCrypt hashing with strength 12
- ✅ **Token Security:** JWT with HS512 algorithm (24-hour expiration)
- ✅ **Identity Verification:** Database of verified students/faculty
- ✅ **Account Management:** ACTIVE/SUSPENDED status control
- ✅ **CORS Protection:** Configured for localhost:5173 and localhost:3000
- ✅ **Stateless Auth:** JWT-based, no session state on server

---

## 📊 API Endpoints

### Authentication
```
POST   /api/auth/register        - Register new user
POST   /api/auth/login           - Login with credentials
GET    /api/auth/me              - Get current user (requires token)
```

### Marketplace (Phase 2+)
```
GET    /api/listings             - Browse all listings
POST   /api/listings             - Create new listing (USER, ADMIN)
PUT    /api/listings/{id}        - Update listing (USER, ADMIN)
DELETE /api/listings/{id}        - Delete listing (USER, ADMIN)

GET    /api/cart                 - Get cart (USER, ADMIN)
POST   /api/cart                 - Add to cart (USER, ADMIN)
DELETE /api/cart/{id}            - Remove from cart (USER, ADMIN)

GET    /api/orders               - Get orders (USER, ADMIN)
POST   /api/orders               - Place order (USER, ADMIN)
PUT    /api/orders/{id}          - Update order status (USER, ADMIN)
```

### Admin (Phase 5+)
```
GET    /api/admin/users          - List users (ADMIN)
PUT    /api/admin/users/{id}     - Update user status (ADMIN)
GET    /api/admin/reports        - View reports (ADMIN)
POST   /api/admin/reports        - Handle report (ADMIN)
```

---

## 🧪 Testing

### Register Your Account
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

**Expected response (201 Created):**
```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "email": "bagayam@students.nu-laguna.edu.ph",
  "role": "ADMIN",
  "verificationStatus": "VERIFIED",
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400
}
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "bagayam@students.nu-laguna.edu.ph",
    "password": "SecurePass123"
  }'
```

---

## 🛠️ Development Setup

### Prerequisites
- Java 21 or higher
- PostgreSQL 12 or higher
- Maven 3.8+
- Node.js 18+ (for frontend)

### Backend
```bash
cd kennelmart

# Install dependencies and build
mvn clean install

# Run tests
mvn test

# Start development server
mvn spring-boot:run
```

Backend runs on: `http://localhost:8080`

### Frontend
```bash
cd kennelmart-ui

# Install dependencies
npm install

# Start development server
npm run dev
```

Frontend runs on: `http://localhost:5173`

---

## 📂 Project Structure

```
KennelMart/
├── kennelmart/                 # Backend (Spring Boot)
│   ├── src/main/java/
│   │   └── com/kennel/mart/kennelmart/
│   │       ├── entity/         # Database entities
│   │       ├── repository/     # Data access layer
│   │       ├── service/        # Business logic
│   │       ├── controller/     # REST endpoints
│   │       ├── dto/            # Data transfer objects
│   │       ├── config/         # Spring configuration
│   │       ├── security/       # JWT and auth
│   │       └── enums/          # Enumerations
│   ├── src/test/java/          # Unit tests
│   ├── pom.xml                 # Maven configuration
│   └── src/main/resources/
│       └── application.properties
│
├── kennelmart-ui/              # Frontend (React + Vite)
│   ├── src/
│   │   ├── components/         # React components
│   │   ├── pages/              # Page components
│   │   ├── services/           # API clients
│   │   ├── store/              # State management
│   │   └── App.tsx
│   ├── package.json
│   └── vite.config.ts
│
├── database/                   # Database scripts
│   ├── kennelmart_schema.sql   # Main schema
│   ├── setup.sh                # Automated setup
│   └── verified_identities.sql # Identity table
│
└── Documentation/
    ├── README.md               # This file
    ├── QUICK_START.md          # 5-minute setup
    ├── DEVELOPER_SETUP.md      # Detailed setup
    ├── SETUP_COMPLETE.md       # What was done
    ├── PHASE_1_SUMMARY.md      # Auth module docs
    ├── DATABASE_SETUP.md       # DB schema docs
    └── CHANGELOG.md            # Version history
```

---

## 🚦 Development Phases

### ✅ Phase 1: Authentication Module (COMPLETE)
- User registration and login
- JWT token-based authentication
- Role-based access control
- Identity verification system
- Email domain validation

### ⏳ Phase 2: Product Listing Management
- Create, read, update, delete listings
- Product categories and search
- Seller dashboard
- Product image management

### ⏳ Phase 3: Marketplace Browsing
- Browse all listings
- Advanced search and filters
- Sort by price, latest, etc.
- View seller profiles

### ⏳ Phase 4: Cart & Order System
- Shopping cart management
- Order placement
- Payment method selection
- Order tracking

### ⏳ Phase 5: Admin Moderation
- User suspension/management
- Listing approval/rejection
- Report system
- Admin dashboard

---

## 👥 Sample Test Accounts

All accounts are VERIFIED and ready to use:

| Email | Name | Role |
|-------|------|------|
| bagayam@students.nu-laguna.edu.ph | Axel Drake Bagay | ADMIN |
| juan.delacruz@students.nu-laguna.edu.ph | Juan dela Cruz | USER |
| maria.santos@students.nu-laguna.edu.ph | Maria Santos | USER |
| pedro.reyes@students.nu-laguna.edu.ph | Pedro Reyes | USER |
| robert.johnson@students.nu-laguna.edu.ph | Dr. Robert Johnson | USER |

**Password for all:** Use your own during registration (min 8 characters)

---

## 🔗 Key Technology Decisions

1. **PostgreSQL** - Relational database for structured data
2. **JWT** - Stateless authentication for scalability
3. **Spring Boot** - Industry-standard Java framework
4. **React + TypeScript** - Type-safe frontend development
5. **BCrypt** - Adaptive password hashing for security
6. **Role-Based Access** - Simple ADMIN/USER model
7. **Verified Identity** - Email domain validation for authenticity

---

## 📞 Support & Troubleshooting

**PostgreSQL not running?**
```bash
sudo service postgresql start  # Linux
sudo systemctl start postgresql  # Or this
```

**Database connection error?**
- Check `application.properties` for correct credentials
- Verify database `kennelmart_db` exists: `createdb -U postgres kennelmart_db`

**Port already in use?**
- Change in `application.properties`: `server.port=8081`

**Detailed setup guide:** See [DEVELOPER_SETUP.md](DEVELOPER_SETUP.md)

---

## 📄 License

[Add your license here]

---

## 👨‍💻 Development Team

- **Lead Developer:** Axel Drake Bagay (bagayam@students.nu-laguna.edu.ph)
- **Institution:** National University Laguna
- **Project:** Campus Marketplace Platform

---

## 🎯 Getting Started

1. **Read:** [QUICK_START.md](QUICK_START.md) (5 minutes)
2. **Setup:** Run `bash database/setup.sh`
3. **Start:** Run `mvn spring-boot:run`
4. **Test:** Use curl commands from QUICK_START.md
5. **Build:** Start Phase 2 features

---

**Ready to build the next phase? Let's go! 🚀**
