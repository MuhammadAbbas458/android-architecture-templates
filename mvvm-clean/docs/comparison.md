\# Template comparison



This document gives a straight, honest breakdown of the `mvvm-clean` template — what it is good for, where it breaks down, and what to expect when you use it.



> `mvi-compose` and `multi-module-clean` comparisons will be added when those templates are released.



\---



\## What is mvvm-clean?



`mvvm-clean` combines two well-known patterns:



\- \*\*MVVM\*\* — Model View ViewModel. The UI observes state from a ViewModel instead of managing it directly.

\- \*\*Clean Architecture\*\* — code is split into three strict layers (presentation, domain, data) with a one-way dependency rule. Inner layers know nothing about outer layers.



Together they give you a codebase where every class has one clear responsibility and every layer can be tested independently.



\---



\## At a glance



| | `mvvm-clean` |

|---|---|

| \*\*Pattern\*\* | MVVM + Clean Architecture |

| \*\*State management\*\* | `StateFlow<UiState>` per screen |

| \*\*Complexity\*\* | ⬛⬜⬜ Low |

| \*\*Onboarding cost\*\* | Low — MVVM is widely understood |

| \*\*Best team size\*\* | 1–5 developers |

| \*\*Build speed\*\* | Fast |

| \*\*Testability\*\* | High |

| \*\*Boilerplate\*\* | Moderate |

| \*\*Compose-first\*\* | Yes |

| \*\*Framework deps in domain\*\* | None |



\---



\## State management



The ViewModel exposes a single `StateFlow<UiState>`. Screens observe it and send `UserAction` events back. One-time effects (navigation, snackbar) go through a `Channel<UiEvent>`.



```kotlin

// ViewModel

private val \_uiState = MutableStateFlow<NoteListUiState>(Loading)

val uiState: StateFlow<NoteListUiState> = \_uiState.asStateFlow()



fun onAction(action: UserAction) {

&#x20;   when (action) {

&#x20;       is UserAction.DeleteNote -> deleteNote(action.id)

&#x20;       is UserAction.RefreshNotes -> loadNotes()

&#x20;   }

}

```



Works well for most screens. Gets awkward when a screen has many independent pieces of state updating at different times — but for the majority of apps this is never a problem.



\---



\## Testability



| Layer | Test type | What to use |

|---|---|---|

| Use cases | Unit test | Fake repository, `runTest` |

| ViewModel | Unit test | Fake use cases, Turbine for Flow |

| Mappers | Unit test | Plain assertions, zero mocks |

| Screens | UI test | `composeTestRule` + fake state |



Because the domain layer is pure Kotlin, use cases have zero Android dependencies — they run in plain JUnit tests with no emulator, no Robolectric, and no slow setup. This is the biggest practical benefit of Clean Architecture.



\---



\## Boilerplate per new screen



Every new screen requires these files:



```

1\. UiState sealed class

2\. UserAction sealed class

3\. UiEvent sealed class

4\. ViewModel with StateFlow

5\. Compose screen function

6\. Navigation destination in NavGraph

```



Most of this is a copy-paste from the previous screen and takes about 10 minutes per screen once you know the pattern.



\---



\## When mvvm-clean breaks down



Being honest about failure modes helps you make the right call:



\- \*\*Too many independent async states on one screen\*\* — a dashboard loading 5 different data sources simultaneously gets messy. You end up either overloading `UiState` or managing multiple flows, which starts to look like MVI anyway.

\- \*\*Team larger than \~6 developers\*\* — everyone committing to a single module causes frequent merge conflicts, especially in `NavGraph` and shared files.

\- \*\*Build times at very large scale\*\* — a single-module project recompiles everything on every change. This is not a problem until the codebase is very large (100k+ lines).



For most apps and most teams, none of these apply.



\---



\## When to use mvvm-clean



Use this template if:



\- You are starting a new app and want a solid, well-understood foundation

\- You are working with a team that already knows MVVM

\- You want the largest community of examples, blog posts, and Stack Overflow answers

\- You are not sure which pattern to pick — this is the right default



\---



\## Migrating away from mvvm-clean



If your requirements change and you outgrow this template:



\*\*mvvm-clean → multi-module\*\* — medium effort. The domain and data logic does not change. You are splitting the existing code into separate Gradle modules. Plan for 1–3 days per feature.



\*\*mvvm-clean → MVI\*\* — low effort. The domain layer is identical. You are only replacing the ViewModel state management and adding Intent/SideEffect classes. Can be done one screen at a time.



\---



\## ADR index



| ADR | Decision |

|---|---|

| \[ADR-001](ADR-001-why-usecases.md) | Use cases instead of calling repositories directly |

| \[ADR-002](ADR-002-dto-vs-domain-model.md) | Separate DTOs and domain models |

| \[ADR-003](ADR-003-why-hilt.md) | Hilt over Koin or manual DI |

| \[ADR-004](ADR-004-stateflow-vs-livedata.md) | StateFlow over LiveData |

| \[ADR-005](ADR-005-single-activity.md) | Single Activity with Compose Navigation |



