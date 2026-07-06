package com.example.mvvmclean.data.remote

import retrofit2.http.GET

/**
 * Points at JSONPlaceholder so the template works out of the box.
 * Swap the base url in NetworkModule and these endpoints for a real backend.
 */
interface NoteApiService {

    @GET("posts")
    suspend fun getNotes(): List<NoteDto>
}
