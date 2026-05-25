# 🚀 Get Started Now! - 3 Steps to Running System

**Total Time:** ~5 minutes

---

## Step 1️⃣: Setup Database (1 minute)

```bash
cd /workspaces/KennelMart
bash database/setup.sh
```

**Expected Output:**
```
✓ Checking PostgreSQL connection...
✓ PostgreSQL is running
✓ Creating database: kennelmart_db...
✓ Creating tables and schema...
✓ Database setup complete!
```

---

## Step 2️⃣: Start Backend (1 minute)

```bash
cd kennelmart
mvn spring-boot:run
```

**Wait for this line to appear:**
```
... Started KennelmartApplication in X.XXX seconds
```

Backend is now running at: **http://localhost:8080**

---

## Step 3️⃣: Test It! (1 minute)

### Register Your Admin Account

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

### 🎉 You Should See:
```json
{
  "userId": "550e8400...",
  "firstName": "Axel",
  "lastName": "Bagay",
  "email": "bagayam@students.nu-laguna.edu.ph",
  "role": "ADMIN",                  ← YOU ARE ADMIN!
  "verificationStatus": "VERIFIED",
  "accountStatus": "ACTIVE",
  "accessToken": "eyJhbGc...",
  "tokenType": "Bearer",
  "expiresIn": 86400
}
```

---

## ✅ Success!

If you see `"role": "ADMIN"` in the response, everything is working! 🎉

---

## 🧪 Test Other Users

Try registering with one of these emails (they're all VERIFIED):

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Juan",
    "lastName": "dela Cruz",
    "email": "juan.delacruz@students.nu-laguna.edu.ph",
    "password": "SecurePass123",
    "confirmPassword": "SecurePass123",
    "studentOrFacultyId": "2024-00001"
  }'
```

**This user will get** `"role": "USER"`

---

## 📚 Learn More

- **QUICK_START.md** - Extended setup guide
- **DEVELOPER_SETUP.md** - Detailed PostgreSQL setup
- **README.md** - Complete project overview
- **PHASE_1_SUMMARY.md** - Authentication details

---

## ⚙️ Troubleshooting

### "Database already exists"
```bash
dropdb -U postgres kennelmart_db
createdb -U postgres kennelmart_db
psql -U postgres -d kennelmart_db -f database/kennelmart_schema.sql
```

### "PostgreSQL not running"
```bash
sudo service postgresql start
```

### "Port 8080 already in use"
Edit `kennelmart/src/main/resources/application.properties`:
```
server.port=8081
```

### "Connection refused"
Check credentials in `application.properties`:
```
spring.datasource.username=postgres
spring.datasource.password=postgres
```

---

## 🎯 What's Next?

After successful setup:

1. **Test more endpoints:**
   - Login: `POST /api/auth/login`
   - Get current user: `GET /api/auth/me`

2. **Start frontend (optional):**
   ```bash
   cd kennelmart-ui
   npm install
   npm run dev
   ```

3. **Begin Phase 2 (Product Listings)**
   - See [PHASE_1_SUMMARY.md](PHASE_1_SUMMARY.md) for architecture overview

---

## 🔑 Your Credentials

```
Email:    bagayam@students.nu-laguna.edu.ph
Password: SecurePass123
Role:     ADMIN
```

---

**That's it! You're ready. Questions? Check the README or QUICK_START.md** ✨
