package com.mmmagomedov.notes.domain.datasource

import com.mmmagomedov.notes.domain.model.Note

interface NotesRemoteDataSource {
    suspend fun fetchNotes(): List<Note>
    suspend fun upsert(note: Note)
    suspend fun delete(uid: String)
}