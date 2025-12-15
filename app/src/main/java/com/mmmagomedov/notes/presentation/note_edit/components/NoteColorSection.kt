package com.mmmagomedov.notes.presentation.note_edit.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp

@Composable
fun NoteColorSection(
    selectedColorArgb: Int,
    onColorSelected: (Int) -> Unit,
    onOpenColorPicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selected = Color(selectedColorArgb)
    val palette = remember {
        listOf(
            Color(0xFFE53935),
            Color(0xFFFB8C00),
            Color(0xFFFFF176),
            Color(0xFF43A047),
            Color(0xFF29B6F6),
            Color(0xFF3949AB),
            Color(0xFF8E24AA),
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = "Цвет", style = MaterialTheme.typography.titleMedium)

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                palette.forEach { color ->
                    ColorSwatch(
                        color = color,
                        selected = color.toArgb() == selectedColorArgb,
                        onClick = { onColorSelected(color.toArgb()) }
                    )
                }

                CustomColorSwatch(
                    selectedColor = selected,
                    onClick = { onColorSelected(selected.toArgb()) },
                    onLongClick = onOpenColorPicker
                )
            }
        }
    }
}