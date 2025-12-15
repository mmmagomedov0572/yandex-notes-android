package com.mmmagomedov.notes.presentation.note_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmmagomedov.notes.domain.model.Note
import com.mmmagomedov.notes.domain.repository.SyncNotesRepository
import com.mmmagomedov.notes.presentation.NoteEntity
import com.mmmagomedov.notes.presentation.toNote
import com.mmmagomedov.notes.presentation.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteEditViewModel @Inject constructor(
    private val repository: SyncNotesRepository
) : ViewModel() {

    var uiState by mutableStateOf(NoteEditUiState())
        private set

    fun load(noteId: String?) {
        if (noteId == null) {
            uiState = uiState.copy(note = NoteEntity(), canSave = false, isLoading = false)
            return
        }
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            val note = repository.getNoteByUid(noteId)
            uiState = if (note != null) {
                val entity = note.toUiState()
                uiState.copy(note = entity, canSave = isValid(entity), isLoading = false)
            } else {
                uiState.copy(note = NoteEntity(uid = noteId), canSave = false, isLoading = false)
            }
        }
    }

    fun onTitleChange(title: String) = update(uiState.note.copy(title = title))
    fun onContentChange(content: String) = update(uiState.note.copy(content = content))
    fun onColorChange(argb: Int) = update(uiState.note.copy(color = argb))
    fun onImportanceChange(importance: Note.Importance) = update(uiState.note.copy(importance = importance))

    fun onSelfDestructEnabledChange(enabled: Boolean) {
        val next = if (enabled) uiState.note.selfDestructAt ?: (System.currentTimeMillis() / 1000L) else null
        update(uiState.note.copy(selfDestructAt = next))
    }

    fun onSelfDestructDatePicked(epochSeconds: Long) {
        update(uiState.note.copy(selfDestructAt = epochSeconds))
    }

    fun onSaveClick(onDone: () -> Unit) {
        viewModelScope.launch {
            if (!uiState.canSave) return@launch
            repository.upsert(uiState.note.toNote())
            onDone()
        }
    }

    private fun update(note: NoteEntity) {
        uiState = uiState.copy(note = note, canSave = isValid(note))
    }

    private fun isValid(note: NoteEntity): Boolean =
        note.title.trim().isNotEmpty() && note.content.trim().isNotEmpty()
}
