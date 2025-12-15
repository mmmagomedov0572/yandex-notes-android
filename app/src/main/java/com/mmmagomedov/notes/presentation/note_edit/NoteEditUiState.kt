package com.mmmagomedov.notes.presentation.note_edit

import com.mmmagomedov.notes.presentation.NoteEntity

data class NoteEditUiState(
    var note: NoteEntity = NoteEntity(),
    val canSave: Boolean = false,
    val isLoading: Boolean = false
)
