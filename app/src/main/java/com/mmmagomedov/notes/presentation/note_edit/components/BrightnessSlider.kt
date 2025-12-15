package com.mmmagomedov.notes.presentation.note_edit.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BrightnessSlider(
    brightness: Float,
    onChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier.fillMaxWidth()) {
        Text(text = "Яркость", modifier = Modifier.padding(end = 10.dp))
        Slider(
            value = brightness,
            onValueChange = onChange,
            valueRange = 0.2f..1f,
            modifier = Modifier.weight(1f)
        )
    }
}