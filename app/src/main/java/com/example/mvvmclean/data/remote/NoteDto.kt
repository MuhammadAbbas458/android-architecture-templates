package com.example.mvvmclean.data.remote

import com.google.gson.annotations.SerializedName

/** Matches the JSONPlaceholder /posts response used as the demo backend. */
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
