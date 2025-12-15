package com.mmmagomedov.notes.domain.datasource

import com.mmmagomedov.notes.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NotesRemoteDataSource {
    suspend fun fetchNotes(): Result<List<Note>>
    suspend fun upsert(note: Note): Result<Unit>
    suspend fun delete(uid: String): Result<Unit>
    suspend fun sync(localNotes: List<Note>): Result<List<Note>>

}