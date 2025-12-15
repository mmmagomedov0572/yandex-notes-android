package com.mmmagomedov.notes.domain.repository

import com.mmmagomedov.notes.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface SyncNotesRepository {
    val notes: Flow<List<Note>>

    suspend fun getNoteByUid(uid: String): Note?
    suspend fun upsert(note: Note)
    suspend fun delete(uid: String): Boolean

    suspend fun refreshFromRemote()
}