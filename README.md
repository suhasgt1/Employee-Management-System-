# Employee Management System

**Student:** Suhas G T · **USN:** 1BM25CD110

**Course/lab/batch:** SKILL LAB · **Submission date:** 2026-10-24

A university lab application for managing employee records with an Angular frontend, a Spring Boot REST API, and a MySQL database. It demonstrates CRUD operations across employees and departments, including employee-to-manager relationships.

> This is a local development project, not a production HR system. It has no authentication or authorization. Do not expose the development database or use its sample credentials for a public deployment.

## Features

- Create employees with a department, optional direct manager, job title, email, ten-digit phone number, and non-negative salary.
- Add departments and assign them to employees.
- Prevent manager cycles and retain reports without a manager when a manager is deleted.
- List records loaded from MySQL and read a single employee by ID.
- Edit an employee without changing its generated ID.
- Confirm before deleting an employee.
- Show inline validation, request status, and useful success/error messages.
- Keep records in MySQL across browser refreshes.
- Run MySQL, Spring Boot, and Angular in a GitHub Codespace.

## Technology

| Layer | Technology | Purpose |
|---|---|---|
| UI | Angular 21, TypeScript, HTML, CSS | Standalone single-page employee form and table |
| UI styling | Bootstrap 5 and custom CSS | Responsive layout, controls, alerts, and table |
| API | Java 21, Spring Boot 3.5, Spring Web | Validated REST endpoints |
| Persistence | Spring Data JPA, Hibernate, MySQL 8 | Map and store employee records |
| Build/tools | Maven, Node.js 22, npm, Git | Build, test, and version the project |
| Environment | GitHub Codespaces, Docker Compose | Provide development tools and run MySQL |

## Repository layout

```text
.
├── .devcontainer/                 # Codespaces Java, Maven, Node, Docker setup
├── backend/
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/bmsce/employeemanagement/
│       │   ├── controller/        # REST controller
│       │   ├── entity/             # Employee and Department JPA entities
│       │   ├── exception/          # API error handling
│       │   ├── repository/         # Spring Data JPA repository
│       │   └── service/            # CRUD business logic
│       ├── main/resources/         # Runtime configuration
│       └── test/                   # H2-backed API integration tests
├── database/setup.sql              # Fresh relational schema
├── database/migrate-employee-relations.sql # Safe migration from the prior schema
├── docs/                           # Project report and supporting documentation
├── frontend/
│   ├── package.json
│   ├── proxy.conf.json             # /api proxy to Spring Boot
│   └── src/app/                    # Angular model, service, and UI
├── screenshots/                    # Application screenshots
├── .env.example
└── compose.yaml                    # Local MySQL 8.4 service and persistent volume
```

## Run in GitHub Codespaces

The repository contains a dev-container definition and MySQL Compose service. Open the repository in a Codespace. If the Codespace predates `.devcontainer/devcontainer.json`, run **Codespaces: Rebuild Container** from the Command Palette once.

Use three terminal sessions from the repository root:

**Terminal 1 — MySQL**

```bash
docker compose up -d mysql
docker compose ps
```

Wait until `mysql` shows `healthy`. See logs with `docker compose logs -f mysql`.

**Terminal 2 — Spring Boot**

```bash
cd backend
mvn spring-boot:run
```

**Terminal 3 — Angular**

```bash
cd frontend
npm install
npm start
```

Open port **4200** in the Codespaces **Ports** panel. The expected URLs are:

- Frontend: `http://localhost:4200` (or the forwarded Codespaces URL).
- Backend API: `http://localhost:8080/api/employees`.
- Angular requests use `/api/employees`; `frontend/proxy.conf.json` forwards them to `http://localhost:8080`.

Port 3306 is bound to the Codespace loopback address and is not configured for public forwarding. See [START_HERE.md](START_HERE.md) for beginner steps.

## Database and configuration

Compose creates the `employee_db` database with a named persistent volume. The schema has `departments` and `employees` tables with department and self-referencing manager foreign keys. For a fresh database, run [database/setup.sql](database/setup.sql). Back up an existing database from the prior schema, then run [database/migrate-employee-relations.sql](database/migrate-employee-relations.sql) before starting the updated backend. The migration also completes partially upgraded schemas and can be safely rerun.

Default local development values are host `localhost`, port `3306`, database `employee_db`, username `root`, and password `root`. To customize them, copy `.env.example` to `.env` and edit the local-only values. `.env` is ignored by Git. Compose reads its `MYSQL_ROOT_PASSWORD` and `MYSQL_PORT` values; Spring Boot reads `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` from the backend process environment. For custom Spring values, load `.env` in the backend terminal before starting Maven:

```bash
set -a
source .env
set +a
cd backend
mvn spring-boot:run
```

The SQL query for checking stored rows is:

```sql
SELECT e.id, e.name, d.name AS department, m.name AS manager, e.job_title, e.email, e.phone, e.salary
FROM employee_db.employees e
JOIN employee_db.departments d ON d.id = e.department_id
LEFT JOIN employee_db.employees m ON m.id = e.manager_id;
```

Or run it in the container with `docker compose exec mysql mysql -uroot -p -e "SELECT * FROM employee_db.employees;"` and enter the local password when prompted. If you change `MYSQL_PORT`, also update the host port in `DB_URL`.

## API

Base path: `http://localhost:8080/api/employees`

| Method | Endpoint | Successful response |
|---|---|---|
| GET | `/api/employees` | `200 OK`, employee array |
| GET | `/api/employees/{id}` | `200 OK`, employee object |
| POST | `/api/employees` | `201 Created`, saved employee |
| PUT | `/api/employees/{id}` | `200 OK`, updated employee |
| DELETE | `/api/employees/{id}` | `204 No Content` |
| GET | `/api/departments` | `200 OK`, department array |
| POST | `/api/departments` | `201 Created`, saved department |

The schema contains `departments` and `employees`. Each employee references one department and may reference another employee as their manager; manager cycles are rejected by the API. Employees require a valid email, exactly ten phone digits, and salary between 0 and 999999999999.99 with at most two decimal places. See the [SQL setup](database/setup.sql) and [migration script](database/migrate-employee-relations.sql).

## Build and test

Backend tests use an isolated in-memory H2 database; they do not require a developer's MySQL data:

```bash
cd backend
mvn test
mvn package
```

Frontend install and production build:

```bash
cd frontend
npm install
npm run build
```

Angular's build runs TypeScript compilation. There is no separate frontend test runner configured yet. Backend integration tests use an isolated H2 database.

## Screenshots

The `screenshots/` directory is reserved for images of the running application. Screenshots are not included in this repository.

## Known limitations

- No authentication, roles, audit log, search, pagination, or payroll.
- `ddl-auto=update` and the sample credentials are for local learning only.
- No frontend unit-test runner or browser automation is configured.
- `@angular/build` currently pins a transitive `undici` release with advisories; `frontend/package.json` overrides it to the patched compatible 7.30.1+ line. Re-run `npm audit` after dependency changes.
