package com.mmmagomedov.notes.data.repository

import com.mmmagomedov.notes.domain.datasource.NotesLocalDataSource
import com.mmmagomedov.notes.domain.datasource.NotesRemoteDataSource
import com.mmmagomedov.notes.domain.model.Note
import com.mmmagomedov.notes.domain.repository.SyncNotesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import kotlin.math.min

class SyncNotesRepositoryImpl(
    private val local: NotesLocalDataSource,
    private val remote: NotesRemoteDataSource
) : SyncNotesRepository {

    private val log = LoggerFactory.getLogger(SyncNotesRepositoryImpl::class.java)

    override val notes: Flow<List<Note>> = local.notes

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private sealed interface Op {
        data class Upsert(val note: Note) : Op
        data class Delete(val uid: String) : Op
        data object SyncNow : Op
    }

    private val ops = Channel<Op>(capacity = Channel.UNLIMITED)

    init {
        scope.launch {
            ops.send(Op.SyncNow)
            workerLoop()
        }
    }

    override suspend fun getNoteByUid(uid: String): Note? = local.getByUid(uid)

    override suspend fun upsert(note: Note) {
        local.upsert(note)
        local.writeToDisk()

        ops.trySend(Op.Upsert(note))
            .onFailure { log.warn("Queue send failed: upsert uid=${note.uid}", it) }
    }

    override suspend fun delete(uid: String): Boolean {
        val removed = local.delete(uid)
        local.writeToDisk()

        ops.trySend(Op.Delete(uid))
            .onFailure { log.warn("Queue send failed: delete uid=$uid", it) }

        return removed
    }

    override suspend fun refreshFromRemote() {
        ops.trySend(Op.SyncNow)
    }

    private suspend fun workerLoop() {
        for (op in ops) {
            try {
                retryForever(op)
            } catch (t: Throwable) {
                log.error("Worker crashed on op=$op, skip", t)
            }
        }
    }


    private suspend fun retryForever(op: Op) {
        var attempt = 0
        while (true) {
            val result = runCatching {
                when (op) {
                    is Op.Upsert -> remote.upsert(op.note).getOrThrow()
                    is Op.Delete -> remote.delete(op.uid).getOrThrow()
                    Op.SyncNow -> syncWithServer()
                }
            }

            if (result.isSuccess) return
            val error = result.exceptionOrNull()!!

            if (shouldRetry(error)) {
                val delayMs = backoffMs(attempt++)
                log.warn("Backend retry in ${delayMs}ms, op=$op", error)
                delay(delayMs)
                continue
            }

            log.error("Non-retryable backend error op=$op body=${error.httpBody()}", error)
            return
        }
    }

    private suspend fun syncWithServer() {
        val snapshot = local.notes.first()

        if (snapshot.isEmpty()) {
            val serverOnly = remote.fetchNotes().getOrThrow()
            local.replaceAll(serverOnly)
            local.writeToDisk()
            log.info("Initial load. Server returned ${serverOnly.size} notes")
            return
        }

        val merged = remote.sync(snapshot).getOrThrow()
        local.replaceAll(merged)
        local.writeToDisk()
        log.info("Synced. Server returned ${merged.size} notes")
    }


    private fun shouldRetry(e: Throwable): Boolean = when (e) {
        is SocketTimeoutException -> true
        is IOException -> true
        is HttpException -> e.code() in 500..599 || e.code() == 429 || e.code() == 409
        else -> false
    }

    private fun backoffMs(attempt: Int): Long {
        val base = 800L * (1 shl min(attempt, 6))
        return min(base, 20_000L)
    }
}

private fun Throwable.httpBody(): String =
    (this as? HttpException)
        ?.response()
        ?.errorBody()
        ?.string()
        .orEmpty()
