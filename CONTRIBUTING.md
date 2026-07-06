# Contributing

Thanks for your interest in improving these templates! Contributions of all sizes are welcome.

## What to contribute

The most useful contributions are:

- **Extending a template** — adding Paging 3, DataStore, WorkManager, or offline-first caching
- **Keeping dependencies current** — Gradle, AGP, Compose, Hilt, and Room versions
- **Adding an ADR** — documenting an architectural decision that isn't covered yet (see `mvvm-clean/docs/`)
- **Improving test coverage** — additional ViewModel, use case, or mapper tests

## Ground rules

Every template must keep its architecture rules intact:

1. **The dependency rule is non-negotiable.** The domain layer stays pure Kotlin — no `android.*`, `androidx.*`, `retrofit2.*`, or `room.*` imports.
2. **DTOs and entities never cross layer boundaries.** Only domain models are passed between layers.
3. **Screens stay stateless** — they receive UI state and emit user actions, nothing else.
4. **Every change must build and pass tests**: `./gradlew build` should be green before you open a PR.

## How to submit

1. Fork the repository and create a branch from `master`.
2. Make your change, including tests where behavior changes.
3. Run `./gradlew :app:testDebugUnitTest` locally.
4. Open a pull request with a clear description of *what* changed and *why*.

For larger changes (a new template, a new library integration), please open an issue first so we can discuss the approach before you invest the time.
