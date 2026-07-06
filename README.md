# Android Architecture Templates



[![CI - mvvm-clean](https://img.shields.io/github/actions/workflow/status/MuhammadAbbas458/android-architecture-templates/ci-mvvm-clean.yml?label=mvvm-clean&logo=android&logoColor=white)](https://github.com/MuhammadAbbas458/android-architecture-templates/actions)

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](CONTRIBUTING.md)

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)

[![API](https://img.shields.io/badge/API-26%2B-brightgreen)](https://developer.android.com/about/versions/oreo)



Opinionated, production-ready Android project templates. Clone one, build your app - the architecture is already wired up, tested, and documented.



Each template ships a working **Notes app** so you can see the pattern applied end-to-end, not just the folder structure. Every architectural decision has an [ADR](mvvm-clean/docs/) explaining the tradeoff so you can adapt the template with confidence rather than guessing at intent.



---



## Templates



| Template | Pattern | Best for | Complexity |
|---|---|---|---|
| [`mvvm-clean`](mvvm-clean/) | MVVM + Clean Architecture | Most apps, team projects, greenfield | ⬛⬜⬜ |



> More templates coming soon - `mvi-compose` and `multi-module-clean` are in progress.



---



## Quick start



### Option A - use GitHub template (recommended)



1. Click **Use this template → Create a new repository**

2. Clone your new repo and open it in Android Studio

3. Rename the package: **Right-click `com.example.notes` → Refactor → Rename**

4. Run the app - a working Notes screen should launch immediately



### Option B - clone this repo



```bash

git clone https://github.com/MuhammadAbbas458/android-architecture-templates.git
cd android-architecture-templates/mvvm-clean
```



Open the `mvvm-clean` subfolder as the project root in Android Studio.



### Requirements



- Android Studio Hedgehog or newer

- JDK 17

- Android API 26+ target device or emulator



---



## Template overview



### `mvvm-clean`



```

app/src/main/
├── data/
│   ├── local/          # Room database, DAOs, entities
│   ├── remote/         # Retrofit service, DTOs
│   ├── repository/     # RepositoryImpl - wires local + remote
│   └── mapper/         # DTO → domain model conversions
├── domain/
│   ├── model/          # Plain Kotlin data classes, zero annotations
│   ├── repository/     # Repository interfaces (contracts only)
│   └── usecase/        # One class, one action, one public function
├── presentation/
│   ├── screens/        # Stateless Compose screens
│   ├── viewmodel/      # @HiltViewModel, StateFlow<UiState>
│   └── state/          # UiState, UiEvent, UserAction definitions
└── di/                 # Hilt modules: Database, Network, Repository
```



Tech stack: Jetpack Compose · ViewModel · StateFlow · Room · Retrofit · Hilt · Coroutines · Flow



[→ Full architecture doc](mvvm-clean/ARCHITECTURE.md) · [→ ADRs](mvvm-clean/docs/)



---



## What this template includes



- **A working Notes app** - list, create, and delete notes so every layer has real code in it

- **Dependency injection** - Hilt wired end-to-end, no manual DI boilerplate

- **Unit tests** - use case tests with fake repositories, ViewModel tests with Turbine

- **GitHub Actions CI** - build, lint, and test on every pull request

- **ARCHITECTURE.md** - ASCII layer diagram, data flow walkthrough, and testing strategy

- **ADR docs** - every non-obvious decision explained with context and reasoning



---



## Architecture Decision Records



| ADR | Decision |
|---|---|
| [ADR-001](mvvm-clean/docs/ADR-001-why-usecases.md) | Use cases instead of calling repositories directly from ViewModels |
| [ADR-002](mvvm-clean/docs/ADR-002-dto-vs-domain-model.md) | Separate DTOs and domain models with mapper functions |
| [ADR-003](mvvm-clean/docs/ADR-003-why-hilt.md) | Hilt for dependency injection |
| [ADR-004](mvvm-clean/docs/ADR-004-stateflow-vs-livedata.md) | StateFlow over LiveData for UI state |
| [ADR-005](mvvm-clean/docs/ADR-005-single-activity.md) | Single Activity with Compose Navigation |



---



## Contributing



Contributions are welcome. The most useful contributions are:



- Extending the template - adding Paging 3, DataStore, WorkManager, or offline-first caching

- Fixing outdated dependencies - keeping Gradle, Compose, and Hilt versions current

- Adding a new ADR - documenting a decision that isn't covered yet

- Improving test coverage - additional ViewModel or use case tests



Please read [CONTRIBUTING.md](CONTRIBUTING.md) before opening a pull request.



---



## Further reading



- [Now in Android](https://github.com/android/nowinandroid) - Google's reference app, same layered approach at production scale

- [Clean Architecture - Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html) - the original article

- [Android app architecture guide](https://developer.android.com/topic/architecture) - official documentation

- [Collecting flows from Android UIs](https://medium.com/androiddevelopers/a-safer-way-to-collect-flows-from-android-uis-23080b1f8bda) - why every ViewModel should expose a single UiState



---



## License



```

MIT License



Copyright (c) 2025 MuhammadAbbas458



Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:



The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.



THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
```



