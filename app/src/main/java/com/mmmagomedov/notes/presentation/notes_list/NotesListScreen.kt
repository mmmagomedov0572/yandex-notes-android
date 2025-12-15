package com.mmmagomedov.notes.presentation.notes_list

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun NotesListScreen(
    onNoteCreate: () -> Unit,
    onNoteModify: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotesListViewModel = hiltViewModel()
) {

}