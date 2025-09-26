# Changelog & PR Notes

Summary: Migrated UI from Bootstrap to Tailwind via CDN and restyled pages for a modern, responsive look.

Files modified:
- [`src/main/resources/templates/fragments/head.html`](src/main/resources/templates/fragments/head.html:1)
- [`src/main/resources/templates/fragments/navbar.html`](src/main/resources/templates/fragments/navbar.html:1)
- [`src/main/resources/templates/search.html`](src/main/resources/templates/search.html:1)
- [`src/main/resources/templates/search-results.html`](src/main/resources/templates/search-results.html:1)
- [`src/main/resources/templates/homestay-details.html`](src/main/resources/templates/homestay-details.html:1)
- [`src/main/resources/templates/book.html`](src/main/resources/templates/book.html:1)
- [`src/main/resources/templates/login.html`](src/main/resources/templates/login.html:1)
- [`src/main/resources/templates/register.html`](src/main/resources/templates/register.html:1)
- [`RUNNING.md`](RUNNING.md:1)

What I changed:
- Replaced Bootstrap CDN with Tailwind CDN and added Google Font + Tailwind config + Alpine.js in head (see head fragment)
- Converted navbar to Tailwind utilities and added mobile menu (Alpine)
- Restyled search page to hero layout and modern form controls
- Restyled search results into responsive card grid with hover effects
- Restyled homestay details into two-column layout with sticky booking card
- Restyled booking, login, register forms with improved inputs and error states

Testing / How to preview
1. Start app: [`mvnw.cmd spring-boot:run`](RUNNING.md:1) (Windows) or [`./mvnw spring-boot:run`](RUNNING.md:1)
2. Open http://localhost:8080/search
3. Verify responsive behavior and forms

Suggested git commands
- git checkout -b feat/tailwind-ui
- git add -A
- git commit -m "feat(ui): migrate templates from Bootstrap to Tailwind CDN; restyle pages"
- git push origin feat/tailwind-ui

PR Title:
feat(ui): migrate site templates from Bootstrap to Tailwind (CDN) — improve visuals

PR Description (concise)
- Migrated all main templates from Bootstrap to Tailwind via CDN and restyled UI for a modern, responsive look.
- Added [`src/main/resources/templates/fragments/head.html`](src/main/resources/templates/fragments/head.html:1) with Tailwind CDN, Inter font, and Alpine.js.
- Updated navbar with responsive mobile menu (Alpine).
- Restyled search, results, homestay details, booking, login, and register pages.
- Added [`RUNNING.md`](RUNNING.md:1) with local run instructions.

Review checklist
- [ ] Run app and verify pages render correctly on desktop and mobile
- [ ] Check form submit flows (login/register/book) for regressions
- [ ] Confirm no leftover Bootstrap references (none found)
- [ ] Accessibility quick pass: color contrast and focus outlines

Notes and follow-ups
- Consider migrating to a full Tailwind build (npm/PostCSS) later for smaller CSS and custom themes
- If you want color tweaks, tell me which primary/accent hex codes to apply

End.