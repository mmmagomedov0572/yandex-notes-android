package com.mmmagomedov.notes.data.datasource

import com.mmmagomedov.notes.data.api.NotesApi
import com.mmmagomedov.notes.data.model.ElementNoteRequest
import com.mmmagomedov.notes.data.model.PatchNotesRequest
import com.mmmagomedov.notes.data.model.toDomain
import com.mmmagomedov.notes.data.model.toDto
import com.mmmagomedov.notes.domain.datasource.NotesRemoteDataSource
import com.mmmagomedov.notes.domain.datasource.TokenProvider
import com.mmmagomedov.notes.domain.model.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class BackendNotesRemoteDataSource(
    private val api: NotesApi,
    private val tokenProvider: TokenProvider,
) : NotesRemoteDataSource {
    private var currentRevision: Int = 0

    private val bearer: String
        get() = "Bearer ${tokenProvider.token}"

    override suspend fun fetchNotes(): Result<List<Note>> = safeCall {
        api.fetchNotes(auth = bearer).let { response ->
            currentRevision = response.revision
            response.list.map { it.toDomain() }
        }
    }

    override suspend fun upsert(note: Note): Result<Unit> = safeCall {
        if (noteExistsRemotely(note.uid)) {
            api.updateNote(
                auth = bearer,
                revision = currentRevision,
                noteId = note.uid,
                request = ElementNoteRequest(element = note.toDto())
            ).also { resp ->
                currentRevision = resp.revision
            }
        } else {
            api.createNote(
                auth = bearer,
                revision = currentRevision,
                request = ElementNoteRequest(element = note.toDto())
            ).also { resp ->
                currentRevision = resp.revision
            }
        }
    }

    override suspend fun delete(uid: String): Result<Unit> = safeCall {
        api.deleteNote(
            auth = bearer,
            revision = currentRevision,
            noteId = uid
        ).also { resp ->
            currentRevision = resp.revision
        }
    }

    override suspend fun sync(localNotes: List<Note>): Result<List<Note>> = safeCall {
        api.patchNotes(
            auth = bearer,
            revision = currentRevision,
            request = PatchNotesRequest(
                list = localNotes.map { it.toDto() }
            )
        ).let { resp ->
            currentRevision = resp.revision
            resp.list.map { it.toDomain() }
        }
    }

    private suspend fun noteExistsRemotely(uid: String): Boolean = try {
        val resp = api.fetchNoteById(auth = bearer, noteId = uid)
        currentRevision = resp.revision
        true
    } catch (e: HttpException) {
        e.code() != 404
    }

    private suspend fun <T> safeCall(block: suspend () -> T): Result<T> =
        withContext(Dispatchers.IO) {
            try {
                Result.success(block())
            } catch (e: HttpException) {
                Result.failure(e)
            } catch (e: IOException) {
                Result.failure(e)
            }
        }
}
