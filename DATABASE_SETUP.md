# KennelMart — Database Setup (Docker)

**Database:** PostgreSQL 15 running in Docker  
**No local PostgreSQL installation required.**  
Docker maps port `5432` to `localhost:5432`, so `application.properties` needs no changes.

---

## �️ All Terminal Commands — Quick Reference

> **Copy and run these in your IDE terminal in order.**

```bash
# ── FIRST TIME ONLY ──────────────────────────────────────────────────────────

# 1. Create the Docker container
docker run -d \
  --name postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=kennelmart_db \
  -p 5432:5432 \
  postgres:15

# 2. Seed verified identities
docker exec -i postgres psql -U postgres -d kennelmart_db < database/verified_identities.sql

# ── EVERY SESSION ────────────────────────────────────────────────────────────

# 3. Start the container (if stopped)
docker start postgres

# 4. Start the backend
cd kennelmart && mvn spring-boot:run

# 5. Start the frontend (separate terminal)
cd kennelmart-ui && npm install && npm run dev

# ── WHEN DONE ────────────────────────────────────────────────────────────────

# 6. Stop everything
docker stop postgres
```

---

## �📋 Prerequisites

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) installed and running
- That's it. No local PostgreSQL needed.

---

## 🚀 Step 1 — Start the PostgreSQL Container

Run this **once** to create the container:

> **▶ Run in terminal:**

```bash
docker run -d \
  --name postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=kennelmart_db \
  -p 5432:5432 \
  postgres:15
```

| Parameter | Value |
|---|---|
| Container name | `postgres` |
| Database | `kennelmart_db` |
| Username | `postgres` |
| Password | `postgres` |
| Port | `5432 → 5432` |

> After the first run, use `docker start postgres` to start it again on subsequent sessions.

Verify the container is running:

> **▶ Run in terminal:**

```bash
docker ps
```

You should see `postgres` in the list with status `Up`.

---

## 🌱 Step 2 — Seed Verified Identities

The `verified_identities` table must exist **before** the backend starts so Spring Boot can validate registrations against it.

> **▶ Run in terminal:**

```bash
docker exec -i postgres psql -U postgres -d kennelmart_db < database/verified_identities.sql
```

This creates the `verified_identities` table and inserts the default test accounts (including the admin account).

Verify the seed worked:

> **▶ Run in terminal:**

```bash
docker exec -it postgres psql -U postgres -d kennelmart_db -c "SELECT school_id, full_name, email FROM verified_identities;"
```

Expected output:

```
  school_id   |      full_name       |               email
--------------+----------------------+------------------------------------
 2025-1020735 | Axel Drake Bagay     | bagayam@students.nu-laguna.edu.ph
 2024-00001   | Juan dela Cruz       | juan.delacruz@students.nu-laguna.edu.ph
 2024-00002   | Maria Santos         | maria.santos@students.nu-laguna.edu.ph
 2024-00003   | Pedro Reyes          | pedro.reyes@students.nu-laguna.edu.ph
 FAC-00001    | Dr. Robert Johnson   | robert.johnson@students.nu-laguna.edu.ph
 FAC-00002    | Prof. Sarah Williams | sarah.williams@students.nu-laguna.edu.ph
```

> All other tables (`users`, `orders`, `carts`, etc.) are created automatically by Hibernate (`spring.jpa.hibernate.ddl-auto=update`) when the Spring Boot app starts.

---

## ⚙️ Step 3 — Backend Configuration

No changes needed. `application.properties` already points to the Docker container:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/kennelmart_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

Docker's port mapping (`-p 5432:5432`) makes the container reachable at `localhost:5432` exactly as configured.

---

## 🔁 Daily Workflow

> **▶ Run in terminal:**

```bash
# Start the database before running the backend
docker start postgres

# Stop the database when done (optional — data is persisted in the container)
docker stop postgres
```

---

## 🛠️ Useful Docker Commands

> **▶ Run in terminal:**

```bash
# Check container status
docker ps -a

# View PostgreSQL logs
docker logs postgres

# Open a psql shell inside the container
docker exec -it postgres psql -U postgres -d kennelmart_db

# Re-seed verified identities (safe — uses ON CONFLICT DO NOTHING)
docker exec -i postgres psql -U postgres -d kennelmart_db < database/verified_identities.sql

# Wipe and recreate the database (nuclear reset)
docker exec -it postgres psql -U postgres -c "DROP DATABASE kennelmart_db;"
docker exec -it postgres psql -U postgres -c "CREATE DATABASE kennelmart_db;"
docker exec -i postgres psql -U postgres -d kennelmart_db < database/verified_identities.sql
```

---

## ➕ Adding Verified Students

Connect to psql and insert directly:

> **▶ Run in terminal:**

```bash
docker exec -it postgres psql -U postgres -d kennelmart_db
```

```sql
INSERT INTO verified_identities (school_id, full_name, email)
VALUES ('2024-12345', 'New Student Name', 'newstudent@students.nu-laguna.edu.ph')
ON CONFLICT (email) DO NOTHING;
```

Or add entries to `database/verified_identities.sql` and re-run the seed command — duplicates are safely ignored via `ON CONFLICT`.

---

## 🐛 Troubleshooting

| Problem | Fix |
|---|---|
| `docker: command not found` | Install Docker Desktop and make sure it's running |
| Port 5432 already in use | A local PostgreSQL may be running. Run `sudo service postgresql stop` or change the port to `-p 5433:5432` and update `application.properties` accordingly |
| `kennelmart_db does not exist` | The `-e POSTGRES_DB=kennelmart_db` flag creates it on first run. If you deleted the container, recreate it with `docker run ...` |
| Container exists but won't start | Run `docker rm postgres` then repeat Step 1 |
| Spring Boot `Connection refused` | The container is not running. Run `docker start postgres` |
| Seed fails with "already exists" | Safe to ignore — `ON CONFLICT DO NOTHING` handles duplicates |

---

## ✅ Setup Checklist

- [ ] Docker Desktop installed and running
- [ ] Container created: `docker run -d --name postgres ...`
- [ ] Seed applied: `docker exec -i postgres psql ... < database/verified_identities.sql`
- [ ] `verified_identities` rows confirmed in psql
- [ ] Backend starts without DB errors (`mvn spring-boot:run`)
- [ ] Hibernate creates remaining tables on first boot
