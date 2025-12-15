package com.mmmagomedov.notes.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ElementNoteRequest(
    @SerialName("element") val element: NoteDto
)

@Serializable
data class PatchNotesRequest(
    @SerialName("list") val list: List<NoteDto>
)

@Serializable
data class FetchNotesResponse(
    @SerialName("status") val status: String,
    @SerialName("list") val list: List<NoteDto>,
    @SerialName("revision") val revision: Int
)

@Serializable
data class FetchNoteResponse(
    @SerialName("status") val status: String,
    @SerialName("element") val element: NoteDto,
    @SerialName("revision") val revision: Int
)

@Serializable
data class NoteResponse(
    @SerialName("status") val status: String,
    @SerialName("element") val element: NoteDto,
    @SerialName("revision") val revision: Int
)
