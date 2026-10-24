# Test Results

Run date: 2026-10-24. Test outcomes below reflect commands executed in this Codespace. The backend tests use H2 in-memory; separate API checks used the project's MySQL 8.4.13 Compose service and only created/deleted the disposable `Codespace Verification` test record.

| Test | Expected result | Actual result | Status |
|---|---|---|---|
| `cd backend && mvn test` | Backend integration suite passes without a developer MySQL database | 3 tests, 0 failures, 0 errors, 0 skipped | PASS |
| `cd backend && mvn package` | Backend compiles, tests pass, and executable JAR is created | Build succeeded; 3 tests passed; `employee-management-1.0.0.jar` created | PASS |
| MySQL startup: `docker compose up -d mysql` and health check | MySQL is healthy and accepts connections | `employee-mysql` became healthy; `mysqladmin ping` returned `mysqld is alive` | PASS |
| List employees | `GET /api/employees` returns 200 and JSON array | HTTP 200; initial response was `[]` | PASS |
| Create employee | Valid POST returns 201 and generated employee ID | HTTP 201; generated ID 1 | PASS |
| Retrieve one employee | GET by the saved ID returns 200 and saved record | HTTP 200 | PASS |
| Update employee | PUT returns 200 and retains the original ID | HTTP 200; department and salary updated | PASS |
| Reject blank name / department | Invalid values return 400 with a validation message | H2 integration tests separately rejected empty name and whitespace-only department with HTTP 400 | PASS |
| Reject missing salary | Missing required salary returns 400 | H2 integration test returned HTTP 400 and a salary validation message | PASS |
| Reject negative salary | Negative value returns 400 | H2 integration test and live API check returned HTTP 400 | PASS |
| Malformed JSON and invalid ID type | Malformed payload and nonnumeric path ID return 400 | H2 integration test returned HTTP 400 for both cases | PASS |
| Retrieve nonexistent ID | Unknown ID returns 404 with safe message | H2 integration test and live API check returned HTTP 404 | PASS |
| Verify MySQL persistence | Updated employee exists in `employee_db.employees` before deletion | SQL query found the test row with updated department and salary | PASS |
| Delete employee | Successful DELETE returns 204 | HTTP 204; removed only the temporary test record | PASS |
| Angular proxy | `GET http://localhost:4200/api/employees` forwards to backend | HTTP 200 and `[]` returned via the proxy | PASS |
| `cd frontend && npm install` | Dependencies install using the generated npm lockfile | Install succeeded; 414 packages audited; npm reported 0 vulnerabilities after the scoped Undici override | PASS |
| `cd frontend && npm run build` | Angular production bundle and TypeScript compile succeed | Build succeeded; one non-blocking component stylesheet warning exceeded the 6 KB warning budget | PASS |
| `cd frontend && npm audit` | Full dependency tree has no known advisories | 0 vulnerabilities found after overriding `@angular/build`'s pinned Undici version to the patched compatible 7.30.1+ line | PASS |
| Manual browser create/edit/delete/refresh | UI interactions work and records remain after browser refresh | Not exercised in a browser in this environment | NOT RUN |
| Eight genuine screenshots | Images show this application and real data | Not captured; follow `SCREENSHOT_GUIDE.md` in the Codespace | NOT RUN |

The integration tests verify endpoint response codes and service behavior using H2. The live MySQL checks verified REST status codes and the stored row. Manual browser interactions remain to be performed for submission.
