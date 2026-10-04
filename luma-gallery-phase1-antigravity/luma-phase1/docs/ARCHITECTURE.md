# Luma Gallery — Phase 1 Architecture

## Stack
- Kotlin
- Jetpack Compose
- Material 3 where useful
- MediaStore
- Room
- DataStore
- Coroutines + Flow
- WorkManager only where background work is actually required
- Coil or an equivalent efficient Android image loader
- Media3/ExoPlayer for video playback if needed by the current project
- Hilt optional; use it only if already present or if dependency injection genuinely improves the project

## Suggested layers
```text
app/
├── core/
│   ├── common/
│   ├── database/
│   ├── media/
│   ├── permissions/
│   └── ui/
├── data/
│   ├── local/
│   ├── mediastore/
│   └── repository/
└── feature/
    ├── photos/
    ├── albums/
    ├── search/
    ├── viewer/
    ├── favorites/
    ├── settings/
    └── navigation/
```

## Media model
Keep media references as URI/string identifiers, not copied binary data.

Suggested fields:
- id
- contentUri
- displayName
- mimeType
- mediaType
- dateTaken/dateAdded
- size
- width
- height
- duration when video
- bucketId/bucketName where available
- favorite

Do not persist fields that are unnecessary for Phase 1.

## Database
Room should primarily store user-owned state:
- favorites
- optional lightweight cached index if justified
- settings should use DataStore instead

Avoid making Room a second copy of MediaStore unless there is a measured performance reason.

## MediaStore
Use ContentResolver queries with projection columns only. Sort at the source where possible. Handle Android version differences cleanly.

## Compose performance
- `LazyVerticalGrid` or equivalent
- stable keys based on media ID/URI
- remember only lightweight state
- avoid mutable collections that trigger unnecessary recompositions
- use derived state carefully
- don't perform ContentResolver queries directly inside composables

## Repository pattern
UI → ViewModel → Use case/repository → MediaStore/Room/DataStore

The UI must not directly access ContentResolver, Room DAO, or file APIs.

## Permissions
Use the correct Android media permission model for the device OS. Avoid requesting write/manage-all-files access unless a later feature truly requires it.

## Error handling
Represent loading/success/empty/error states explicitly. A single failed media item must not crash the entire grid.
