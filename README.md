# MythicVault 🎮  
*A Spring Boot MMORPG Catalog & Questing API*  

## 📌 Overview  
**MythicVault** is a backend system simulating a game catalog and questing service for an MMORPG.  
It demonstrates:  

- 🔒 **JWT-based authentication & authorization** (Spring Security)  
- 📊 **Leaderboards** with snapshots and entries  
- 🎒 **Item management** with rarity & types  
- 🧙 **Character management** (class, race, level, inventory)  
- 📜 **Quest system** with steps, requirements, and rewards  
- ✅ **Automated testing** (unit + specs + controller tests)  
- 📖 **OpenAPI/Swagger UI** auto-documented APIs  

This project was developed as part of a **technical assessment** and refined to be production-grade with clean architecture and full testing.  

---

## ⚙️ Tech Stack  
- **Java 21**  
- **Spring Boot 3.x** (Web, Data JPA, Security)  
- **PostgreSQL** (via Docker)  
- **Flyway** for database migrations  
- **JWT** for authentication  
- **springdoc-openapi** for API docs  
- **JUnit 5 + Mockito + AssertJ** for testing  
- **JaCoCo** for test coverage reporting  

---

## Getting Started  

### 1. Clone & Build  
```bash

git clone https://github.com/yourusername/mythicvault.git
cd mythicvault
mvn clean package -DskipTests

`````

### 2. Run with Docker

The project comes with a `docker-compose.yml` to spin up **PostgreSQL** and the app.
```bash
docker compose up --build
`````

### 3. Access API:
👉 http://localhost:8080

### 4 Swagger UI:
👉 http://localhost:8080/swagger-ui.html

### 5 🗄️ Database & Migrations

Schema and seed data are managed via Flyway.
Migrations are under: src/main/resources/db/migration/.

### 6 Authentication & Demo Users
The system comes pre-seeded with demo users via Flyway migrations:
  ### 1 Admin User
    Username: admin_user
    password: test
  ### 2 User
    Username: demo_user
    password: test

### 7 Testing
The project has test coverage using JUnit 5 + Mockito + JaCoCo.
## Run Tests

```bash
mvn clean verify
`````
## Generates JaCoCo report:
👉 target/site/jacoco/index.html

### 8 Project Highlights for Interview

Clean layered architecture: Repositories → Services → Controllers → DTOs

Security: JWT with roles + OpenAPI docs integration

Database: Reproducible schema with Flyway migrations + seed data

Testing: coverage + Report (services, specs, controllers)

DevOps: Ready-to-run with Docker Compose

Code Quality: Consistent error handling (ResourceNotFoundException, validation, controller advice in tests)
