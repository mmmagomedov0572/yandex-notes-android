package com.mmmagomedov.notes.presentation.note_edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mmmagomedov.notes.presentation.note_edit.components.ColorPickerDialog
import com.mmmagomedov.notes.presentation.note_edit.components.NoteColorSection
import com.mmmagomedov.notes.presentation.note_edit.components.NoteImportanceSection
import com.mmmagomedov.notes.presentation.note_edit.components.NoteSelfDestructSection

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NoteEditScreen(
    noteId: String?,
    title: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NoteEditViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    var showColorPickerDialog by remember { mutableStateOf(false) }

    LaunchedEffect(noteId) {
        viewModel.load(noteId)
    }

    if (showColorPickerDialog) {
        ColorPickerDialog(
            initialColorArgb = uiState.note.color,
            onDismiss = { showColorPickerDialog = false },
            onColorPicked = { argb ->
                viewModel.onColorChange(argb)
                showColorPickerDialog = false
            }
        )
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding(),
        topBar = {
            TopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = { viewModel.onSaveClick(onNavigateBack) },
                        enabled = uiState.canSave
                    ) { Text(text = "Сохранить") }
                }
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NoteBaseSection(
                title = uiState.note.title,
                content = uiState.note.content,
                onTitleChange = viewModel::onTitleChange,
                onContentChange = viewModel::onContentChange
            )

            NoteSelfDestructSection(
                expirationDate = uiState.note.selfDestructAt,
                onEnabledChange = viewModel::onSelfDestructEnabledChange,
                onPickDate = viewModel::onSelfDestructDatePicked
            )

            NoteImportanceSection(
                importance = uiState.note.importance,
                onChange = viewModel::onImportanceChange
            )

            NoteColorSection(
                selectedColorArgb = uiState.note.color,
                onColorSelected = viewModel::onColorChange,
                onOpenColorPicker = { showColorPickerDialog = true }
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun NoteBaseSection(
    title: String,
    content: String,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "Основное", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text(text = "Название") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = content,
                onValueChange = onContentChange,
                label = { Text(text = "Текст заметки") },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Default
                ),
                minLines = 5,
                maxLines = Int.MAX_VALUE,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 140.dp)
            )
        }
    }
}