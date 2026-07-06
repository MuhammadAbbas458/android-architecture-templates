package com.example.mvvmclean.domain.usecase

import com.example.mvvmclean.domain.model.Note
import com.example.mvvmclean.domain.repository.NoteRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Streams all notes, most recently updated first. */
class GetNotesUseCase @Inject constructor(
    private val repository: NoteRepository,
) {
    operator fun invoke(): Flow<List<Note>> =
        repository.getNotes().map { notes ->
            notes.sortedByDescending { it.updatedAt }
        }
}
