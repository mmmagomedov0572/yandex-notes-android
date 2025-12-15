package com.mmmagomedov.notes.data.model

import android.graphics.Color
import android.icu.lang.UCharacter.SentenceBreak.SEP
import com.mmmagomedov.notes.domain.model.Note

fun Note.toDto(deviceId: String = "my_phone"): NoteDto {
    val now = System.currentTimeMillis()

    return NoteDto(
        id = this.uid,
        text = "$title/mmmagomedov$content",
        importance = when (this.importance) {
            Note.Importance.LOW -> "low"
            Note.Importance.NORMAL -> "basic"
            Note.Importance.HIGH -> "important"
        },
        deadline = this.selfDestructAt,
        isDone = false,
        lastUpdatedBy = deviceId,
        createdAt = now,
        changedAt = now,
        color = if (this.color != Color.WHITE) {
            String.format("#%06X", 0xFFFFFF and this.color)
        } else {
            null
        }
    )
}

fun NoteDto.toDomain(): Note {
    val titlePart = text.substringBefore("/mmmagomedov")
    val contentPart = text.substringAfter("/mmmagomedov", missingDelimiterValue = "")

    return Note(
        uid = this.id,
        title = titlePart,
        content = contentPart,
        importance = when (this.importance.lowercase()) {
            "low" -> Note.Importance.LOW
            "important" -> Note.Importance.HIGH
            else -> Note.Importance.NORMAL
        },
        selfDestructAt = this.deadline,
        color = this.color?.let {
            try {
                Color.parseColor(it)
            } catch (e: IllegalArgumentException) {
                Color.WHITE
            }
        } ?: Color.WHITE,
    )
}