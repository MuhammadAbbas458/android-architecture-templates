package com.example.mvvmclean.presentation.state

/**
 * One-time side effects sent from the ViewModel to the UI through a Channel.
 *
 * Unlike [NoteListUiState] these must be consumed exactly once — showing a
 * snackbar again after a configuration change would be a bug.
 */
sealed class UiEvent {

    data class ShowSnackbar(val message: String) : UiEvent()

    data object NavigateBack : UiEvent()
}
