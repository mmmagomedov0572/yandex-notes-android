package com.mmmagomedov.notes.data

import android.content.Context
import com.mmmagomedov.notes.domain.Note
import org.json.JSONArray
import java.io.File

class FileNotebook(
    context: Context,
    fileName: String = "notes.json"
) {
    private val file: File = File(context.applicationContext.filesDir, fileName)
    private val _notes = mutableListOf<Note>()
    val notes: List<Note> get() = _notes.toList()

    init {
        loadFromFile(file)
    }

    fun add(note: Note) {
        val idx = _notes.indexOfFirst { it.uid == note.uid }
        if (idx >= 0) _notes[idx] = note else _notes.add(note)
    }

    fun remove(uid: String): Boolean =
        _notes.removeAll { it.uid == uid }

    fun saveToFile(file: File) {
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
    }
}
