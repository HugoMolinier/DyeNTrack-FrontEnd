package com.dyentrack.app.presentation.screens.seance.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dyentrack.app.R
import com.dyentrack.app.domain.model.enum.ExerciseMode

@Composable
fun ModeMenu(
    currentMode: ExerciseMode,
    onToggleMode: (ExerciseMode) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(
            onClick = { expanded = true },
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                Icons.Default.Menu,
                contentDescription = stringResource(R.string.mode_menu),
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(4.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
            , modifier = Modifier.background(color = MaterialTheme.colorScheme.surface)
        ) {
            // Lock Mode
            DropdownMenuItem(
                text = { Text(stringResource(R.string.mode_lock)) },
                onClick = {
                    val newMode = if (currentMode == ExerciseMode.LOCK) ExerciseMode.NORMAL else ExerciseMode.LOCK
                    onToggleMode(newMode)
                    expanded = false
                },
                trailingIcon = {
                    if (currentMode == ExerciseMode.LOCK) Icon(Icons.Default.Check, contentDescription = null)
                }
            )

            // Change Order Mode
            DropdownMenuItem(
                text = { Text(stringResource(R.string.mode_change_order)) },
                onClick = {
                    val newMode = if (currentMode == ExerciseMode.CHANGE_ORDER) ExerciseMode.NORMAL else ExerciseMode.CHANGE_ORDER
                    onToggleMode(newMode)
                    expanded = false
                },
                trailingIcon = {
                    if (currentMode == ExerciseMode.CHANGE_ORDER) Icon(Icons.Default.Check, contentDescription = null)
                }
            )
        }
    }
}
