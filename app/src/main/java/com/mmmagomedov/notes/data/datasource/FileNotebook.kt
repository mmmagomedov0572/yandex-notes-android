package com.mmmagomedov.notes.data.datasource

import android.content.Context
import com.mmmagomedov.notes.data.json
import com.mmmagomedov.notes.data.parse
import com.mmmagomedov.notes.domain.datasource.NotesLocalDataSource
import com.mmmagomedov.notes.domain.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.json.JSONArray
import org.slf4j.LoggerFactory
import java.io.File

class FileNotebook(
    context: Context,
    fileName: String = "notes.json"
) : NotesLocalDataSource {
    private val log = LoggerFactory.getLogger(FileNotebook::class.java)
    private val file: File = File(context.applicationContext.filesDir, fileName)

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    override val notes: Flow<List<Note>> = _notes.asStateFlow()

    init {
        log.info("Init local cache file=${file.absolutePath}")
    }

    override suspend fun getByUid(uid: String): Note? =
        _notes.value.firstOrNull { it.uid == uid }

    override suspend fun upsert(note: Note) {
        _notes.update { current ->
            val idx = current.indexOfFirst { it.uid == note.uid }
            if (idx >= 0) current.toMutableList().apply { this[idx] = note }.toList()
            else current + note
        }
    }

    override suspend fun delete(uid: String): Boolean {
        var removed = false
        _notes.update { current ->
            val next = current.filterNot { it.uid == uid }
            removed = next.size != current.size
            next
        }
        return removed
    }

    override suspend fun writeToDisk() {
        val snapshot = _notes.value
        val array = JSONArray()
        snapshot.forEach { array.put(it.json) }
        file.writeText(array.toString())
        log.info("Saved ${snapshot.size} notes")
    }

    override suspend fun loadFromDisk() {
        if (!file.exists()) {
            _notes.value = emptyList()
            return
        }
        val text = file.readText()
        val array = JSONArray(text)
        val loaded = buildList {
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val note = Note.Companion.parse(obj) ?: continue
                add(note)
            }
        }
        _notes.value = loaded
        log.info("Loaded ${loaded.size} notes")
    }
}