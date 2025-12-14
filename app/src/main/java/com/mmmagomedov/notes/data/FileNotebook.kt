package com.mmmagomedov.notes.data

import android.content.Context
import com.mmmagomedov.notes.domain.Note
import org.json.JSONArray
import org.slf4j.LoggerFactory
import java.io.File

class FileNotebook(
    context: Context,
    fileName: String = "notes.json"
) {
    private val log = LoggerFactory.getLogger(FileNotebook::class.java)

    private val file: File = File(context.applicationContext.filesDir, fileName)
    private val _notes = mutableListOf<Note>()
    val notes: List<Note> get() = _notes.toList()

    init {
        log.info("Init FileNotebook, file=${file.absolutePath}")
        loadFromFile(file)
    }

    fun add(note: Note) {
        val idx = _notes.indexOfFirst { it.uid == note.uid }
        if (idx >= 0) {
            _notes[idx] = note
            log.info("Updated note uid=${note.uid}, title=${note.title}")
        } else {
            _notes.add(note)
            log.info("Added note uid=${note.uid}, title=${note.title}")
        }
    }

    fun remove(uid: String): Boolean {
        val removed = _notes.removeAll { it.uid == uid }
        log.info("Remove note uid=$uid, removed=$removed")
        return removed
    }

    fun saveToFile(file: File) {
        log.info("Saving ${_notes.size} notes to file=${file.absolutePath}")

        val array = JSONArray()
        _notes.forEach { array.put(it.json) }
        file.writeText(array.toString())
    }

    fun loadFromFile(file: File = this.file) {
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
