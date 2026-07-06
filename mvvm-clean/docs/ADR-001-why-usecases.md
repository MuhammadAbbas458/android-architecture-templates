# ADR-001: Use cases instead of calling repositories directly from ViewModels

## Status

Accepted

## Context

The ViewModel needs to read and mutate notes. It could inject `NoteRepository`
directly — one less layer, less boilerplate. Many small apps do exactly that.

The question is where business rules live. In this template two already exist:

- Notes are sorted by last update, newest first (`GetNotesUseCase`)
- A note title must not be blank and input is trimmed (`CreateNoteUseCase`)

Without use cases those rules end up either in the ViewModel (untestable
without Android/lifecycle setup, duplicated when a second screen needs the
same rule) or in the repository (which should orchestrate data sources, not
enforce product rules).

## Decision

Every interaction between presentation and domain goes through a use case
with exactly one public `operator fun invoke`. Use cases are plain classes
with constructor-injected repositories, so a fake repository is all a test
needs.

## Consequences

- Business rules have one obvious home and are tested in isolation
  (`GetNotesUseCaseTest` runs without Android, mocks, or coroutine plumbing
  beyond `runTest`).
- ViewModels shrink to state mapping and event dispatch.
- Cost: pass-through use cases (like `DeleteNoteUseCase`) add a file that
  does little today. We accept the boilerplate for consistency — a mixed
  convention ("go through a use case only when there is logic") is harder to
  follow than a uniform one.
