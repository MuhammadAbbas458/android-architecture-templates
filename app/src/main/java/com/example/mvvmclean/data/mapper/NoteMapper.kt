package com.example.mvvmclean.data.mapper

import com.example.mvvmclean.data.local.NoteEntity
import com.example.mvvmclean.data.remote.NoteDto
import com.example.mvvmclean.domain.model.Note

fun NoteDto.toDomain(): Note = Note(
    id = id,
    title = title,
    content = body,
    createdAt = 0L,
    updatedAt = 0L,
)

fun NoteEntity.toDomain(): Note = Note(
    id = id,
    title = title,
    content = content,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun Note.toEntity(): NoteEntity = NoteEntity(
    id = id,
    title = title,
    content = content,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun NoteDto.toEntity(): NoteEntity = NoteEntity(
    id = id,
    title = title,
    content = body,
    createdAt = 0L,
    updatedAt = 0L,
)
