package com.dyentrack.app.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.dyentrack.app.presentation.theme.TertiaOne
import com.dyentrack.app.presentation.theme.TertiaThree

@Composable
fun ChipDropDown(
    listOfSelectableItem: List<String>,
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(listOfSelectableItem.firstOrNull().orEmpty()) }

    Box {
        FilterChip(
            selected = selectedOption.isNotEmpty(),
            onClick = { expanded = true },
            label = { Text(selectedOption) },
            trailingIcon = {
                Icon(
                    if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = null
                )
            },
            shape = MaterialTheme.shapes.large,
            colors = FilterChipDefaults.filterChipColors(
                containerColor = TertiaThree,
                selectedContainerColor = TertiaThree,
                labelColor = MaterialTheme.colorScheme.onSurface,
                selectedLabelColor = MaterialTheme.colorScheme.onSurface
            ),
            border = FilterChipDefaults.filterChipBorder(
                true,
                selected = expanded,
                borderColor = TertiaOne
            )

        )

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            for (selectableItem: String in listOfSelectableItem)
                DropdownMenuItem(
                    text = { Text(selectableItem) },
                    onClick = {
                        selectedOption = selectableItem
                        expanded = false
                    })
        }
    }


}
