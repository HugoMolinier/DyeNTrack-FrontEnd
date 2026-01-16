package com.dyentrack.app.presentation.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@Composable
fun DateRangePickerDialog(
    startDate: LocalDate,
    endDate: LocalDate,
    onRangeSelected: (Pair<Long?, Long?>) -> Unit,
    onDismiss: () -> Unit
) {
    // État de sélection de plage
    val zone = java.time.ZoneId.systemDefault()
    val startMillis = startDate.atStartOfDay(zone).toInstant().toEpochMilli()
    val endMillis = endDate.atStartOfDay(zone).toInstant().toEpochMilli()

    // State stable avec plage initiale
    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = startMillis,
        initialSelectedEndDateMillis = endMillis,
        initialDisplayedMonthMillis = endMillis
    )
    DatePickerDialog(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
        ,
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                // On retourne la plage sélectionnée
                onRangeSelected(
                    Pair(
                        dateRangePickerState.selectedStartDateMillis,
                        dateRangePickerState.selectedEndDateMillis
                    )
                )
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            title = {
                Text(text = "Sélectionner une plage de dates")
            },
            showModeToggle = false,
            modifier = Modifier.padding(top=16.dp)
                .fillMaxSize()

        )
    }
}
