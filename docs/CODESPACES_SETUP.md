# GitHub Codespaces Setup

The repository itself contains the project source and `.devcontainer/devcontainer.json`; no ZIP upload or extraction is required. The dev container provides Java 21, Maven, Node.js 22, Docker support, and forwarded application ports. MySQL 3306 is intentionally bound to the Codespace loopback address and is not configured for public forwarding.

## Open or rebuild the Codespace

1. Create or open a Codespace for this GitHub repository.
2. If it was created before the dev-container configuration was present or changed, open the Command Palette and run **Codespaces: Rebuild Container**.
3. Wait for the rebuild to finish and open a terminal at the repository root.

The dev-container's `postCreateCommand` runs `npm install` in `frontend/`. A rebuild is not needed for normal code changes; rebuild only when changing the dev-container features or setup.

## Optional local environment overrides

The app has development defaults matching the lab assignment. To customize them:

```bash
cp .env.example .env
```

Edit `.env` only in the Codespace. It is excluded from Git. Docker Compose reads `MYSQL_ROOT_PASSWORD` and `MYSQL_PORT`. Spring Boot reads `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` from the backend process environment. If using custom values, load the file before launching the backend:

```bash
set -a
source .env
set +a
cd backend
mvn spring-boot:run
```

If `.env` is not created, application properties and Compose use the lab defaults (`root` / `root`). Never put a real password or token in a tracked file.

## Start the three services

Keep three terminals open.

### Terminal 1 — MySQL from the repository root

```bash
docker compose up -d mysql
docker compose ps
```

Wait for `healthy`. MySQL logs:

```bash
docker compose logs --tail=80 mysql
```

The service creates database `employee_db` and persists it in volume `employee_mysql_data`. The host port is loopback-only; the Angular/browser does not connect to MySQL.

### Terminal 2 — Spring Boot

From the repository root:

```bash
cd backend
mvn spring-boot:run
```

The backend listens on port 8080 and uses JDBC `jdbc:mysql://localhost:3306/employee_db`. Hibernate creates/updates the `employees` table. Verify:

```bash
curl -i http://localhost:8080/api/employees
```

### Terminal 3 — Angular

From the repository root:

```bash
cd frontend
npm install
npm start
```

Open port 4200 through the Codespaces **Ports** tab. The UI uses `/api/employees`; `proxy.conf.json` forwards that path from the Angular server to `http://localhost:8080`. This avoids sending browser requests to the viewer's local machine.

## URLs and database inspection

- Frontend: port `4200` (open its forwarded URL).
- Backend API: `http://localhost:8080/api/employees` from within the Codespace, or forwarded port 8080.
- MySQL: port `3306` on Codespace loopback only.

Inspect records with:

```bash
docker compose exec mysql mysql -uroot -p -e "SELECT * FROM employee_db.employees;"
```

Enter the configured local root password when prompted. `database/setup.sql` contains the equivalent schema setup for a manually installed MySQL server.

## Test commands

```bash
(cd backend && mvn test)
(cd frontend && npm run build)
```

Backend tests use in-memory H2 and do not depend on the Codespace MySQL service. The Angular build includes TypeScript compilation.

## Troubleshooting

- **Docker is unavailable:** run `docker version` and `docker compose version`; check that the Codespace rebuild completed successfully.
- **MySQL is still starting:** inspect `docker compose ps` and `docker compose logs --tail=80 mysql`, then wait for health status.
- **Backend cannot connect:** confirm MySQL health and that the JDBC URL/credentials match the running container. A previously initialized volume retains its original MySQL password. If you change `MYSQL_PORT`, also change the port in `DB_URL`.
- **Angular cannot reach the API:** check both app terminals, `curl http://localhost:8080/api/employees`, and `frontend/proxy.conf.json`.
- **Ports are not listed:** add 4200 or 8080 through the **Ports** panel. Do not forward 3306 publicly.
- **Frontend install shows advisories:** do not run `npm audit fix --force` unless you intentionally want the Angular version upgrade and have rechecked compatibility.

The application is a lab demonstration without authentication, authorization, or production database hardening. Stop the Codespace when finished to avoid consuming compute time. Do not remove the database volume if you need to retain your saved records.
