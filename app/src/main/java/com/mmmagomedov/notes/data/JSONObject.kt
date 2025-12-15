package com.mmmagomedov.notes.data

import android.graphics.Color
import com.mmmagomedov.notes.domain.model.Note
import org.json.JSONObject
import java.util.UUID

private const val KEY_UID = "uid"
private const val KEY_TITLE = "title"
private const val KEY_CONTENT = "content"
private const val KEY_COLOR = "color"
private const val KEY_IMPORTANCE = "importance"
private const val KEY_SELF_DESTRUCT_AT = "selfDestructAt"

private fun Note.Importance.toRu(): String = when (this) {
    Note.Importance.LOW -> "неважная"
    Note.Importance.NORMAL -> "обычная"
    Note.Importance.HIGH -> "важная"
}

private fun importanceFromRu(value: String): Note.Importance? = when (value) {
    "неважная" -> Note.Importance.LOW
    "обычная" -> Note.Importance.NORMAL
    "важная" -> Note.Importance.HIGH
    else -> null
}

fun Note.Companion.parse(json: JSONObject): Note? = try {
    val title = json.optString(KEY_TITLE, null) ?: return null
    val content = json.optString(KEY_CONTENT, null) ?: return null

    val uid = json.optString(KEY_UID, null) ?: UUID.randomUUID().toString()
    val color = if (json.has(KEY_COLOR)) json.getInt(KEY_COLOR) else Color.WHITE

    val importanceStr = json.optString(KEY_IMPORTANCE, "обычная")
    val importance = importanceFromRu(importanceStr) ?: Note.Importance.NORMAL

    val selfDestructAt =
        if (json.has(KEY_SELF_DESTRUCT_AT)) json.getLong(KEY_SELF_DESTRUCT_AT) else null

    Note(
        uid = uid,
        title = title,
        content = content,
        color = color,
        importance = importance,
        selfDestructAt = selfDestructAt
    )
} catch (_: Exception) {
    null
}

val Note.json: JSONObject
    get() = JSONObject().apply {
        put(KEY_UID, uid)
        put(KEY_TITLE, title)
        put(KEY_CONTENT, content)

        if (color != Color.WHITE) put(KEY_COLOR, color)
        if (importance != Note.Importance.NORMAL) put(KEY_IMPORTANCE, importance.toRu())

        if (selfDestructAt != null) put(KEY_SELF_DESTRUCT_AT, selfDestructAt)
    }
