# ADR-004: StateFlow over LiveData for UI state

## Status

Accepted

## Context

The ViewModel must expose observable screen state. LiveData was the
historical answer; StateFlow is the coroutines-native one. Since the data
layer already produces `Flow` (Room) and the UI is Compose, LiveData would
add a conversion at both ends for no benefit.

## Decision

- Screen state: `StateFlow<NoteListUiState>` built with
  `stateIn(viewModelScope, WhileSubscribed(5_000), Loading)`. The 5-second
  timeout keeps the upstream Room subscription alive across configuration
  changes but stops it when the app is truly backgrounded.
- One-time effects (snackbars, navigation): a `Channel<UiEvent>` exposed as a
  `Flow`. State can be re-rendered any number of times; events must be
  consumed exactly once - putting them in state causes the classic
  "snackbar reappears on rotation" bug.
- Collection in Compose uses `collectAsStateWithLifecycle()` so flows stop
  when the UI is not at least STARTED.

## Consequences

- One reactive vocabulary (Flow operators) from Room to the screen; the
  Loading → Success/Error mapping is a two-operator chain (`map` + `catch`).
- Testable with plain coroutines tooling - `NoteListViewModelTest` uses
  Turbine and a test dispatcher, no `InstantTaskExecutorRule`.
- StateFlow always has a value, so there is no "no state yet" branch in the
  UI; the initial value is explicitly `Loading`.
- Cost: the state/event split is more concepts than a single LiveData, and
  `WhileSubscribed` timing can surprise newcomers. Both are documented in the
  ViewModel comments.
