package com.example.mvvmclean.domain.usecase

import com.example.mvvmclean.domain.repository.NoteRepository
import javax.inject.Inject

/**
 * Deletes a note by id.
 */
class DeleteNoteUseCase @Inject constructor(
    private val repository: NoteRepository,
) {
    suspend operator fun invoke(id: Long): Result<Unit> =
        runCatching { repository.deleteNote(id) }
}
