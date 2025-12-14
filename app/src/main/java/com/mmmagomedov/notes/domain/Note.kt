package com.mmmagomedov.notes.domain

import android.graphics.Color
import androidx.annotation.ColorInt
import java.util.UUID

data class Note(
    val uid: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    @ColorInt val color: Int = Color.WHITE,
    val importance: Importance = Importance.NORMAL,
    val selfDestructAt: Long? = null
) {
    enum class Importance { LOW, NORMAL, HIGH }
    companion object
}
