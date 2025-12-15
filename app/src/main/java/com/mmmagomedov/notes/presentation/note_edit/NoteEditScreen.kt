package com.mmmagomedov.notes.presentation.note_edit

import android.app.DatePickerDialog
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mmmagomedov.notes.domain.model.Note
import com.mmmagomedov.notes.presentation.formattedDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Calendar

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

@Composable
private fun NoteSelfDestructSection(
    expirationDate: Long?,
    onEnabledChange: (Boolean) -> Unit,
    onPickDate: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val enabled = expirationDate != null
    val context = LocalContext.current

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = "Самоуничтожение", style = MaterialTheme.typography.titleMedium)

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Включить дату")
                Switch(checked = enabled, onCheckedChange = onEnabledChange)
            }

            if (enabled) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = expirationDate?.formattedDate() ?: "Не выбрана",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(onClick = {
                        val calendar = Calendar.getInstance()
                        DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                val localDateTime = LocalDateTime.of(year, month + 1, day, 0, 0)
                                val epochSeconds = localDateTime.toEpochSecond(ZoneOffset.UTC)
                                onPickDate(epochSeconds)
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }) {
                        Text(text = "Выбрать")
                    }
                }
            }
        }
    }
}

@Composable
private fun NoteImportanceSection(
    importance: Note.Importance,
    onChange: (Note.Importance) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = "Важность", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Note.Importance.entries.forEach { item ->
                    FilterChip(
                        selected = importance == item,
                        onClick = { onChange(item) },
                        label = { Text(text = item.getRussianName()) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NoteColorSection(
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

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
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

@Composable
private fun ColorSwatch(
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

@Composable
private fun CustomColorSwatch(
    selectedColor: Color,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val paletteBrush = remember {
        Brush.linearGradient(
            colors = listOf(
                Color.Red,
                Color.Yellow,
                Color.Green,
                Color.Cyan,
                Color.Blue,
                Color.Magenta,
                Color.Red
            )
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(paletteBrush)
            .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(8.dp))
            .combinedTapAndLongPress(onClick = onClick, onLongClick = onLongClick)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(selectedColor)
                .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(3.dp))
        )
    }
}

@Composable
private fun ColorPickerDialog(
    initialColorArgb: Int,
    onDismiss: () -> Unit,
    onColorPicked: (Int) -> Unit
) {
    var colorState by remember { mutableStateOf(ColorPickerState.fromArgb(initialColorArgb)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = { onColorPicked(colorState.toFinalArgb()) }) {
                Text(text = "Готово")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text(text = "Отмена") }
        },
        title = { Text(text = "Выбор цвета") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxWidth()) {
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
                        .height(220.dp)
                )
            }
        }
    )
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

@Composable
private fun BrightnessSlider(
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

@Composable
private fun HueBar(
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
                        Color.Red, Color.Yellow, Color.Green, Color.Cyan, Color.Blue, Color.Magenta, Color.Red
                    ),
                    tileMode = TileMode.Clamp
                )
            }
            Box(modifier = Modifier.fillMaxSize().background(gradient))
            val x = (hue / 360f) * constraints.maxWidth.toFloat()
            CanvasCrosshair(
                center = Offset(x, constraints.maxHeight / 2f),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun SaturationValuePalette(
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
        val valGradient = remember { Brush.verticalGradient(listOf(Color.Transparent, Color.Black)) }

        Box(modifier = Modifier.fillMaxSize().background(satGradient))
        Box(modifier = Modifier.fillMaxSize().background(valGradient))

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
private fun CanvasCrosshair(
    center: Offset,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val crossSize = 7.dp.toPx()
        val stroke = 2.5f
        drawLine(Color.White, Offset(center.x - crossSize, center.y), Offset(center.x + crossSize, center.y), stroke)
        drawLine(Color.White, Offset(center.x, center.y - crossSize), Offset(center.x, center.y + crossSize), stroke)
        drawCircle(Color.Black, radius = 9.dp.toPx(), center = center, style = Stroke(width = 1.5f))
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

@Stable
data class ColorPickerState(
    val hue: Float,
    val saturation: Float,
    val value: Float,
    val brightness: Float,
) {
    fun toFinalArgb(): Int {
        val hsv = floatArrayOf(hue.coerceIn(0f, 360f), saturation.coerceIn(0f, 1f), value.coerceIn(0f, 1f))
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

private fun Modifier.combinedTapAndLongPress(
    onClick: () -> Unit,
    onLongClick: () -> Unit
): Modifier {
    val interactionSource = MutableInteractionSource()
    return this.then(
        combinedClickable(
            interactionSource = interactionSource,
            indication = null,
            role = Role.Button,
            onClick = onClick,
            onLongClick = onLongClick
        )
    )
}

private suspend fun PointerInputScope.detectTap(onTap: () -> Unit) {
    detectTapGestures(onTap = { onTap() })
}
