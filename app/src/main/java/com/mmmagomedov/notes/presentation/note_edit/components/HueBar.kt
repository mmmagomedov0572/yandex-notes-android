package com.mmmagomedov.notes.presentation.note_edit.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun HueBar(
    hue: Float,
    onHueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Оттенок")
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(22.dp)
                .clip(RoundedCornerShape(10.dp))
                .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(10.dp))
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val newHue = (offset.x / size.width) * 360f
                            onHueChange(newHue.coerceIn(0f, 360f))
                        },
                        onDrag = { change, _ ->
                            val newHue = (change.position.x / size.width) * 360f
                            onHueChange(newHue.coerceIn(0f, 360f))
                        }
                    )
                }
        ) {
            val gradient = remember {
                Brush.horizontalGradient(
                    colors = listOf(
                        Color.Red,
                        Color.Yellow,
                        Color.Green,
                        Color.Cyan,
                        Color.Blue,
                        Color.Magenta,
                        Color.Red
                    ),
                    tileMode = TileMode.Clamp
                )
            }
            Box(modifier = Modifier
                .fillMaxSize()
                .background(gradient))
            val x = (hue / 360f) * constraints.maxWidth.toFloat()
            CanvasCrosshair(
                center = Offset(x, constraints.maxHeight / 2f),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun CanvasCrosshair(
    center: Offset,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val crossSize = 7.dp.toPx()
        val stroke = 2.5f
        drawLine(
            Color.White,
            Offset(center.x - crossSize, center.y),
            Offset(center.x + crossSize, center.y),
            stroke
        )
        drawLine(
            Color.White,
            Offset(center.x, center.y - crossSize),
            Offset(center.x, center.y + crossSize),
            stroke
        )
        drawCircle(Color.Black, radius = 9.dp.toPx(), center = center, style = Stroke(width = 1.5f))
    }
}
