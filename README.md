# Ezi Gallery 📸

A high-performance, offline-first Android gallery app crafted with **Kotlin**, **Jetpack Compose**, and **Material 3**. Designed specifically to run ultra-smoothly on low-end devices (2–4 GB RAM) while easily handling massive photo libraries with tens of thousands of media items.

---

## 🌟 Key Features (Phase 1)

- 🔒 **100% Offline & Privacy-First**: Zero tracking, zero telemetry, no accounts, and no network dependencies. Your media never leaves your device.
- ⚡ **Low-RAM & Low-Resource Optimized**:
  - Incremental MediaStore indexing with projection queries.
  - Strict thumbnail-level decoding (320x320) via Coil for grid cells to eliminate out-of-memory crashes.
  - Bounded memory cache (25% max heap) and 100 MB disk thumbnail cache.
  - Virtualized Compose Lazy grids with stable keys.
- 📅 **Date Grouped Photo Grid**: Smart chronological headers (*Today*, *Yesterday*, full dates) with seamless scrolling.
- 📐 **Adaptive Grid Density**: Customize column density between 2, 3, 4, or 5 columns on phones and tablets.
- 📁 **Albums & Folder Organization**: Automatically discovers and aggregates collections based on MediaStore buckets.
- 🔍 **Instant Local Search**: Real-time filename, extension, and folder search with debouncing.
- 🖼️ **Fullscreen Media Viewer**:
  - Horizontal swipe pager.
  - Pinch-to-zoom and double-tap zoom gestures.
  - Auto-hiding immersive chrome.
  - Detailed EXIF & metadata inspector sheet (dimensions, size, date, file path, MIME type).
- 🎬 **Video Playback**: Built-in AndroidX Media3 ExoPlayer integration for seamless local video playback.
- ❤️ **Local Favorites**: Fast Room-backed favorite flags that persist across launches without altering media files.
- ☑️ **Multi-Select & Bulk Actions**: Long-press to enter selection mode, select all, share via Android Sharesheet, or bulk delete.
- 🗑️ **Safe MediaStore Deletion**: Fully compliant with modern Android scoped storage (Android 11+ `createDeleteRequest` PendingIntent).
- 🎨 **Multiple Color Themes**: Support for **System Default**, **Light**, **Dark**, and **AMOLED** (true pitch black for OLED battery saving).

---

## 🏗️ Architecture & Technology Stack

- **UI**: 100% Declarative Jetpack Compose + Material 3 + Edge-to-Edge display
- **Language**: Kotlin 2.0
- **Asynchrony**: Kotlin Coroutines + Flow
- **Data Source**: Android `MediaStore` (ContentResolver with projection queries)
- **Local Database**: AndroidX Room (App-owned favorites & lightweight metadata)
- **Preferences**: AndroidX DataStore (Theme, Grid Density, Sort Order, Grouping)
- **Image Loading**: Coil 2.7 with VideoFrameDecoder
- **Video Playback**: AndroidX Media3 ExoPlayer 1.4.1
- **Target OS**: Android 8.0 (API 26) through Android 15 (API 35)

---

## 📂 Project Structure

```text
app/src/main/java/com/ezi/gallery/
├── EziGalleryApp.kt              # Application class (initializes Room, DataStore, Coil ImageLoader)
├── MainActivity.kt               # Edge-to-edge entry point & dynamic theme provider
├── core/
│   ├── model/                    # MediaItem, MediaDateGroup, AlbumItem, Enums
│   ├── permissions/              # Android 14+ Partial / Full media permission handler
│   ├── preferences/              # DataStore persistence for user settings
│   ├── theme/                    # Color palettes (Light, Dark, AMOLED), Typography, Theme
│   └── util/                     # DateFormatter & FileUtils
├── data/
│   ├── local/                    # Room Database, FavoriteEntity, FavoriteDao
│   ├── mediastore/               # MediaStoreScanner query engine
│   └── repository/               # MediaRepository bridging MediaStore, Room, Intents
└── feature/
    ├── albums/                   # AlbumsScreen & AlbumDetailScreen
    ├── common/                   # MediaThumbnail, DateHeader, EmptyState, LoadingState
    ├── favorites/                # FavoritesScreen
    ├── more/                     # MoreScreen hub
    ├── navigation/               # Screen routes & AppNavHost bottom navigation
    ├── photos/                   # PhotosScreen & PhotosViewModel
    ├── search/                   # SearchScreen & SearchViewModel
    ├── settings/                 # SettingsScreen & SettingsViewModel
    └── viewer/                   # MediaViewerScreen, ZoomableImageView, VideoPlayerView
```

---

## 🚀 Building & Running

### Prerequisites
- JDK 17 (recommended: JetBrains Runtime 17)
- Android SDK (API 34+)

### Build via Command Line
```bash
# Debug compile
./gradlew assembleDebug

# Run unit tests
./gradlew test
```

---

## 📄 License
This project is open-source under the [MIT License](LICENSE).
