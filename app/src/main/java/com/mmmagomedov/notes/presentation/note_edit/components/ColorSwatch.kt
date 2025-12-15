package com.mmmagomedov.notes.presentation.note_edit.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun ColorSwatch(
    color: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color)
            .border(
                BorderStroke(
                    width = if (selected) 2.dp else 1.dp,
                    color = Color.Black
                ),
                shape = RoundedCornerShape(8.dp)
            )
            .then(
                Modifier.pointerInput(Unit) {
                    detectTap(onTap = onClick)
                }
            )
    ) {
        if (selected) {
            Text(text = "✓", color = Color.Black)
        }
    }
}

private suspend fun PointerInputScope.detectTap(onTap: () -> Unit) {
    detectTapGestures(onTap = { onTap() })
}
