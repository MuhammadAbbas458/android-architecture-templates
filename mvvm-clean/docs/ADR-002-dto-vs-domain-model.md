# ADR-002: Separate DTOs and domain models with mapper functions

## Status

Accepted

## Context

A note exists in three shapes: what the API returns, what Room stores, and
what the app logic works with. The tempting shortcut is one class annotated
for everything — `@Entity` for Room, `@SerializedName` for Gson — passed
through every layer.

That couples the whole app to external contracts: renaming a JSON field or
adding a database column forces changes in screens and ViewModels, and the
domain layer silently inherits framework annotations.

## Decision

Three types, one per concern, meeting only inside `data/mapper/NoteMapper.kt`:

- `NoteDto` — mirrors the network response (Gson annotations)
- `NoteEntity` — mirrors the database table (Room annotations)
- `Note` — plain Kotlin data class, the only type that crosses layer
  boundaries

Mapping is done with simple extension functions (`toDomain()`, `toEntity()`),
not a mapping library.

## Consequences

- API and schema changes are absorbed at the boundary; presentation and
  domain code never notice.
- The domain layer stays free of framework imports (enforced by review; see
  the dependency rule in ARCHITECTURE.md).
- Mappers are trivially testable with plain assertions (`NoteMapperTest`
  uses zero mocks).
- Cost: three near-identical classes and hand-written mapping for every new
  model. For CRUD-heavy apps this is the main tax of Clean Architecture; we
  pay it to keep the boundaries real rather than decorative.
