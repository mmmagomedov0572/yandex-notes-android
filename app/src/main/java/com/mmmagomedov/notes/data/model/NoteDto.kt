package com.mmmagomedov.notes.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NoteDto(
    @SerialName("id") val id: String,
    @SerialName("text") val text: String,
    @SerialName("importance") val importance: String,
    @SerialName("deadline") val deadline: Long? = null,
    @SerialName("done") val isDone: Boolean,
    @SerialName("color") val color: String? = null,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("changed_at") val changedAt: Long,
    @SerialName("last_updated_by") val lastUpdatedBy: String,
)
