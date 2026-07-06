package com.example.mvvmclean.domain.usecase

import com.example.mvvmclean.domain.model.Note
import com.example.mvvmclean.domain.repository.NoteRepository
import javax.inject.Inject

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
