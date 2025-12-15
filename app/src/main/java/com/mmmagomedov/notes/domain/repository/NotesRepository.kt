package com.mmmagomedov.notes.domain.repository

import com.mmmagomedov.notes.domain.model.Note
import kotlinx.coroutines.flow.Flow
import java.io.File

interface NotesRepository {
    val notes: Flow<List<Note>>

    suspend fun addNote(note: Note)
    suspend fun getNoteByUid(uid: String): Note?
   // fun updateNote(note: Note)
    suspend fun removeNote(uid: String): Boolean

    suspend fun saveToFile()
    fun loadFromFile()
}