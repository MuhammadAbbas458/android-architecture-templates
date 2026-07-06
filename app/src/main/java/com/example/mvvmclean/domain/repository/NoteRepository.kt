package com.example.mvvmclean.domain.repository

import com.example.mvvmclean.domain.model.Note
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract owned by the domain layer.
 *
 * The data layer provides the implementation; the domain layer never knows
 * where notes come from (Room, network, or both).
 */
interface NoteRepository {

    /** Streams all notes. Emits again whenever the underlying data changes. */
    fun getNotes(): Flow<List<Note>>

    /** Persists a new note and returns it with its generated id. */
    suspend fun createNote(title: String, content: String): Note

    /** Deletes the note with the given [id]. */
    suspend fun deleteNote(id: Long)
}
