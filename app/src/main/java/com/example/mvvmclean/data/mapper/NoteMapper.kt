package com.example.mvvmclean.data.mapper

import com.example.mvvmclean.data.local.NoteEntity
import com.example.mvvmclean.data.remote.NoteDto
import com.example.mvvmclean.domain.model.Note

/**
 * Mapping between data-layer models and the domain [Note].
 *
 * These functions are the only place where [NoteDto], [NoteEntity] and [Note]
 * meet — everything above the repository sees domain models only.
 */

/** Maps a network DTO to the domain model. */
fun NoteDto.toDomain(): Note = Note(
    id = id,
    title = title,
    content = body,
    createdAt = 0L,
    updatedAt = 0L,
)

/** Maps a Room entity to the domain model. */
fun NoteEntity.toDomain(): Note = Note(
    id = id,
    title = title,
    content = content,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

/** Maps the domain model to a Room entity for persistence. */
fun Note.toEntity(): NoteEntity = NoteEntity(
    id = id,
    title = title,
    content = content,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

/** Maps a network DTO straight to a Room entity, used when caching a sync. */
fun NoteDto.toEntity(): NoteEntity = NoteEntity(
    id = id,
    title = title,
    content = body,
    createdAt = 0L,
    updatedAt = 0L,
)
