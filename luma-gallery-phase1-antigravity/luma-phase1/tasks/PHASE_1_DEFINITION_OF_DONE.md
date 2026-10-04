# Phase 1 Definition of Done

## Functional
- [ ] App launches without crash
- [ ] Media permission flow works on supported Android versions
- [ ] Photos load from MediaStore
- [ ] Videos appear correctly
- [ ] Grid scrolls through large libraries
- [ ] Date grouping works
- [ ] Sorting works
- [ ] Viewer opens images
- [ ] Viewer navigates next/previous
- [ ] Zoom works without obvious crashes
- [ ] Supported videos play
- [ ] Albums/folders open
- [ ] Favorite/unfavorite works and persists
- [ ] Multi-select works
- [ ] Share works through Android Sharesheet
- [ ] Delete asks for confirmation and follows Android MediaStore rules
- [ ] Search finds matching filenames/display names
- [ ] Theme setting works
- [ ] Settings persist
- [ ] Empty and error states are usable

## Performance
- [ ] No full-resolution decoding in the grid
- [ ] Lazy loading is used
- [ ] Large libraries do not require loading all items into memory
- [ ] No obvious bitmap memory leak
- [ ] Background work does not block the main thread
- [ ] App remains usable while indexing
- [ ] Low-end performance has been considered explicitly

## Privacy
- [ ] No backend required
- [ ] No account required
- [ ] No analytics/ads added
- [ ] No unnecessary permissions
- [ ] No network dependency for Phase 1 functionality

## Engineering
- [ ] No compile errors
- [ ] No known fatal runtime exceptions in core flows
- [ ] UI state is lifecycle-aware
- [ ] Repository boundaries are respected
- [ ] No future-phase code has been added just for completeness
