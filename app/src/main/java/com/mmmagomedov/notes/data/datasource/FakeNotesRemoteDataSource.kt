package com.mmmagomedov.notes.data.datasource

import com.mmmagomedov.notes.domain.datasource.NotesRemoteDataSource
import com.mmmagomedov.notes.domain.model.Note
import org.slf4j.LoggerFactory

class FakeNotesRemoteDataSource : NotesRemoteDataSource {
    private val log = LoggerFactory.getLogger(FakeNotesRemoteDataSource::class.java)

    override suspend fun fetchNotes(): List<Note> {
        log.info("Remote: fetchNotes()")
        return emptyList()
    }

    override suspend fun upsert(note: Note) {
        log.info("Remote: upsert uid=${note.uid}")
    }

    override suspend fun delete(uid: String) {
        log.info("Remote: delete uid=$uid")
    }
}