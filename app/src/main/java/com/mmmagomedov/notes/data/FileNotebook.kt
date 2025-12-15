package com.mmmagomedov.notes.data

import android.content.Context
import com.mmmagomedov.notes.domain.model.Note
import com.mmmagomedov.notes.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf
import org.json.JSONArray
import org.slf4j.LoggerFactory
import java.io.File

class FileNotebook(
    context: Context,
    fileName: String = "notes.json"
) : NotesRepository {
    private val log = LoggerFactory.getLogger(FileNotebook::class.java)

    private val file: File = File(context.applicationContext.filesDir, fileName)
    private val _notes = mutableListOf<Note>()
    override val notes: Flow<List<Note>> get() = flowOf(_notes.toList())

    init {
        log.info("Init FileNotebook, file=${file.absolutePath}")
        loadFromFile()
    }

    override fun addNote(note: Note) {
        val idx = _notes.indexOfFirst { it.uid == note.uid }
        if (idx >= 0) {
            _notes[idx] = note
            log.info("Updated note uid=${note.uid}, title=${note.title}")
        } else {
            _notes.add(note)
            log.info("Added note uid=${note.uid}, title=${note.title}")
        }
    }

    override fun getNoteByUid(uid: String): Note? {
        return _notes.find { it.uid == uid }
    }

    override fun removeNote(uid: String): Boolean {
        val removed = _notes.removeAll { it.uid == uid }
        log.info("Remove note uid=$uid, removed=$removed")
        return removed
    }

    override fun saveToFile() {
        log.info("Saving ${_notes.size} notes to file=${file.absolutePath}")

        val array = JSONArray()
        _notes.forEach { array.put(it.json) }
        file.writeText(array.toString())
    }

    override fun loadFromFile() {
        if (!file.exists()) return
        val text = file.readText()
        val array = JSONArray(text)

        _notes.clear()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val note = Note.parse(obj) ?: continue
            _notes.add(note)
        }
        log.info("Loaded ${_notes.size} notes from file")
    }
}
