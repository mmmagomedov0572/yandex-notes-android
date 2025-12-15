package com.mmmagomedov.notes.data

import android.content.Context
import com.mmmagomedov.notes.domain.model.Note
import com.mmmagomedov.notes.domain.repository.NotesRepository
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
) : NotesRepository {

    private val log = LoggerFactory.getLogger(FileNotebook::class.java)
    private val file: File = File(context.applicationContext.filesDir, fileName)

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    override val notes: Flow<List<Note>> = _notes.asStateFlow()

    init {
        log.info("Init FileNotebook, file=${file.absolutePath}")
        loadFromFile()
    }

    override suspend fun addNote(note: Note) {
        _notes.update { current ->
            val idx = current.indexOfFirst { it.uid == note.uid }
            if (idx >= 0) current.toMutableList().apply { this[idx] = note }.toList()
            else current + note
        }
        log.info("Upsert note uid=${note.uid}, title=${note.title}")
    }

    override suspend fun getNoteByUid(uid: String): Note? {
        return _notes.value.firstOrNull { it.uid == uid }
    }

    override suspend fun removeNote(uid: String): Boolean {
        var removed = false
        _notes.update { current ->
            val next = current.filterNot { it.uid == uid }
            removed = next.size != current.size
            next
        }
        log.info("Remove note uid=$uid, removed=$removed")
        return removed
    }

    override suspend fun saveToFile() {
        val snapshot = _notes.value
        log.info("Saving ${snapshot.size} notes to file=${file.absolutePath}")

        val array = JSONArray()
        snapshot.forEach { array.put(it.json) }
        file.writeText(array.toString())
    }

    override fun loadFromFile() {
        if (!file.exists()) {
            _notes.value = emptyList()
            return
        }

        val text = file.readText()
        val array = JSONArray(text)
        val loaded = buildList {
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val note = Note.parse(obj) ?: continue
                add(note)
            }
        }

        _notes.value = loaded
        log.info("Loaded ${loaded.size} notes from file")
    }
}