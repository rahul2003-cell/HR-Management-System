# 🏢 NexusHR — Employee Management System

> Full-stack HR management system built with Spring Boot + React. Featuring JWT auth, role-based access, attendance tracking, leave management, payroll with PDF/Excel export.

![Java](https://img.shields.io/badge/Java-17-orange) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green) ![React](https://img.shields.io/badge/React-18-blue)

---

## ✨ Features

| Feature | Details |
|---------|---------|
| 🔐 Authentication | JWT-based login, role-based access (Admin, HR, Employee) |
| 👥 Employee Management | Full CRUD, search, filter, profile view |
| 🏢 Departments | Create, assign employees, track headcount |
| 📅 Attendance | Daily marking, check-in/out, monthly summary |
| 📋 Leave Management | Apply, approve/reject workflow with remarks |
| 💰 Payroll | Auto-generate with PF+tax calc, mark as paid |
| 📄 PDF Export | Salary slip PDF per employee (iText) |
| 📊 Excel Export | Payroll + Employee reports (Apache POI) |
| 📈 Dashboard | Real-time stats with Recharts visualization |

---

## 🚀 Quick Start

```bash
# Terminal 1 — Backend (H2 in-memory, zero setup)
cd backend
mvn spring-boot:run

# Terminal 2 — Frontend
cd frontend
npm install && npm run dev
```

Open **http://localhost:3000**

### Demo Credentials
| Role | Email | Password |
|------|-------|----------|
| Admin | admin@nexushr.com | admin123 |
| HR Manager | priya.sharma@nexushr.com | admin123 |
| Employee | ananya.iyer@nexushr.com | Hrms@123 |

---

## 🐳 Docker

```bash
docker compose up --build
# App: http://localhost | API: http://localhost:8080
```

---

## 🗂️ Project Structure

```
hrms/
├── backend/                         # Spring Boot API
│   └── src/main/java/com/hrms/
│       ├── config/                  # Security + DataSeeder
│       ├── controller/              # REST endpoints
│       ├── dto/                     # Request/Response DTOs
│       ├── entity/                  # JPA Entities
│       ├── exception/               # Global error handling
│       ├── repository/              # Spring Data JPA
│       ├── security/                # JWT utils + filter
│       └── service/                 # Business logic + Export
├── frontend/                        # React + Vite
│   └── src/
│       ├── api/                     # Axios client
│       ├── components/layout/       # Sidebar, Topbar, Layout
│       ├── pages/admin/             # Dashboard, Employees, Attendance, Leaves, Payroll
│       ├── pages/employee/          # Profile, Attendance, Leaves, Payslips
│       └── store/                   # Zustand state management
├── docker-compose.yml
└── README.md
```

---

## 📡 API Endpoints

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/auth/login` | ❌ | Login |
| GET | `/api/auth/me` | ✅ | Current user |
| GET | `/api/employees` | ✅ | List employees |
| POST | `/api/employees` | ADMIN/HR | Create employee |
| GET | `/api/attendance/date/{date}` | ✅ | Daily attendance |
| POST | `/api/attendance/mark` | ADMIN/HR | Mark attendance |
| GET | `/api/leaves` | ADMIN/HR | All leaves |
| POST | `/api/leaves/apply/{empId}` | ✅ | Apply leave |
| PATCH | `/api/leaves/{id}/action` | ADMIN/HR | Approve/reject |
| POST | `/api/payroll/generate` | ADMIN/HR | Generate payroll |
| GET | `/api/payroll/{id}/slip/pdf` | ✅ | Download PDF slip |
| GET | `/api/payroll/monthly/excel` | ADMIN/HR | Download Excel |
| GET | `/api/admin/dashboard` | ADMIN/HR | Dashboard stats |

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 17, Spring Boot 3.2, Spring Security |
| Auth | JWT (JJWT 0.11) |
| Database | MySQL 8 (prod) / H2 (dev) |
| ORM | Spring Data JPA + Hibernate |
| PDF | iText 5.5 |
| Excel | Apache POI 5.2 |
| Frontend | React 18, Vite, React Router v6 |
| State | Zustand |
| Charts | Recharts |
| HTTP | Axios |
| Styling | CSS Modules (Glassmorphism dark theme) |
