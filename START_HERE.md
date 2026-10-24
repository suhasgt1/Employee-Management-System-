# START HERE — Employee Management System

This repository is prepared for GitHub Codespaces. You do not need to install Java, Maven, Node.js, or MySQL on your Windows computer when using the configured Codespace.

## 1. Open the Codespace

1. Open the GitHub repository in a browser.
2. Select **Code → Codespaces → Create codespace** (or reopen your existing Codespace).
3. If the Codespace was created before `.devcontainer/devcontainer.json` was added or changed, open the Command Palette and run **Codespaces: Rebuild Container**. Rebuild only once after a dev-container configuration change.
4. Open a terminal at the repository root.

## 2. Start MySQL — Terminal 1

```bash
docker compose up -d mysql
docker compose ps
```

Wait for the service status to show `healthy`. To inspect startup messages:

```bash
docker compose logs --tail=80 mysql
```

The database is `employee_db`; MySQL data is stored in the `employee_mysql_data` Docker volume and survives container restarts.

## 3. Start Spring Boot — Terminal 2

Open another terminal at the repository root and run:

```bash
cd backend
mvn spring-boot:run
```

Check the API from a third terminal:

```bash
curl http://localhost:8080/api/employees
```

An empty database returns `[]`. The development defaults are `root` / `root`; configure custom values in your ignored `.env` file and export them in the backend terminal. See the root README for configuration details.

## 4. Start Angular — Terminal 3

Open a third terminal at the repository root:

```bash
cd frontend
npm install
npm start
```

Open port **4200** from the Codespaces **Ports** tab. The frontend sends requests to relative `/api` URLs; the Angular proxy forwards them to Spring Boot on port 8080.

## 5. Try the application and prepare evidence

Create a test employee, refresh the browser, edit the record, and delete it. The application must be connected to MySQL; it does not use dummy records. Capture genuine screenshots after running the app. The project report contains the schema and the course/lab/batch and submission date are already filled in.

## Common fixes

- **MySQL not healthy:** run `docker compose ps` and `docker compose logs --tail=80 mysql`.
- **Backend database connection error:** wait until MySQL is healthy; verify that the port and `DB_USERNAME` / `DB_PASSWORD` match the Compose credentials.
- **Frontend API error:** keep the backend terminal running and check `curl http://localhost:8080/api/employees`.
- **Port 4200 missing:** add it from the **Ports** panel; keep visibility private unless sharing is required.
- **Changed dev-container setup:** rebuild the Codespace from the Command Palette.
- **npm reports dependency advisories:** `npm install` may finish successfully while reporting issues in Angular's development build tool; do not apply a forced major Angular upgrade without checking compatibility.

For all run commands, API routes, and test commands, use the root [README.md](README.md).
