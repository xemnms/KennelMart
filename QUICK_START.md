# KennelMart Quick Start Guide

## Prerequisites
- Java 21 or higher
- PostgreSQL 12 or higher
- Maven 3.8+
- Node.js 18+ (for frontend)

---

## 🚀 Start Backend in 5 Minutes

### 1. **Create PostgreSQL Database** (2 minutes)
```bash
# Create the database
createdb -U postgres kennelmart_db

# Verify it was created
psql -U postgres -c "\l" | grep kennelmart_db
```

### 2. **Setup Database Schema** (1 minute)
```bash
# Navigate to project root
cd /workspaces/KennelMart

# Run the setup script
bash database/setup.sh

# Or manually:
psql -U postgres -d kennelmart_db -f database/kennelmart_schema.sql
```

### 3. **Start Spring Boot Backend** (1 minute)
```bash
cd kennelmart

# Build and run
mvn spring-boot:run

# Wait for message: "Started KennelmartApplication in X.XXX seconds"
```

**Backend is now running on:** `http://localhost:8080`

---

## 📝 Test Your Setup

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
    "schoolId": "2025-1020735"
  }'
```

**Expected Response:**
```json
{
  "userId": "...",
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

### Login with Your Account
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "bagayam@students.nu-laguna.edu.ph",
    "password": "SecurePass123"
  }'
```

---

## 📱 Start Frontend

### 1. Install Dependencies
```bash
cd kennelmart-ui
npm install
```

### 2. Start Dev Server
```bash
npm run dev
```

**Frontend is now running on:** `http://localhost:5173`

---

## 🔑 Your Test Credentials

| Field | Value |
|-------|-------|
| **Email** | bagayam@students.nu-laguna.edu.ph |
| **Password** | SecurePass123 |
| **Student ID** | 2025-1020735 |
| **Role** | ADMIN |

---

## 📚 Additional Test Users

These verified users are in the database. Register with any of them:

| Email | Name |
|-------|------|
| juan.delacruz@students.nu-laguna.edu.ph | Juan dela Cruz |
| maria.santos@students.nu-laguna.edu.ph | Maria Santos |
| pedro.reyes@students.nu-laguna.edu.ph | Pedro Reyes |
| robert.johnson@students.nu-laguna.edu.ph | Dr. Robert Johnson |

All test users will get the USER role (can buy and sell).

---

## 🛑 Stop Services

### Stop Backend
```bash
# Press Ctrl+C in the terminal running "mvn spring-boot:run"
```

### Stop Frontend
```bash
# Press Ctrl+C in the terminal running "npm run dev"
```

### Stop PostgreSQL
```bash
# Linux:
sudo service postgresql stop

# Or:
sudo systemctl stop postgresql
```

---

## 🔧 Configuration Files

**Backend Configuration:**
- `kennelmart/src/main/resources/application.properties`
  - Database: `jdbc:postgresql://localhost:5432/kennelmart_db`
  - User: `postgres`
  - Password: `postgres` (change if different)

**Database Schema:**
- `database/kennelmart_schema.sql` - Main schema
- `database/setup.sh` - Automated setup script

**Frontend Configuration:**
- `kennelmart-ui/vite.config.ts` - Vite configuration
- `kennelmart-ui/.env` - Environment variables (create if needed)

---

## ✅ Verification Checklist

- [ ] PostgreSQL running
- [ ] Database `kennelmart_db` created
- [ ] Schema imported successfully
- [ ] Backend starts without errors
- [ ] Can register account at `POST /api/auth/register`
- [ ] Can login at `POST /api/auth/login`
- [ ] Received JWT token
- [ ] Frontend starts without errors
- [ ] Frontend connects to backend API

---

## 📊 Database Tables Created

```
verified_identities  - List of verified NU students/faculty
users               - Registered users in the system
```

## 🔐 User Roles

- **ADMIN** - Full system access (your account)
- **USER** - Can buy and sell (other users)

---

## 📖 Useful Commands

### Check PostgreSQL Status
```bash
# Check if running
psql -U postgres -c "SELECT 1"

# List databases
psql -U postgres -c "\l"

# Connect to database
psql -U postgres -d kennelmart_db

# Exit psql
\q
```

### Backend Logs
```bash
# View real-time logs from backend
tail -f kennelmart/target/logs/application.log

# Build project
cd kennelmart && mvn clean install

# Run tests
mvn test
```

### Frontend Logs
```bash
# Check build
cd kennelmart-ui && npm run build

# Run tests
npm test
```

---

## ⚠️ Common Issues

**Issue: "Database already exists"**
```bash
# Drop existing database and recreate
dropdb -U postgres kennelmart_db
createdb -U postgres kennelmart_db
psql -U postgres -d kennelmart_db -f database/kennelmart_schema.sql
```

**Issue: "Connection refused" for PostgreSQL**
```bash
# PostgreSQL not running, start it:
sudo service postgresql start
```

**Issue: "Password authentication failed"**
```bash
# Update in application.properties:
spring.datasource.password=your_actual_password
```

**Issue: Port 8080 already in use**
```bash
# Change port in application.properties:
server.port=8081
```

---

## 🎯 Next Steps

1. ✅ Start both backend and frontend
2. ✅ Register your account
3. ✅ Explore the authentication endpoints
4. ⏳ Phase 2: Implement Product Listing functionality
5. ⏳ Phase 3: Build marketplace browsing
6. ⏳ Phase 4: Add cart and order system
7. ⏳ Phase 5: Create admin moderation dashboard

---

## 📞 Support Files

- **[DEVELOPER_SETUP.md](DEVELOPER_SETUP.md)** - Detailed setup instructions
- **[PHASE_1_SUMMARY.md](PHASE_1_SUMMARY.md)** - Authentication module documentation
- **[DATABASE_SETUP.md](DATABASE_SETUP.md)** - Database setup guide
- **[INTEGRATION_COMPLETE.md](INTEGRATION_COMPLETE.md)** - Integration verification

---

**You're all set! Backend + Frontend running = Ready to develop Phase 2 features! 🚀**
