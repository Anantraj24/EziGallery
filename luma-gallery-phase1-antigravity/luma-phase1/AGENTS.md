# Luma Gallery — Antigravity Agent Rules

## Project Goal
Build Phase 1 of Luma Gallery: a fast, offline-first Android gallery optimized for low-end phones.

## Non-negotiable rules
1. Phase 1 only. Do NOT implement future features unless explicitly requested.
2. No backend, account, cloud backup, analytics, ads, or network dependency.
3. Use Kotlin + Jetpack Compose.
4. Use Android MediaStore for media discovery. Do not depend on hard-coded filesystem paths.
5. Use Room only for app-owned metadata such as favorites and lightweight indexed data. Never copy media into Room.
6. Use Paging/lazy loading and thumbnail-sized decoding. Never load full-resolution images into the gallery grid.
7. Keep scrolling smooth on low-RAM devices.
8. Use lifecycle-aware coroutines and cancel work that is no longer needed.
9. Preserve original media files when deleting from the app; use Android MediaStore deletion and an app-level trash metadata flow only if safely supported. Do not invent unsafe recovery behavior.
10. Ask for the minimum required Android media permission based on OS version.
11. Every meaningful change must leave the project compiling.
12. Prefer simple, maintainable code over premature abstraction.

## Phase 1 features
- First-run media permission
- MediaStore photo/video scanning
- Fast date-grouped photo grid
- Responsive grid density
- Photo viewer
- Video playback/viewer
- Albums/folders based on MediaStore collections
- Favorites
- Multi-select
- Share
- Delete with confirmation
- Basic search by filename/display name
- Sort: newest, oldest, filename
- Light/dark/AMOLED themes
- Basic settings
- Empty/loading/error states

## Explicitly NOT Phase 1
- Photo editor
- Filters
- Crop/resize/compression
- Private vault
- Encryption
- AI search
- Face recognition
- Object recognition
- Duplicate detection
- Storage cleaner
- Map view
- Cloud backup
- PC transfer
- WebDAV/Nextcloud
- Memories
- Advanced EXIF editing
- Accounts
- Ads

## UI principles
- Photo-first, minimal UI.
- Edge-to-edge content.
- Bottom navigation: Photos, Albums, Search, More.
- Use spacing and typography before cards, borders, and shadows.
- Support phones and tablets through adaptive layouts.
- Large touch targets and accessible labels.

## Before coding
Read:
- docs/PHASE_1_PRD.md
- docs/ARCHITECTURE.md
- design/DESIGN_SYSTEM.md
- tasks/PHASE_1_ROADMAP.md
- tasks/PHASE_1_DEFINITION_OF_DONE.md

Then inspect the existing repository. Reuse existing code when safe. Do not rewrite unrelated code.
