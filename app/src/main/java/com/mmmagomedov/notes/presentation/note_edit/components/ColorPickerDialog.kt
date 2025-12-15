package com.mmmagomedov.notes.presentation.note_edit.components

import android.R.attr.scaleX
import android.R.attr.scaleY
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import android.graphics.Color as AndroidColor

@Composable
 fun ColorPickerDialog(
    initialColorArgb: Int,
    onDismiss: () -> Unit,
    onColorPicked: (Int) -> Unit
) {
    var colorState by remember { mutableStateOf(ColorPickerState.fromArgb(initialColorArgb)) }
    var visible by remember { mutableStateOf(false) }

    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (visible) 1f else 0.65f,
        animationSpec = androidx.compose.animation.core.spring(
            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioLowBouncy,
            stiffness = androidx.compose.animation.core.Spring.StiffnessLow
        ),
        label = "pickerScale"
    )
    val alpha by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = androidx.compose.animation.core.tween(
            durationMillis = if (visible) 140 else 110,
            easing = androidx.compose.animation.core.FastOutSlowInEasing
        ),
        label = "pickerAlpha"
    )

    LaunchedEffect(Unit) { visible = true }

    androidx.compose.ui.window.Dialog(
        onDismissRequest = { visible = false }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    this.alpha = alpha
                },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(text = "Выбор цвета", style = MaterialTheme.typography.titleLarge)

                ColorPickerPreview(colorArgb = colorState.toFinalArgb())

                BrightnessSlider(
                    brightness = colorState.brightness,
                    onChange = { colorState = colorState.copy(brightness = it) }
                )

                HueBar(
                    hue = colorState.hue,
                    onHueChange = { colorState = colorState.copy(hue = it) }
                )

                SaturationValuePalette(
                    hue = colorState.hue,
                    saturation = colorState.saturation,
                    value = colorState.value,
                    onChange = { s, v -> colorState = colorState.copy(saturation = s, value = v) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End)
                ) {
                    Button(onClick = { visible = false }) { Text(text = "Отмена") }
                    Button(onClick = {
                        onColorPicked(colorState.toFinalArgb())
                        visible = false
                    }) { Text(text = "Готово") }
                }
            }
        }
    }

    LaunchedEffect(visible) {
        if (!visible) {
            kotlinx.coroutines.delay(140)
            onDismiss()
        }
    }
}

@Composable
private fun ColorPickerPreview(
    colorArgb: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(54.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(colorArgb))
            .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(14.dp))
    )
}

@Stable
data class ColorPickerState(
    val hue: Float,
    val saturation: Float,
    val value: Float,
    val brightness: Float,
) {
    fun toFinalArgb(): Int {
        val hsv = floatArrayOf(
            hue.coerceIn(0f, 360f),
            saturation.coerceIn(0f, 1f),
            value.coerceIn(0f, 1f)
        )
        val argb = AndroidColor.HSVToColor(hsv)
        val hsv2 = FloatArray(3)
        AndroidColor.colorToHSV(argb, hsv2)
        hsv2[2] = (hsv2[2] * brightness.coerceIn(0f, 1f)).coerceIn(0f, 1f)
        return AndroidColor.HSVToColor(hsv2)
    }

    companion object {
        fun fromArgb(argb: Int): ColorPickerState {
            val hsv = FloatArray(3)
            AndroidColor.colorToHSV(argb, hsv)
            return ColorPickerState(
                hue = hsv[0],
                saturation = hsv[1],
                value = hsv[2],
                brightness = 1f
            )
        }
    }
}