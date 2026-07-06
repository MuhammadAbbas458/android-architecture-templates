package com.example.mvvmclean.domain.model

/**
 * Domain model representing a note.
 *
 * This is the only type passed between layers. It is a plain Kotlin data
 * class with no framework annotations; the data layer maps its own entities
 * and DTOs to and from this type at the layer boundary.
 */
data class Note(
    val id: Long,
    val title: String,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long,
)
