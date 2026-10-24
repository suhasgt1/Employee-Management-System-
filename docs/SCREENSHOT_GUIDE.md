# Screenshot Evidence Guide

Capture evidence from the real application running in your Codespace. Do not create, edit, or submit mock screenshots as proof. Use the Codespaces browser/desktop screenshot function or your operating system's screenshot tool. Save the eight images in `screenshots/` with the suggested names.

| # | Suggested filename | What to show and how to produce it | Suggested report caption |
|---|---|---|---|
| 1 | `01-project-setup.png` | VS Code Explorer showing `backend/`, Java packages, `frontend/src/app/`, `.devcontainer/`, and `docs/`. | Repository layout for the Angular, Spring Boot, and MySQL project. |
| 2 | `02-homepage.png` | Open the forwarded Angular port 4200 and show the Employee Management heading, form, and employee list/empty state. | Initial employee management page. |
| 3 | `03-create.png` | Enter a harmless test name, department, and non-negative salary. Submit and capture the success notice and resulting row. | Employee created successfully through the web form. |
| 4 | `04-list-read.png` | Show a saved row returned in the employee table. Optionally show the browser after a refresh to demonstrate records are reloaded. | Employee records read from the REST API and displayed. |
| 5 | `05-update.png` | In the employee list, note the row's ID and click **Edit**. Change a field and click **Save changes**. Capture the success message and updated row showing the same ID. The **No.** column is a display position and can change after deletion; the **ID** column is the stable database ID. | Existing employee updated without changing its ID. |
| 6 | `06-delete.png` | Confirm deletion of the test row and capture the resulting list after the successful response. | Employee removed after confirmed deletion. |
| 7 | `07-api-evidence.png` | Show a terminal with `curl -i http://localhost:8080/api/employees` and the JSON/status, or the Spring Boot startup and request evidence. | Spring Boot REST API response from the Codespace. |
| 8 | `08-database-evidence.png` | Show a MySQL client running `SELECT * FROM employee_db.employees;` and the table/result. Do not show passwords. | MySQL `employees` table and persisted records. |

## Capture workflow

1. Start MySQL, Spring Boot, and Angular using [START_HERE.md](../START_HERE.md).
2. Create a clearly temporary test employee and capture screenshots 3 and 4.
3. Edit that same record. Note its ID before editing; after saving, capture the success message, updated value, and unchanged ID for screenshot 5. The saved view shows the updated value and ID together.
4. Delete the temporary record and capture screenshot 6.
5. Capture API/backend and MySQL evidence before deleting the row if the evidence should show stored data.
6. Save the genuine files in this directory. The `README.md` here explains the expected location.
7. In `docs/PROJECT_REPORT.md`, replace each placeholder only after the corresponding genuine image exists. Retain an accurate caption and explain what the screenshot demonstrates.

Never expose database passwords, access tokens, private employee data, or unrelated browser tabs. Screenshots are not currently included because they must come from your own live Codespaces session.
