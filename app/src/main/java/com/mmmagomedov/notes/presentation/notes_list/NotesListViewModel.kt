package com.mmmagomedov.notes.presentation.notes_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmmagomedov.notes.domain.model.Note
import com.mmmagomedov.notes.domain.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesListViewModel @Inject constructor(
    private val repository: NotesRepository
) : ViewModel() {

    val notes: StateFlow<List<Note>> = repository.notes
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun delete(uid: String) {
        viewModelScope.launch {
            repository.removeNote(uid)
            repository.saveToFile()
        }
    }

    fun refreshFromDisk() {
        viewModelScope.launch {
            repository.loadFromFile()
        }
    }
}
