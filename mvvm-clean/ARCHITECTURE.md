\# Architecture — mvvm-clean



This document explains how the `mvvm-clean` template is structured, why each layer exists, and how data flows through the app. Every significant design decision has a linked ADR so you can understand the tradeoff, not just the outcome.



\---



\## Layer diagram



```

┌─────────────────────────────────────────────────────────┐

│                     :app module                         │

│                                                         │

│  ┌───────────────────────────────────────────────────┐  │

│  │               Presentation layer                  │  │

│  │                                                   │  │

│  │   ┌─────────────────┐     ┌────────────────────┐  │  │

│  │   │  Compose screen │────▶│    ViewModel       │  │  │

│  │   │  (stateless UI) │◀────│  StateFlow<UiState>│  │  │

│  │   └─────────────────┘     └────────┬───────────┘  │  │

│  └────────────────────────────────────│───────────────┘  │

│                                       │ calls             │

│  ┌────────────────────────────────────▼───────────────┐  │

│  │                  Domain layer                      │  │

│  │            (pure Kotlin — no Android)              │  │

│  │                                                    │  │

│  │   ┌──────────────┐      ┌────────────────────┐    │  │

│  │   │   UseCase    │─────▶│  Repository        │    │  │

│  │   │  (one action)│      │  (interface only)  │    │  │

│  │   └──────────────┘      └────────────────────┘    │  │

│  └─────────────────────────────────────│──────────────┘  │

│                                        │ implemented by   │

│  ┌─────────────────────────────────────▼──────────────┐  │

│  │                   Data layer                       │  │

│  │                                                    │  │

│  │   ┌──────────────────┐    ┌──────────────────────┐ │  │

│  │   │RepositoryImpl    │    │  RemoteDataSource    │ │  │

│  │   │(maps DTO→model)  │───▶│  (Retrofit/API)      │ │  │

│  │   │                  │    └──────────────────────┘ │  │

│  │   │                  │    ┌──────────────────────┐ │  │

│  │   │                  │───▶│  LocalDataSource     │ │  │

│  │   └──────────────────┘    │  (Room DAO)          │ │  │

│  │                           └──────────────────────┘ │  │

│  └────────────────────────────────────────────────────┘  │

└─────────────────────────────────────────────────────────┘

```



\*\*Dependency rule:\*\* arrows point inward only. The domain layer has zero knowledge of Android, Retrofit, Room, or any framework. The data layer depends on domain interfaces. The presentation layer depends on domain models, never on data classes or DTOs.



\---



\## The three layers



\### Presentation layer



\*\*Packages:\*\* `presentation/screens/`, `presentation/viewmodel/`, `presentation/state/`



The presentation layer owns everything the user sees and interacts with. Compose screens are stateless — they receive a `UiState` data class and emit events back to the ViewModel. The ViewModel holds the only mutable state and exposes it as a `StateFlow<UiState>`.



```

UiState        — a sealed class or data class representing every possible screen state

UiEvent        — one-time side effects (navigation, snackbar) emitted via a Channel

UserAction     — what the screen sends back to the ViewModel (button taps, input changes)

```



Key rule: screens never call use cases directly, never hold coroutine scopes, and never reference Room or Retrofit types.



See \[ADR-004 — why StateFlow over LiveData](docs/ADR-004-stateflow-vs-livedata.md).



\---



\### Domain layer



\*\*Packages:\*\* `domain/model/`, `domain/repository/`, `domain/usecase/`



The domain layer is the heart of the app and the only layer with no framework dependencies. It contains:



\- \*\*Models\*\* — plain Kotlin data classes representing your business concepts. No `@Entity`, no `@SerializedName`.

\- \*\*Repository interfaces\*\* — contracts that the data layer must fulfil. The domain layer defines \_what\_ it needs; the data layer provides \_how\_.

\- \*\*Use cases\*\* — one class, one public function, one responsibility. A use case orchestrates one user-facing action (e.g. `GetNotesUseCase`, `CreateNoteUseCase`). It calls repository methods and applies any business logic.



```kotlin

class GetNotesUseCase @Inject constructor(

&#x20;   private val repository: NoteRepository

) {

&#x20;   operator fun invoke(): Flow<List<Note>> = repository.getNotes()

}

```



Because the domain layer is pure Kotlin, every use case is trivial to unit test with a fake repository — no Robolectric, no instrumentation.



See \[ADR-001 — why use cases instead of calling the repository directly](docs/ADR-001-why-usecases.md).



\---



\### Data layer



\*\*Packages:\*\* `data/repository/`, `data/local/`, `data/remote/`, `data/mapper/`



The data layer implements the repository interfaces defined in the domain layer. A `RepositoryImpl` class receives both a `RemoteDataSource` (Retrofit) and a `LocalDataSource` (Room DAO) and decides which to call and when.



DTOs live here and never cross into the domain. Mapper functions (`NoteDto.toDomain()`, `NoteEntity.toDomain()`) live in `data/mapper/` and are the only place where framework types touch domain types.



```

NoteDto        — Retrofit response shape (@SerializedName)

NoteEntity     — Room table shape (@Entity)

Note           — domain model (no annotations)

NoteMapper     — NoteDto → Note, NoteEntity → Note, Note → NoteEntity

```



See \[ADR-002 — why separate DTOs and domain models](docs/ADR-002-dto-vs-domain-model.md).



\---



\## Dependency injection



Hilt is the DI framework. Modules live in `di/` and are split by concern:



| Module | Provides |

|---|---|

| `DatabaseModule` | `NoteDatabase`, `NoteDao` |

| `NetworkModule` | `Retrofit`, `NoteApiService` |

| `RepositoryModule` | `NoteRepository` → `NoteRepositoryImpl` binding |



The domain layer has no Hilt annotations. Use cases are injected into ViewModels via `@HiltViewModel`. The domain layer stays framework-free.



See \[ADR-003 — why Hilt over manual DI or Koin](docs/ADR-003-why-hilt.md).



\---



\## Data flow — end to end



A complete user action follows this path:



```

1\. User taps "Add note" in Compose screen

&#x20;        │

&#x20;        ▼

2\. Screen calls viewModel.onAction(CreateNote(title, body))

&#x20;        │

&#x20;        ▼

3\. ViewModel calls createNoteUseCase(title, body)

&#x20;        │

&#x20;        ▼

4\. CreateNoteUseCase calls noteRepository.createNote(note)

&#x20;        │

&#x20;        ▼

5\. NoteRepositoryImpl maps Note → NoteEntity

&#x20;  calls noteDao.insert(entity)          ← Room write

&#x20;  (optionally) calls apiService.post()  ← Retrofit write

&#x20;        │

&#x20;        ▼

6\. Room emits updated Flow<List<NoteEntity>>

&#x20;  RepositoryImpl maps NoteEntity → Note

&#x20;  emits Flow<List<Note>> back up to ViewModel

&#x20;        │

&#x20;        ▼

7\. ViewModel maps List<Note> → UiState.Success(notes)

&#x20;  StateFlow emits new state

&#x20;        │

&#x20;        ▼

8\. Compose screen recomposes with updated list

```



No step skips a layer. No step passes a framework type across a boundary.



\---



\## Testing strategy



| Layer | Test type | What to use |

|---|---|---|

| Domain — use cases | Unit test | Fake repository, `runTest`, no Android deps |

| Data — repository | Unit test | `mockk` for DAO + API, in-memory Room |

| Data — mappers | Unit test | Plain assertions, zero mocks needed |

| Presentation — ViewModel | Unit test | Fake use cases, `Turbine` for Flow assertions |

| Presentation — screens | UI test | `composeTestRule`, fake ViewModel state |



Start with use case and mapper tests — they are the fastest to write and give the most confidence.



\---



\## ADR index



| # | Decision | Status |

|---|---|---|

| \[ADR-001](docs/ADR-001-why-usecases.md) | Use cases instead of direct repository calls from ViewModel | Accepted |

| \[ADR-002](docs/ADR-002-dto-vs-domain-model.md) | Separate DTOs and domain models with mapper functions | Accepted |

| \[ADR-003](docs/ADR-003-why-hilt.md) | Hilt for dependency injection | Accepted |

| \[ADR-004](docs/ADR-004-stateflow-vs-livedata.md) | StateFlow over LiveData for UI state | Accepted |

| \[ADR-005](docs/ADR-005-single-activity.md) | Single Activity with Compose Navigation | Accepted |



\---



\## What this template does not include



This template is intentionally minimal so it stays readable. The following are common additions you may want — each is a natural "good first issue" for contributors:



\- Paging 3 integration for large lists

\- Offline-first caching strategy in `RepositoryImpl`

\- Navigation type-safety with Compose Destinations or the new `NavController` type-safe APIs

\- Proto DataStore for user preferences

\- WorkManager for background sync



See the \[open issues](../../issues) for community-contributed extensions.



\---



\## Further reading



\- \[Now in Android](https://github.com/android/nowinandroid) — Google's reference app using the same layered approach at scale

\- \[Clean Architecture by Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html) — the original article

\- \[Android app architecture guide](https://developer.android.com/topic/architecture) — official Android documentation

\- \[Why every ViewModel should expose a single UiState](https://medium.com/androiddevelopers/a-safer-way-to-collect-flows-from-android-uis-23080b1f8bda) — Android developers blog

