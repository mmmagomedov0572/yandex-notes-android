package com.mmmagomedov.notes.data.api

import com.mmmagomedov.notes.data.model.ElementNoteRequest
import com.mmmagomedov.notes.data.model.FetchNoteResponse
import com.mmmagomedov.notes.data.model.FetchNotesResponse
import com.mmmagomedov.notes.data.model.NoteResponse
import com.mmmagomedov.notes.data.model.PatchNotesRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface NotesApi {

    @GET("list")
    suspend fun fetchNotes(
        @Header("Authorization") auth: String,
        @Header("X-Generate-Fails") generateFailsThreshold: Int? = null,
    ): FetchNotesResponse

    @GET("list/{id}")
    suspend fun fetchNoteById(
        @Header("Authorization") auth: String,
        @Path("id") noteId: String,
        @Header("X-Generate-Fails") generateFailsThreshold: Int? = null,
    ): FetchNoteResponse

    @POST("list")
    suspend fun createNote(
        @Header("Authorization") auth: String,
        @Header("X-Last-Known-Revision") revision: Int,
        @Body request: ElementNoteRequest,
        @Header("X-Generate-Fails") generateFailsThreshold: Int? = null,
    ): NoteResponse

    @PUT("list/{id}")
    suspend fun updateNote(
        @Header("Authorization") auth: String,
        @Header("X-Last-Known-Revision") revision: Int,
        @Path("id") noteId: String,
        @Body request: ElementNoteRequest,
        @Header("X-Generate-Fails") generateFailsThreshold: Int? = null,
    ): NoteResponse

    @DELETE("list/{id}")
    suspend fun deleteNote(
        @Header("Authorization") auth: String,
        @Header("X-Last-Known-Revision") revision: Int,
        @Path("id") noteId: String,
        @Header("X-Generate-Fails") generateFailsThreshold: Int? = null,
    ): NoteResponse

    @PATCH("list")
    suspend fun patchNotes(
        @Header("Authorization") auth: String,
        @Header("X-Last-Known-Revision") revision: Int,
        @Body request: PatchNotesRequest,
        @Header("X-Generate-Fails") generateFailsThreshold: Int? = null,
    ): FetchNotesResponse
}
