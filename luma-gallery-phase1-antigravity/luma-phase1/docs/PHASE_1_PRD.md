# Luma Gallery — Phase 1 PRD

## 1. Product
Luma is a local Android gallery focused on speed, privacy, and low-resource usage.

## 2. Phase 1 objective
Deliver a reliable replacement for a basic phone gallery. A user must be able to discover, browse, organize, view, search, favorite, share, and delete local photos/videos without an account or internet connection.

## 3. Target
- Android phones, including low-end devices
- 2–8 GB RAM target
- Large media libraries
- Portrait first, responsive to larger screens
- No mandatory internet

## 4. Core screens
### Photos
- Header: Luma, search shortcut, overflow
- All photos count
- Date-grouped grid
- Responsive 2–6 columns depending on available width
- Lazy loading
- Pinch/grid-density control may be implemented if it does not harm performance

### Albums
Show MediaStore-backed folders/collections and basic album navigation. Do not physically duplicate files.

### Search
- Search by display name/file name
- Sort/filter basics
- Empty results state

### Viewer
- Full-screen image viewing
- Swipe next/previous
- Pinch/double-tap zoom
- Video playback for supported device formats
- Favorite
- Share
- Delete
- Details: filename, date, size, dimensions, type when available

### Favorites
A simple local database flag. Removing a favorite must not affect the actual file.

### More
- Favorites
- Settings

### Settings
- Theme: System/Light/Dark/AMOLED
- Grid density
- Sort order
- Group by date toggle
- Animation preference/reduced motion if practical
- App version/about

## 5. First-run flow
Launch → explain local/privacy model → request media permission → show usable gallery as soon as possible → continue indexing in background.

If permission is denied, show a clear recovery screen with an Open Settings action where supported.

## 6. Selection flow
Long press → selection mode → select multiple → actions:
- Share
- Favorite/unfavorite
- Delete
- Cancel

## 7. Delete behavior
Ask for confirmation before deletion. Use modern MediaStore APIs and Android permission/user-confirmation requirements correctly for the target Android version. Do not claim that a deleted file can be restored unless a real trash implementation exists.

## 8. Offline/privacy constraints
- No backend
- No account
- No analytics
- No advertising
- No network dependency
- Do not request unrelated permissions

## 9. Non-goals
Editor, vault, encryption, AI, duplicate scanner, storage cleaner, map, cloud, PC transfer, memories, advanced metadata editing, social features.

## 10. Success criteria
A user with thousands of photos can open the app, see thumbnails quickly, scroll smoothly, open media, search by filename, favorite items, share them, and delete them without crashes or unnecessary network access.
