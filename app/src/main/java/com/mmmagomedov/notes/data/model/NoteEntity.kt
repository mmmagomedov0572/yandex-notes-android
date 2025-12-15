package com.mmmagomedov.notes.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mmmagomedov.notes.domain.model.Note

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val uid: String,
    val title: String,
    val content: String,
    val color: Int,
    val importance: String,
    val createdAt: Long,
    val selfDestructAt: Long? = null
)

fun NoteEntity.toNote(): Note = Note(
    uid = uid,
    title = title,
    content = content,
    color = color,
    importance = when (importance.uppercase()) {
        "HIGH" -> Note.Importance.HIGH
        "LOW" -> Note.Importance.LOW
        else -> Note.Importance.NORMAL
    },
    selfDestructAt = selfDestructAt

)

fun Note.toEntity(): NoteEntity {
    val now = System.currentTimeMillis()

    return NoteEntity(
        uid = this.uid,
        title = this.title,
        content = this.content,
        color = this.color,
        importance = when (this.importance) {
            Note.Importance.HIGH -> "HIGH"
            Note.Importance.LOW -> "LOW"
            else -> "NORMAL"
        },
        createdAt = now,
        selfDestructAt = this.selfDestructAt
    )
}