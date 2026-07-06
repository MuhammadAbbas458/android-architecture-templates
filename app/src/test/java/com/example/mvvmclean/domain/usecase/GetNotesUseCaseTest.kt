package com.example.mvvmclean.domain.usecase

import com.example.mvvmclean.domain.model.Note
import com.example.mvvmclean.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetNotesUseCaseTest {

    private class FakeNoteRepository : NoteRepository {

        val notes = MutableStateFlow<List<Note>>(emptyList())

        override fun getNotes(): Flow<List<Note>> = notes

        override suspend fun createNote(title: String, content: String): Note {
            val note = Note(
                id = (notes.value.maxOfOrNull { it.id } ?: 0) + 1,
                title = title,
                content = content,
                createdAt = 1L,
                updatedAt = 1L,
            )
            notes.value = notes.value + note
            return note
        }

        override suspend fun deleteNote(id: Long) {
            notes.value = notes.value.filterNot { it.id == id }
        }
    }

    private val repository = FakeNoteRepository()
    private val getNotes = GetNotesUseCase(repository)

    @Test
    fun `emits notes sorted by updatedAt descending`() = runTest {
        repository.notes.value = listOf(
            Note(id = 1, title = "Oldest", content = "", createdAt = 0, updatedAt = 100),
            Note(id = 2, title = "Newest", content = "", createdAt = 0, updatedAt = 300),
            Note(id = 3, title = "Middle", content = "", createdAt = 0, updatedAt = 200),
        )

        val result = getNotes().first()

        assertEquals(listOf("Newest", "Middle", "Oldest"), result.map { it.title })
    }

    @Test
    fun `emits empty list when repository has no notes`() = runTest {
        val result = getNotes().first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `re-emits when the underlying data changes`() = runTest {
        assertTrue(getNotes().first().isEmpty())

        repository.createNote("New note", "content")

        assertEquals(listOf("New note"), getNotes().first().map { it.title })
    }
}
