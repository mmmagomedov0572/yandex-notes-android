package com.mmmagomedov.notes.domain.repository

import com.mmmagomedov.notes.domain.model.Note
import kotlinx.coroutines.flow.Flow
import java.io.File

interface NotesRepository {
    val notes: Flow<List<Note>>

    fun addNote(note: Note)
     fun getNoteByUid(uid: String): Note?
   // fun updateNote(note: Note)
    fun removeNote(uid: String): Boolean

    fun saveToFile()
    fun loadFromFile()
}