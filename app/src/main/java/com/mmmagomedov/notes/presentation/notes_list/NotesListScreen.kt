package com.mmmagomedov.notes.presentation.notes_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mmmagomedov.notes.presentation.notes_list.components.NoteCard

@Composable
fun NotesListScreen(
    onNoteCreate: () -> Unit,
    onNoteModify: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotesListViewModel = hiltViewModel()
) {
    val notes by viewModel.notes.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNoteCreate,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 12.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить заметку"
                )
            }
        }
    ) { innerPadding ->
        if (notes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Пока нет заметок",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 12.dp,
                    end = 12.dp,
                    top = 12.dp,
                    bottom = 96.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = notes,
                    key = { it.uid }
                ) { note ->
                    NoteCard(
                        note = note,
                        onClick = { onNoteModify(note.uid) }
                    )
                }
            }
        }
    }
}