package com.mmmagomedov.notes.presentation.note_edit.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun SaturationValuePalette(
    hue: Float,
    saturation: Float,
    value: Float,
    onChange: (Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(16.dp))
            .pointerInput(hue) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val s = (offset.x / size.width).coerceIn(0f, 1f)
                        val v = (1f - (offset.y / size.height)).coerceIn(0f, 1f)
                        onChange(s, v)
                    },
                    onDrag = { change, _ ->
                        val s = (change.position.x / size.width).coerceIn(0f, 1f)
                        val v = (1f - (change.position.y / size.height)).coerceIn(0f, 1f)
                        onChange(s, v)
                    }
                )
            }
    ) {
        val base = remember(hue) { Color.hsv(hue, 1f, 1f) }
        val satGradient = remember(base) { Brush.horizontalGradient(listOf(Color.White, base)) }
        val valGradient =
            remember { Brush.verticalGradient(listOf(Color.Transparent, Color.Black)) }

        Box(modifier = Modifier
            .fillMaxSize()
            .background(satGradient))
        Box(modifier = Modifier
            .fillMaxSize()
            .background(valGradient))

        val center = Offset(
            x = saturation * constraints.maxWidth.toFloat(),
            y = (1f - value) * constraints.maxHeight.toFloat()
        )
        CanvasTarget(
            center = center,
            colorArgb = Color.hsv(hue, saturation, value).toArgb(),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun CanvasTarget(
    center: Offset,
    colorArgb: Int,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val outer = 14.dp.toPx()
        val inner = 7.dp.toPx()
        drawCircle(Color.Black, radius = outer, center = center, style = Stroke(width = 2f))
        drawCircle(Color(colorArgb), radius = inner, center = center)
    }
}