package com.mmmagomedov.notes.data.repository

import com.mmmagomedov.notes.domain.datasource.NotesLocalDataSource
import com.mmmagomedov.notes.domain.datasource.NotesRemoteDataSource
import com.mmmagomedov.notes.domain.model.Note
import com.mmmagomedov.notes.domain.repository.SyncNotesRepository
import kotlinx.coroutines.flow.Flow
import org.slf4j.LoggerFactory

class SyncNotesRepositoryImpl(
    private val local: NotesLocalDataSource,
    private val remote: NotesRemoteDataSource
) : SyncNotesRepository {

    private val log = LoggerFactory.getLogger(SyncNotesRepositoryImpl::class.java)

    override val notes: Flow<List<Note>> = local.notes

    override suspend fun getNoteByUid(uid: String): Note? = local.getByUid(uid)

    override suspend fun upsert(note: Note) {
        local.upsert(note)
        local.writeToDisk()
        remote.upsert(note)
    }

    override suspend fun delete(uid: String): Boolean {
        val removed = local.delete(uid)
        local.writeToDisk()
        remote.delete(uid)
        return removed
    }

    override suspend fun refreshFromRemote() {
        val remoteNotes = remote.fetchNotes()
        remoteNotes.forEach { local.upsert(it) }
        local.writeToDisk()
        log.info("RefreshFromRemote: cached ${remoteNotes.size} notes")
    }
}
