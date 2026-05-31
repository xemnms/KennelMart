# KennelMart Quick Start Guide

## Prerequisites
- Java 21+
- Maven 3.8+ (or use the included `mvnw` wrapper)
- Node.js 18+
- Docker Desktop (for the database)

---

## 🖥️ All Terminal Commands — Copy & Run

> **Paste these into your IDE terminal in order. That's all you need.**

```bash
# ── Terminal 1: Database + Backend ───────────────────────────────────────

# (first time only) Create the Docker container + seed the database
docker run -d --name postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=kennelmart_db -p 5432:5432 postgres:15
docker exec -i postgres psql -U postgres -d kennelmart_db < database/verified_identities.sql

# (every session) Start the existing container
docker start postgres

# Start the Spring Boot backend
cd kennelmart
mvn spring-boot:run

# ── Terminal 2: Frontend ──────────────────────────────────────────────────

cd kennelmart-ui
npm install     # first time only
npm run dev

# ── When done ───────────────────────────────────────────────────────────────

docker stop postgres
```

---

## 🚀 Running Locally — 3 Steps

### 1. Start the Database (Docker)

> **▶ Run in terminal (first time):**

```bash
# First time — creates the container
docker run -d \
  --name postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=kennelmart_db \
  -p 5432:5432 \
  postgres:15

# Seed verified identities (run once after creating the container)
docker exec -i postgres psql -U postgres -d kennelmart_db < database/verified_identities.sql
```

> On subsequent sessions just run `docker start postgres`.

> **▶ Run in terminal (every session):**

```bash
docker start postgres
```

### 2. Start the Backend

> **▶ Run in terminal (Terminal 1):**

```bash
cd kennelmart
mvn spring-boot:run
```

Wait for: `Started KennelmartApplication in X.XXX seconds`  
Backend runs at: `http://localhost:8080`

### 3. Start the Frontend

> **▶ Run in terminal (Terminal 2):**

```bash
cd kennelmart-ui
npm install    # first time only
npm run dev
```

Frontend runs at: `http://localhost:5173`

---

## 📝 Test Your Setup

### Register Your Account

> **▶ Run in terminal:**

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Axel Drake Bagay",
    "email": "bagayam@students.nu-laguna.edu.ph",
    "password": "SecurePass123",
    "confirmPassword": "SecurePass123",
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

### Login with Your Account

> **▶ Run in terminal:**

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "bagayam@students.nu-laguna.edu.ph",
    "password": "SecurePass123"
  }'
```

---

## 📱 Accessing the App

Open `http://localhost:5173` in your browser and sign in with your credentials.

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

These accounts are pre-seeded in the database. Register with any of them to create a `USER` account:

| Email | Name |
|-------|------|
| juan.delacruz@students.nu-laguna.edu.ph | Juan dela Cruz |
| maria.santos@students.nu-laguna.edu.ph | Maria Santos |
| pedro.reyes@students.nu-laguna.edu.ph | Pedro Reyes |
| robert.johnson@students.nu-laguna.edu.ph | Dr. Robert Johnson |

---

## 🛑 Stopping Services
> **▶ Run in terminal:**
```bash
# Stop backend — Ctrl+C in the mvn terminal

# Stop frontend — Ctrl+C in the npm terminal

# Stop database
docker stop postgres
```

---

## 🔧 Configuration Files

| File | Purpose |
|---|---|
| `kennelmart/src/main/resources/application.properties` | Backend — DB connection, JWT, CORS, file upload |
| `kennelmart-ui/vite.config.ts` | Frontend — dev server, proxy config |
| `database/verified_identities.sql` | Seed data for allowed NU accounts |

---

## ✅ Verification Checklist

- [ ] Docker Desktop running
- [ ] `postgres` container started
- [ ] Seed applied (`verified_identities.sql`)
- [ ] Backend starts without errors
- [ ] Frontend starts without errors
- [ ] Can register and receive a JWT token
- [ ] Browser opens marketplace at `http://localhost:5173`

---

## 🐛 Common Issues

| Problem | Fix |
|---|---|
| `Connection refused` on port 5432 | Run `docker start postgres` |
| `Failed to connect` in browser | Check backend is running on `:8080` |
| `npm: command not found` | Install Node.js 18+ from nodejs.org |
| Port 5432 already in use | Stop local PostgreSQL: `sudo service postgresql stop` |
