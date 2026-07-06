package com.example.mvvmclean.domain.usecase

import com.example.mvvmclean.domain.model.Note
import com.example.mvvmclean.domain.repository.NoteRepository
import javax.inject.Inject

/**
 * Creates a new note after validating its input.
 *
 * Validation is a business rule and therefore belongs to the domain layer,
 * not the ViewModel or the repository.
 */
class CreateNoteUseCase @Inject constructor(
    private val repository: NoteRepository,
) {
    suspend operator fun invoke(title: String, content: String): Result<Note> {
        val trimmedTitle = title.trim()
        if (trimmedTitle.isEmpty()) {
            return Result.failure(IllegalArgumentException("Title must not be blank"))
        }
        return runCatching { repository.createNote(trimmedTitle, content.trim()) }
    }
}
