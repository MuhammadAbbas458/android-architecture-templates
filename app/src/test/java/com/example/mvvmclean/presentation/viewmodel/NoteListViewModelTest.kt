package com.example.mvvmclean.presentation.viewmodel

import app.cash.turbine.test
import com.example.mvvmclean.domain.model.Note
import com.example.mvvmclean.domain.usecase.CreateNoteUseCase
import com.example.mvvmclean.domain.usecase.DeleteNoteUseCase
import com.example.mvvmclean.domain.usecase.GetNotesUseCase
import com.example.mvvmclean.presentation.state.NoteListUiState
import com.example.mvvmclean.presentation.state.UiEvent
import com.example.mvvmclean.presentation.state.UserAction
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NoteListViewModelTest {

    private val getNotes: GetNotesUseCase = mockk()
    private val createNote: CreateNoteUseCase = mockk()
    private val deleteNote: DeleteNoteUseCase = mockk()

    private val testDispatcher = StandardTestDispatcher()

    private val testNotes = listOf(
        Note(id = 1, title = "First", content = "one", createdAt = 1, updatedAt = 2),
        Note(id = 2, title = "Second", content = "two", createdAt = 3, updatedAt = 4),
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { getNotes() } returns flowOf(testNotes)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = NoteListViewModel(getNotes, createNote, deleteNote)

    @Test
    fun `uiState starts as Loading then emits Success with notes`() = runTest {
        val viewModel = buildViewModel()

        viewModel.uiState.test {
            assertEquals(NoteListUiState.Loading, awaitItem())
            assertEquals(NoteListUiState.Success(testNotes), awaitItem())
        }
    }

    @Test
    fun `uiState emits Error when the notes stream fails`() = runTest {
        every { getNotes() } returns flow { throw IOException("Database unavailable") }
        val viewModel = buildViewModel()

        viewModel.uiState.test {
            assertEquals(NoteListUiState.Loading, awaitItem())
            assertEquals(NoteListUiState.Error("Database unavailable"), awaitItem())
        }
    }

    @Test
    fun `successful create sends NavigateBack then confirmation snackbar`() = runTest {
        val created = Note(id = 3, title = "New", content = "note", createdAt = 5, updatedAt = 5)
        coEvery { createNote("New", "note") } returns Result.success(created)
        val viewModel = buildViewModel()

        viewModel.events.test {
            viewModel.onAction(UserAction.CreateNote("New", "note"))

            assertEquals(UiEvent.NavigateBack, awaitItem())
            assertEquals(UiEvent.ShowSnackbar("Note created"), awaitItem())
        }
    }

    @Test
    fun `failed create sends error snackbar and does not navigate`() = runTest {
        coEvery { createNote(any(), any()) } returns
            Result.failure(IllegalArgumentException("Title must not be blank"))
        val viewModel = buildViewModel()

        viewModel.events.test {
            viewModel.onAction(UserAction.CreateNote("", ""))

            assertEquals(UiEvent.ShowSnackbar("Title must not be blank"), awaitItem())
            expectNoEvents()
        }
    }

    @Test
    fun `successful delete sends confirmation snackbar`() = runTest {
        coEvery { deleteNote(1L) } returns Result.success(Unit)
        val viewModel = buildViewModel()

        viewModel.events.test {
            viewModel.onAction(UserAction.DeleteNote(1L))

            assertEquals(UiEvent.ShowSnackbar("Note deleted"), awaitItem())
        }
    }

    @Test
    fun `failed delete sends error snackbar`() = runTest {
        coEvery { deleteNote(1L) } returns Result.failure(IOException("Disk full"))
        val viewModel = buildViewModel()

        viewModel.events.test {
            viewModel.onAction(UserAction.DeleteNote(1L))

            assertEquals(UiEvent.ShowSnackbar("Disk full"), awaitItem())
        }
    }
}
