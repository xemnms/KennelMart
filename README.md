# KennelMart

A peer-to-peer campus marketplace for **National University Laguna** students and faculty — buy, sell, and connect within the campus community.

---

## Stack

| Layer | Technology |
|---|---|
| Backend | Spring Boot 4, Java 21, Spring Security, JWT (HS512) |
| Database | PostgreSQL 12+, Spring Data JPA / Hibernate |
| Frontend | React 18 + TypeScript, Vite, Zustand, Axios |
| Auth | BCrypt (strength 12), JWT (24h expiry), role-based access |

---

## Running Locally

### 1. Start PostgreSQL

```bash
docker start postgres
# or
sudo service postgresql start
```

### 2. Backend

```bash
cd kennelmart
mvn spring-boot:run
# Runs at http://localhost:8080
```

### 3. Frontend

```bash
cd kennelmart-ui
npm install
npm run dev
# Runs at http://localhost:5173
```

> See [DEVELOPER_SETUP.md](DEVELOPER_SETUP.md) for full PostgreSQL configuration and [DATABASE_SETUP.md](DATABASE_SETUP.md) for schema details.

---

## Features

### Auth & Identity
- Registration restricted to `@students.nu-laguna.edu.ph` domain
- Student/Faculty ID validated against the NU Laguna master list
- Verification status: `VERIFIED`, `PENDING`, `REJECTED`
- Account status: `ACTIVE`, `SUSPENDED`
- JWT-based stateless auth, no server-side sessions

### Marketplace
- Browse product listings with search, filters, and category sorting
- Feed and user profile browsing
- Listing detail with seller info and message shortcut

### Seller Tools
- Create, edit, and manage listings with image uploads
- Per-listing status: `ACTIVE`, `INACTIVE`, `SOLD_OUT`, `PENDING_APPROVAL`
- Sales order dashboard (accept, prepare, mark shipped/delivered)

### Buying
- Pawket (cart) with quantity management
- Checkout flow with order placement
- Order history and per-order status tracking
- Post-delivery review system

### Messaging
- Direct messages between buyers and sellers
- Inbox with unread counts
- Report users from within a conversation

### Notifications
- Real-time-style notification feed (order updates, reviews, messages)

### Admin
- User management (verify, suspend, delete)
- Listing moderation (approve, reject)
- Report queue

---

## API Reference

```
POST  /api/auth/register
POST  /api/auth/login
GET   /api/auth/me

GET   /api/listings
POST  /api/listings
PUT   /api/listings/{id}
DELETE /api/listings/{id}

GET   /api/cart
POST  /api/cart
DELETE /api/cart/{id}

GET   /api/orders
POST  /api/orders
PUT   /api/orders/{id}

GET   /api/messages/conversations
GET   /api/messages/{userId}
POST  /api/messages/{userId}

GET   /api/notifications
PUT   /api/notifications/read-all

GET   /api/admin/users
PUT   /api/admin/users/{id}
GET   /api/admin/reports
POST  /api/admin/reports/{id}/resolve
```

---

## Project Structure

```
KennelMart/
├── kennelmart/               # Spring Boot backend
│   └── src/main/java/com/kennel/
│       ├── controller/       # REST endpoints
│       ├── service/          # Business logic
│       ├── entity/           # JPA entities
│       ├── dto/              # Request/response DTOs
│       ├── repository/       # Data access
│       ├── security/         # JWT filter, config
│       ├── config/           # CORS, app config
│       └── enums/
│
├── kennelmart-ui/            # React + TypeScript frontend
│   └── src/
│       ├── components/       # AppShell, modals, guards
│       ├── pages/            # auth, marketplace, orders,
│       │                     # seller, cart, checkout,
│       │                     # messages, notifications,
│       │                     # profile, admin, reviews
│       ├── services/         # Axios API clients
│       ├── store/            # Zustand state (auth, cart,
│       │                     # messages, notifications)
│       ├── types/            # TypeScript interfaces
│       ├── router/           # React Router v6 routes
│       └── utils/
│
├── database/                 # SQL schema + seed scripts
└── context/                  # Project rules and flow specs
```

---

## Roles

| Role | Capabilities |
|---|---|
| `USER` | Buy, sell, message, review, manage own listings/orders |
| `ADMIN` | All USER capabilities + user/listing/report moderation |

---

## Security

- Passwords hashed with BCrypt strength 12
- JWT signed with HS512, expires in 24 hours
- CORS restricted to `localhost:5173` / `localhost:3000`
- Email domain enforced at registration (`@students.nu-laguna.edu.ph`)
- Identity cross-checked against the `verified_identities` table

---

## Developer

**Axel Drake Bagay** — bagayam@students.nu-laguna.edu.ph  
National University Laguna
