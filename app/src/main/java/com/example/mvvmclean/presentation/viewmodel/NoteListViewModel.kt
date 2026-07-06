package com.example.mvvmclean.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvmclean.domain.model.Note
import com.example.mvvmclean.domain.usecase.CreateNoteUseCase
import com.example.mvvmclean.domain.usecase.DeleteNoteUseCase
import com.example.mvvmclean.domain.usecase.GetNotesUseCase
import com.example.mvvmclean.presentation.state.NoteListUiState
import com.example.mvvmclean.presentation.state.UiEvent
import com.example.mvvmclean.presentation.state.UserAction
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class NoteListViewModel @Inject constructor(
    getNotes: GetNotesUseCase,
    private val createNote: CreateNoteUseCase,
    private val deleteNote: DeleteNoteUseCase,
) : ViewModel() {

    /**
     * Screen state derived directly from the notes stream. `stateIn` shares a
     * single upstream collection across all observers and keeps it alive for
     * five seconds after the UI stops listening, surviving rotation without
     * re-querying the database.
     */
    val uiState: StateFlow<NoteListUiState> = getNotes()
        .map<List<Note>, NoteListUiState> { notes -> NoteListUiState.Success(notes) }
        .catch { throwable ->
            emit(NoteListUiState.Error(throwable.message ?: "Something went wrong"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NoteListUiState.Loading,
        )

    /** Buffered channel so events fired while the UI is paused are not lost. */
    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events: Flow<UiEvent> = _events.receiveAsFlow()

    /** Single entry point for everything the user does on screen. */
    fun onAction(action: UserAction) {
        when (action) {
            is UserAction.CreateNote -> onCreateNote(action.title, action.content)
            is UserAction.DeleteNote -> onDeleteNote(action.id)
        }
    }

    private fun onCreateNote(title: String, content: String) {
        viewModelScope.launch {
            createNote(title, content)
                .onSuccess {
                    _events.send(UiEvent.NavigateBack)
                    _events.send(UiEvent.ShowSnackbar("Note created"))
                }
                .onFailure { throwable ->
                    _events.send(
                        UiEvent.ShowSnackbar(throwable.message ?: "Could not create note")
                    )
                }
        }
    }

    private fun onDeleteNote(id: Long) {
        viewModelScope.launch {
            deleteNote(id)
                .onSuccess { _events.send(UiEvent.ShowSnackbar("Note deleted")) }
                .onFailure { throwable ->
                    _events.send(
                        UiEvent.ShowSnackbar(throwable.message ?: "Could not delete note")
                    )
                }
        }
    }
}
