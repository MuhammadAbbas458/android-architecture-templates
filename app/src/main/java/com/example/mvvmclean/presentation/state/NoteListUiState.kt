package com.example.mvvmclean.presentation.state

import com.example.mvvmclean.domain.model.Note

sealed class NoteListUiState {

    data object Loading : NoteListUiState()

    data class Success(val notes: List<Note>) : NoteListUiState()

    data class Error(val message: String) : NoteListUiState()
}
