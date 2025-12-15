package com.mmmagomedov.notes.presentation

import android.graphics.Color
import com.mmmagomedov.notes.domain.model.Note
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.UUID

data class NoteEntity(
    val uid: String = UUID.randomUUID().toString(),
    val title: String = "",
    val content: String = "",
    val color: Int = Color.WHITE,
    val importance: Note.Importance = Note.Importance.NORMAL,
    val selfDestructAt: Long? = null

)

fun NoteEntity.toNote(): Note = Note(
    uid = uid,
    title = title,
    content = content,
    color = color,
    importance = importance,
    selfDestructAt = selfDestructAt
)

fun Note.toUiState(): NoteEntity = NoteEntity(
    uid = uid,
    title = title,
    content = content,
    color = color,
    importance = importance,
    selfDestructAt = selfDestructAt
)

fun Long.formattedDate(): String {
    val dateTime = LocalDateTime.ofEpochSecond(this, 0, ZoneOffset.UTC)
    return DateTimeFormatter
        .ofPattern("dd.MM.yyyy")
        .format(dateTime)
}