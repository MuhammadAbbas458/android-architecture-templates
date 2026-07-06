# ADR-005: Single Activity with Compose Navigation

## Status

Accepted

## Context

The app has two destinations: the notes list and a note detail/create screen.
Classic Android would give each its own Activity (or Fragment). With Compose,
destinations are just composables, and multiple Activities would mean
duplicated theming, splash handling, and Intent-based argument passing.

## Decision

One `MainActivity` hosting a `NavHost` (Navigation Compose) with string
routes:

- `notes` — the list
- `notes/new` — create form
- `notes/{noteId}` — read-only detail, argument typed as Long

Screens are stateless: the activity-scoped `NoteListViewModel` provides a
single `StateFlow` of UI state, and screens emit `UserAction`s back. The
detail screen resolves its note from the already-loaded list state instead of
re-querying, which keeps the template at exactly the three use cases the
architecture defines. One-time `UiEvent`s (snackbar, navigate-back) are
consumed at the NavHost level in `MainActivity`.

The splash screen (`androidx.core:core-splashscreen`) also lives here: it
stays visible while the shared state is `Loading`, so the first frame the
user sees is real content.

## Consequences

- Navigation is plain Kotlin — testable screens, previewable with fake
  state, no Intent plumbing.
- A single ViewModel scope is enough for this app's size; a larger app would
  give each destination its own ViewModel (`hiltViewModel()` per back-stack
  entry) rather than growing this one.
- Cost: route strings are stringly-typed (typed routes need Navigation 2.8's
  Kotlin-serialization API, left out to keep the dependency list minimal),
  and process death restores navigation but not unsaved form input beyond
  what `rememberSaveable` covers.
