# Git Commit & PR Instructions

Branch:
- git checkout -b feat/tailwind-ui

Staging and commit:
- git add -A
- git commit -m "feat(ui): migrate templates from Bootstrap to Tailwind CDN; restyle pages for modern responsive UI"
- git push -u origin feat/tailwind-ui

PR Title:
feat(ui): migrate site templates from Bootstrap to Tailwind (CDN) — improve visuals

PR Description:
- Migrated templates to Tailwind via CDN and restyled UI for a modern, responsive look.
- Key files changed:
  - [`src/main/resources/templates/fragments/head.html`](src/main/resources/templates/fragments/head.html:1)
  - [`src/main/resources/templates/fragments/navbar.html`](src/main/resources/templates/fragments/navbar.html:1)
  - [`src/main/resources/templates/search.html`](src/main/resources/templates/search.html:1)
  - [`src/main/resources/templates/search-results.html`](src/main/resources/templates/search-results.html:1)
  - [`src/main/resources/templates/homestay-details.html`](src/main/resources/templates/homestay-details.html:1)
  - [`src/main/resources/templates/book.html`](src/main/resources/templates/book.html:1)
  - [`src/main/resources/templates/login.html`](src/main/resources/templates/login.html:1)
  - [`src/main/resources/templates/register.html`](src/main/resources/templates/register.html:1)
  - [`RUNNING.md`](RUNNING.md:1)
  - [`PR_NOTES.md`](PR_NOTES.md:1)

Review checklist:
- [ ] Run app and verify main pages render correctly: open http://localhost:8080/search
- [ ] Verify responsive behavior on mobile/tablet/desktop
- [ ] Check form submission flows (login/register/book) for regressions
- [ ] Confirm there are no leftover Bootstrap references (search completed)
- [ ] Quick accessibility check: focus outlines, color contrast, form labels

Optional follow-ups:
- Convert to full Tailwind build (npm/PostCSS) for production optimizations and custom theme
- Add visual variations (dark mode, theme colors) once CI review passes

Notes:
See [`PR_NOTES.md`](PR_NOTES.md:1) for changelog and preview instructions, and [`RUNNING.md`](RUNNING.md:1) for local run commands.