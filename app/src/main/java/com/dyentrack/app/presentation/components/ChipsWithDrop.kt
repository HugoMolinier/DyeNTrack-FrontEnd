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
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextStyle
import com.dyentrack.app.presentation.theme.TertiaOne
import com.dyentrack.app.presentation.theme.TertiaThree

enum class ChipsEmphasys { PRIMARY, SECONDARY }
@Composable
fun ChipDropDown(
    listOfSelectableItem: List<String>,
    canChange: Boolean = true,
    emphasys : ChipsEmphasys ?= ChipsEmphasys.PRIMARY,
    defaultSelected: String?=null,
    textStyle : TextStyle = MaterialTheme.typography.bodySmall,
    onSelected: (String) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember(defaultSelected) {
        mutableStateOf(defaultSelected ?: listOfSelectableItem.first())
    }

    Box {
        FilterChip(
            selected = selectedItem.isNotEmpty(),
            onClick = { if (canChange) expanded = true },
            label = { Text(selectedItem, style = textStyle) },
            trailingIcon = {
                if (canChange) Icon(
                    if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = null
                )
            },
            shape = MaterialTheme.shapes.large,
            colors = FilterChipDefaults.filterChipColors(
                containerColor = if (emphasys == ChipsEmphasys.PRIMARY) TertiaThree else TertiaOne,
                selectedContainerColor = if (emphasys == ChipsEmphasys.PRIMARY) TertiaThree else TertiaOne,
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
            listOfSelectableItem.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        expanded = false
                        if (canChange) onSelected(item)
                    }
                )
            }

        }
    }


}
