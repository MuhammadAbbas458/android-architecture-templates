package com.example.mvvmclean.presentation.state

import com.example.mvvmclean.domain.model.Note

/**
 * Every state the notes screen can be in. The screen renders exactly one of
 * these — there is no way to show a loading spinner and an error at once.
 */
sealed class NoteListUiState {

    data object Loading : NoteListUiState()

    data class Success(val notes: List<Note>) : NoteListUiState()

    data class Error(val message: String) : NoteListUiState()
}
