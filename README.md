# Finance Data Processing and Access Control Backend

A production-structured Spring Boot backend for a finance dashboard system,
built as part of the Zorvyn Backend Developer Intern assessment.

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 21 |
| Framework | Spring Boot 3.5.1 |
| Security | Spring Security + JWT (jjwt 0.11.5) |
| Database | MySQL 8.0 |
| ORM | Spring Data JPA + Hibernate |
| Validation | Jakarta Bean Validation |
| Build Tool | Maven |

---

## Project Structure
```
src/main/java/com/zorvyn/finance/
├── config/         → DataSeeder (auto seeds on startup)
├── controller/     → REST API endpoints
├── service/        → Business logic
├── repository/     → Database queries
├── model/          → JPA entities
├── dto/            → Request and response objects
├── security/       → JWT filter, utility, Spring Security config
└── exception/      → Global exception handler
```

## Getting Started

### Prerequisites
- Java 21
- MySQL 8.0
- Maven

### Database Setup

Run in MySQL Workbench:
```sql
CREATE DATABASE finance_db;
CREATE USER 'financeuser'@'localhost' IDENTIFIED BY 'finance123';
GRANT ALL PRIVILEGES ON finance_db.* TO 'financeuser'@'localhost';
FLUSH PRIVILEGES;
```

### Run the Application
```bash
git clone https://github.com/yourusername/zorvyn-finance-backend
cd finance
mvn spring-boot:run
```

Server starts at: `http://localhost:8080`

### Auto-Seeded Test Accounts

On first startup, the DataSeeder automatically creates:

| Name | Email | Password | Role |
|------|-------|----------|------|
| Khushi Admin | admin@zorvyn.com | admin123 | ADMIN |
| Priya Analyst | analyst@zorvyn.com | analyst123 | ANALYST |
| Rahul Viewer | viewer@zorvyn.com | viewer123 | VIEWER |

15 financial records across 3 months are also created automatically.

---

## API Reference

### Authentication Header
All protected endpoints require:
Authorization: Bearer <token>
---

### Auth Endpoints (Public)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login and get JWT token |

---

### User Endpoints (Admin only)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users` | Get all users |
| GET | `/api/users/{id}` | Get user by ID |
| PUT | `/api/users/{id}/role` | Update user role |
| PUT | `/api/users/{id}/status?active=true` | Activate or deactivate user |
| DELETE | `/api/users/{id}` | Delete user |

---

### Financial Record Endpoints

| Method | Endpoint | Role Required |
|--------|----------|---------------|
| POST | `/api/records` | ADMIN |
| GET | `/api/records?page=0&size=10` | ALL |
| GET | `/api/records?type=INCOME&page=0&size=10` | ALL |
| GET | `/api/records?category=Salary&page=0&size=10` | ALL |
| GET | `/api/records?from=2026-01-01&to=2026-04-01` | ALL |
| GET | `/api/records/{id}` | ALL |
| PUT | `/api/records/{id}` | ADMIN |
| DELETE | `/api/records/{id}` | ADMIN |

---

### Dashboard Endpoints

| Method | Endpoint | Role Required | Returns |
|--------|----------|---------------|---------|
| GET | `/api/dashboard/summary` | ALL | Income, expense, net balance, savings rate |
| GET | `/api/dashboard/recent` | ALL | Last 10 transactions |
| GET | `/api/dashboard/category-wise` | ANALYST, ADMIN | Totals per category |
| GET | `/api/dashboard/monthly-trend` | ANALYST, ADMIN | Month by month breakdown |

---

## Role Permission Matrix

| Action | VIEWER | ANALYST | ADMIN |
|--------|--------|---------|-------|
| View records | ✅ | ✅ | ✅ |
| View dashboard summary | ✅ | ✅ | ✅ |
| View recent activity | ✅ | ✅ | ✅ |
| View category wise data | ❌ | ✅ | ✅ |
| View monthly trends | ❌ | ✅ | ✅ |
| Create records | ❌ | ❌ | ✅ |
| Update records | ❌ | ❌ | ✅ |
| Delete records | ❌ | ❌ | ✅ |
| Manage users | ❌ | ❌ | ✅ |

---

## Design Decisions and Assumptions

### Soft Delete
Financial records are never permanently deleted. A `deleted` flag marks
them as removed. This preserves the audit trail which is essential
in any finance system. The flag is hidden from API responses using
`@JsonIgnore`.

### JWT Stateless Authentication
No sessions are stored server side. Each request carries a self-contained
JWT token with the user's email and role. This is more scalable and
appropriate for a REST API.

### Inactive User Check at Login
Users marked as inactive are blocked at the login stage, not just at
the role level. This prevents a deactivated employee's existing token
from continuing to work for the full expiry duration in a real system.

### Self-Deletion Protection
An admin cannot delete their own account. This prevents accidental
lockout of the entire system.

### Consistent API Response Format
Every endpoint returns the same wrapper:
```
json
{
  "success": true,
  "message": "Human readable message",
  "data": { }
}
```
This makes frontend integration predictable.

### COALESCE in Aggregation Queries
Dashboard total queries use `COALESCE(SUM(...), 0)` to return 0
instead of null when no records exist. This prevents null pointer
errors in the service layer.

### Savings Rate
The dashboard summary includes a calculated savings rate
`(netBalance / totalIncome * 100)` to provide meaningful financial
insight beyond raw totals.

---

## Assumptions Made

| Area | Assumption |
|------|-----------|
| Roles | Three roles are sufficient for a finance dashboard |
| Record types | Only INCOME and EXPENSE are valid types |
| Delete | Soft delete preferred over hard delete for financial data |
| Auth | JWT expiry set to 24 hours for development |
| Pagination | Default page size is 10 records |

---


## Running Tests
```bash
mvn test
```
Tests cover: registration success, duplicate email, inactive user
login, wrong password, successful login.

---

## Quick Test Guide (Postman)

**Step 1 — Login as Admin**
```
json
POST http://localhost:8080/api/auth/login
{
  "email": "admin@zorvyn.com",
  "password": "admin123"
}
```

**Step 2 — Dashboard Summary**

GET http://localhost:8080/api/dashboard/summary
Authorization: Bearer <token>

**Step 3 — Paginated Records**
GET http://localhost:8080/api/records?page=0&size=5
Authorization: Bearer <token>
Expected: 5 records, totalElements: 15, totalPages: 3

**Step 4 — Viewer Cannot Access Category Data**
GET http://localhost:8080/api/dashboard/category-wise
Authorization: Bearer <viewer_token>
Expected: 403 Access denied


Step 6 — Monthly Trend as Analyst
POST http://localhost:8080/api/auth/login
{ "email": "analyst@zorvyn.com", "password": "analyst123" }

GET http://localhost:8080/api/dashboard/monthly-trend
Authorization: Bearer <analyst_token>
Expected: breakdown for Feb, March, April 2026

````
finance/
└── screenshots/
├── 01-login.png
├── 02-dashboard-summary.png
├── 03-pagination.png
├── 04-access-denied.png
└── 05-validation-error.png
,,,

---

## API Screenshots

### Login — JWT Token Generated
![Login](screenshots/01-login.png)

### Dashboard Summary — Real Calculated Data
![Dashboard Summary](screenshots/02-dashboard-summary.png)

### Paginated Records — Page 0, Size 5
![Pagination](screenshots/03-pagination.png)

### Access Control — Viewer Blocked with 403
![Access Denied](screenshots/04-access-denied.png)

### Validation — Invalid Input Rejected
![Validation Error](screenshots/05-validation-error.png)