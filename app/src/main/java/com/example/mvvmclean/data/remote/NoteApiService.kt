package com.example.mvvmclean.data.remote

import retrofit2.http.GET

/**
 * Retrofit definition of the remote notes API.
 *
 * Points at JSONPlaceholder (`https://jsonplaceholder.typicode.com/`) so the
 * template works out of the box; swap the base URL and endpoints for your
 * real backend.
 */
interface NoteApiService {

    @GET("posts")
    suspend fun getNotes(): List<NoteDto>
}
