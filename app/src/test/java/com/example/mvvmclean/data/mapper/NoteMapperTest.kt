package com.example.mvvmclean.data.mapper

import com.example.mvvmclean.data.local.NoteEntity
import com.example.mvvmclean.data.remote.NoteDto
import com.example.mvvmclean.domain.model.Note
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Pure mapping tests — plain input/output assertions, zero mocks.
 */
class NoteMapperTest {

    @Test
    fun `NoteDto maps to domain Note`() {
        val dto = NoteDto(id = 7, title = "Remote title", body = "Remote body", userId = 3)

        val note = dto.toDomain()

        assertEquals(
            Note(id = 7, title = "Remote title", content = "Remote body", createdAt = 0, updatedAt = 0),
            note,
        )
    }

    @Test
    fun `NoteEntity maps to domain Note`() {
        val entity = NoteEntity(
            id = 42,
            title = "Local title",
            content = "Local content",
            createdAt = 1_000L,
            updatedAt = 2_000L,
        )

        val note = entity.toDomain()

        assertEquals(
            Note(id = 42, title = "Local title", content = "Local content", createdAt = 1_000L, updatedAt = 2_000L),
            note,
        )
    }

    @Test
    fun `domain Note maps to NoteEntity`() {
        val note = Note(
            id = 42,
            title = "Domain title",
            content = "Domain content",
            createdAt = 1_000L,
            updatedAt = 2_000L,
        )

        val entity = note.toEntity()

        assertEquals(
            NoteEntity(id = 42, title = "Domain title", content = "Domain content", createdAt = 1_000L, updatedAt = 2_000L),
            entity,
        )
    }

    @Test
    fun `NoteDto maps to NoteEntity for caching`() {
        val dto = NoteDto(id = 7, title = "Remote title", body = "Remote body", userId = 3)

        val entity = dto.toEntity()

        assertEquals(
            NoteEntity(id = 7, title = "Remote title", content = "Remote body", createdAt = 0, updatedAt = 0),
            entity,
        )
    }

    @Test
    fun `entity to domain and back is lossless`() {
        val entity = NoteEntity(id = 5, title = "t", content = "c", createdAt = 1, updatedAt = 2)

        assertEquals(entity, entity.toDomain().toEntity())
    }
}
