package com.mmmagomedov.notes.domain.datasource

import com.mmmagomedov.notes.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NotesLocalDataSource {
    val notes: Flow<List<Note>>
    suspend fun getByUid(uid: String): Note?
    suspend fun upsert(note: Note)
    suspend fun delete(uid: String): Boolean
    suspend fun writeToDisk()
    suspend fun loadFromDisk()
}

