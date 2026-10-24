# GitHub and Submission Guide

This project is already in a Git repository. Do not run `git init` in this Codespace. Review all changes and decide what to include before committing. No commit or push is performed for you.

## 1. Confirm the GitHub repository

1. Open the current repository page in GitHub and confirm that it is the intended course repository.
2. If starting a different repository, create it in GitHub and choose whether its visibility should be public or private according to the evaluator's requirements.
3. Do not add a second README or `.gitignore` if the repository already contains these files.
4. Confirm that Git is connected to the intended remote:

   ```bash
   git remote -v
   git status --short --branch
   ```

5. Do not change the remote unless you intend to publish to a different repository.

## 2. Review and stage the source

Check ignored files and inspect the proposed changes:

```bash
git status --short --untracked-files=all
git check-ignore .env frontend/node_modules backend/target
git diff --check
git diff --stat
```

Create `.env` only for local settings. It is ignored by Git; never stage it. The source staging command below intentionally leaves the pre-existing `backend/package-lock.json` un-staged because this Java backend has no npm package manifest:

```bash
git add .devcontainer .env.example .gitignore README.md START_HERE.md compose.yaml database docs frontend screenshots backend/pom.xml backend/src
git diff --cached --check
git diff --cached --stat
git status --short
```

Review the staged content before committing. Do not stage passwords, tokens, personal data, `node_modules`, Angular build output, Maven `target`, or IDE state.

## 3. Commit and push when ready

The commands below save the reviewed source to the current `main` branch and publish it to its configured `origin`. Run them only when you are ready to publish:

```bash
git commit -m "Complete employee management system" \
  -m "Co-authored-by: Copilot <223556219+Copilot@users.noreply.github.com>"
git push origin main
```

If you work on a different branch, replace `main` with that branch. GitHub authentication should use the browser/credential manager; do not put a token or password in a URL or command. If commit identity is not configured, set your preferred name and email in Git before committing.

## 4. Verify the files on GitHub

1. Refresh the intended repository page.
2. Check the project folders, source, `.devcontainer`, README, report, tests, and guides.
3. Check that `.env`, `node_modules`, `dist`, `.angular`, and `backend/target` are absent.
4. Copy the repository URL into the report and the course submission form. If the repository is private, grant the evaluator access using the process allowed by your course.

## 5. Download a source ZIP

After committing the version you want to submit, use GitHub's **Code → Download ZIP**. It contains committed files only and excludes untracked dependencies/build output. Alternatively, from the repository root:

```bash
git archive --format=zip --output ../employee-management-system.zip HEAD
```

Inspect the ZIP before uploading it. Add the genuine screenshots and completed report first if they are required in the ZIP.

## 6. Submit the report and evidence

1. Fill in the course/lab/batch and submission date in `docs/PROJECT_REPORT.md`.
2. Review `docs/TEST_RESULTS.md` and keep each status accurate. Do not change `NOT RUN` to `PASS` without actually running that test.
3. Capture the eight real screenshots described in `docs/SCREENSHOT_GUIDE.md`; put them in `screenshots/`.
4. Add the GitHub link to the report and verify the evaluator can open it.
5. Submit the report, source repository or ZIP, and evidence in the format requested by the lab.

`docs/PROJECT_REPORT.md` is the complete Markdown source. If a Word/PDF converter is not installed, keep the Markdown report as the source and follow the conversion instructions at the end of that document rather than submitting an outdated template as if it were current.
