package com.example.mvvmclean.domain.repository

import com.example.mvvmclean.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    fun getNotes(): Flow<List<Note>>

    /** Returns the created note with its generated id. */
    suspend fun createNote(title: String, content: String): Note

    suspend fun deleteNote(id: Long)
}
