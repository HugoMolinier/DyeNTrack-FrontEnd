package com.dyentrack.app.presentation.components

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.dyentrack.app.presentation.theme.TertiaOne
import com.dyentrack.app.presentation.theme.secondaryTextColor
import java.text.SimpleDateFormat
import java.util.*
import com.dyentrack.app.R


@Composable
fun DatePickerInput(birthday: MutableState<String>,enable:Boolean=true) {
    val context = LocalContext.current
    val displayFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE) }
    val serverFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE) }
    val calendar = Calendar.getInstance()
    if (birthday.value.isNotEmpty()) {
        try {
            val date: Date = serverFormat.parse(birthday.value) ?: Date()
            calendar.time = date
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val datePickerDialog = DatePickerDialog(
        context,
        R.style.DatePickerCustomDialog,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            birthday.value = serverFormat.format(calendar.time)
        },


        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)

    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = TertiaOne, shape = RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = Color(0xFF9CA3AF).copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            )
            .then(if (enable) Modifier.clickable { datePickerDialog.show() } else Modifier)
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Text(
            text = if (birthday.value.isEmpty()) "Sélectionner la date"
            else displayFormat.format(serverFormat.parse(birthday.value) ?: Date()),
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = if (enable) MaterialTheme .colorScheme.onSurface else secondaryTextColor.copy(alpha= 0.8f)
        )
    }
}
