package com.mmmagomedov.notes.data.datasource

import com.mmmagomedov.notes.data.model.toNote
import com.mmmagomedov.notes.data.model.toEntity
import com.mmmagomedov.notes.data.room.NoteDao
import com.mmmagomedov.notes.domain.datasource.NotesLocalDataSource
import com.mmmagomedov.notes.domain.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalRoomDataSource(
    private val noteDao: NoteDao
) : NotesLocalDataSource {

    override val notes: Flow<List<Note>> =
        noteDao.getNotesFlow().map { entities -> entities.map { it.toNote() } }

    override suspend fun getByUid(uid: String): Note? =
        noteDao.getNoteByUid(uid)?.toNote()

    override suspend fun upsert(note: Note) {
        noteDao.insertNote(note.toEntity())
    }

    override suspend fun delete(uid: String): Boolean {
        return noteDao.deleteByUid(uid) > 0
    }

    override suspend fun replaceAll(notes: List<Note>) {
        noteDao.deleteAll()
        if (notes.isNotEmpty()) {
            noteDao.insertNotes(notes.map { it.toEntity() })
        }
    }

    override suspend fun writeToDisk() {}

    override suspend fun loadFromDisk() {}
}
