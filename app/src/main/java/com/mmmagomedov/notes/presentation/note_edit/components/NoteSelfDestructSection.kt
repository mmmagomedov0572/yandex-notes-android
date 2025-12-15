package com.mmmagomedov.notes.presentation.note_edit.components

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mmmagomedov.notes.presentation.formattedDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Calendar

@Composable
fun NoteSelfDestructSection(
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