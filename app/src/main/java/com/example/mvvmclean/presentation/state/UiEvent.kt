package com.example.mvvmclean.presentation.state

/**
 * One-shot effects, sent over a Channel so they are consumed exactly once.
 * Keeping these in UiState would replay them after a configuration change.
 */
sealed class UiEvent {

    data class ShowSnackbar(val message: String) : UiEvent()

    data object NavigateBack : UiEvent()
}
