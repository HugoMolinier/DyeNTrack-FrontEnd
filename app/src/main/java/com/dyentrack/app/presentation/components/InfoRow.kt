package com.dyentrack.app.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dyentrack.app.presentation.theme.secondaryTextColor


@Composable
fun InfoRow(modifier:Modifier = Modifier, label: String, value: String, border: Boolean= false) {
    if (border) HorizontalDivider(color = secondaryTextColor.copy(alpha = 0.3f), thickness = 1.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label,modifier= Modifier.padding(end = 20.dp), style = MaterialTheme.typography.bodySmall.copy(color = secondaryTextColor))
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
    if (border) HorizontalDivider(color = secondaryTextColor.copy(alpha = 0.3f), thickness = 1.dp)
}
