package com.example.mvvmclean.data.remote

import com.google.gson.annotations.SerializedName

/**
 * Network response shape for a note.
 *
 * Matches the JSONPlaceholder `/posts` resource that this template uses as a
 * demo backend. Like [com.example.mvvmclean.data.local.NoteEntity], this type
 * never leaves the data layer.
 */
data class NoteDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("body")
    val body: String,
    @SerializedName("userId")
    val userId: Long? = null,
)
