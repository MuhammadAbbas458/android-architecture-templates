# ADR-003: Hilt for dependency injection

## Status

Accepted

## Context

The object graph is small but real: database → DAO → repository → use cases →
ViewModel, plus OkHttp/Retrofit. Options considered:

- **Manual DI** - a hand-rolled `AppContainer`. Zero dependencies, but wiring
  grows quadratically and ViewModel factories are tedious.
- **Koin** - runtime service locator; light setup, but graph errors surface
  at runtime, not compile time.
- **Hilt** - Dagger under the hood with Android-aware components; graph
  errors fail the build.

## Decision

Hilt everywhere, via KSP. Three modules keep concerns separate:

- `DatabaseModule` - Room database and DAO
- `NetworkModule` - OkHttp, Retrofit, API service
- `RepositoryModule` - binds `NoteRepositoryImpl` to the domain's
  `NoteRepository` interface (`@Binds`, so the domain interface is the only
  thing ViewModels see)

Constructor injection everywhere else (`@Inject constructor`), including use
cases; `@HiltViewModel` handles ViewModel creation. No class is instantiated
manually.

## Consequences

- A missing or ambiguous binding is a compile error, not a runtime crash.
- Tests don't need Hilt at all: every class takes its dependencies through
  the constructor, so unit tests just pass fakes.
- Cost: KSP adds build time, and Hilt's annotations are viral (`@AndroidEntryPoint`,
  `@HiltAndroidApp`). We consider this the standard, best-supported choice
  for a template meant to be extended.
