# Antigravity Master Prompt — Luma Gallery Phase 1

You are the senior Android engineer implementing **Phase 1 of Luma Gallery**, an offline-first, privacy-focused gallery designed to remain fast on low-end Android phones.

## Your job
Implement only the Phase 1 scope described in this repository. Do not jump ahead to Phase 2.

## First action
1. Read `AGENTS.md`.
2. Read `docs/PHASE_1_PRD.md`.
3. Read `docs/ARCHITECTURE.md`.
4. Read `design/DESIGN_SYSTEM.md`.
5. Read `tasks/PHASE_1_ROADMAP.md`.
6. Read `tasks/PHASE_1_DEFINITION_OF_DONE.md`.
7. Inspect the current repository structure and existing Gradle configuration.
8. Identify Android SDK/minSdk/targetSdk and existing dependencies before modifying them.

## Implementation order
Build vertically in this order:

1. App shell and navigation
2. Permission flow
3. MediaStore repository
4. Media model/indexing
5. Photo grid with lazy loading
6. Date grouping and sorting
7. Viewer
8. Video playback
9. Albums/folders
10. Favorites
11. Multi-select/action bar
12. Share/delete
13. Search
14. Settings/theme
15. Loading/empty/error states
16. Performance pass

## Performance requirements
- Never decode full-resolution images for grid cells.
- Use lazy/virtualized Compose lists/grids.
- Avoid holding large Bitmaps in Compose state.
- Do not scan the entire media library repeatedly on every recomposition or navigation.
- Cache thumbnails appropriately but keep cache bounded.
- Use background work for expensive indexing.
- Prefer incremental MediaStore updates where practical.
- Avoid unnecessary animations.
- The UI must become usable before a huge library finishes indexing.

## Navigation
Bottom navigation:
- Photos
- Albums
- Search
- More

More contains:
- Favorites
- Settings

Do not add future feature destinations.

## Quality rules
After each logical milestone:
- compile the app
- fix errors immediately
- inspect logs for crashes
- verify navigation
- verify permissions
- verify rotation/configuration changes where relevant
- verify empty states

At the end, run a Phase 1 review against `tasks/PHASE_1_DEFINITION_OF_DONE.md` and report anything that cannot be verified.

## Important
If a requested change conflicts with the Phase 1 scope, stop and state that it belongs to a later phase instead of silently expanding scope.
