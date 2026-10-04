# Luma Gallery — Phase 1 Design System

## Design direction
Minimal, premium, photo-first, fast. The photos are the visual content; chrome stays quiet.

## Theme
### Light
- Background: white/near-white
- Primary text: near-black
- Secondary text: neutral gray
- Dividers: very subtle

### Dark
- Background: deep neutral
- Primary text: white
- Secondary text: muted gray

### AMOLED
- Background: pure black

Use one restrained accent color for selection and primary actions. Do not overuse colored surfaces.

## Typography
Use one font family. Prefer the platform/system font unless the existing project has a design system.

Suggested hierarchy:
- Screen title: 24sp
- Section title: 16–18sp
- Body: 14–16sp
- Metadata: 12–14sp

## Spacing
Use a consistent 4dp base grid. Common values: 4, 8, 12, 16, 20, 24, 32.

## Components
- TopAppBar
- BottomNavigation
- PhotoGrid
- PhotoGridItem
- DateHeader
- AlbumTile
- SearchBar
- SelectionToolbar
- ActionSheet/ModalBottomSheet
- SettingsRow
- EmptyState
- LoadingState
- ErrorState

## Grid
Default phone grid: 3 columns. Adapt based on available width. Maintain square thumbnails unless the user changes a later preference.

Do not put text overlays on every photo. Show video duration/type badges only when useful.

## Viewer
Edge-to-edge, dark surface by default while viewing media. Controls fade when idle. Gestures should feel standard and predictable.

## Accessibility
- Minimum comfortable touch targets
- Content descriptions for meaningful icons
- Do not rely only on color for selected state
- Respect system font scaling where practical
- Respect reduced-motion preference
