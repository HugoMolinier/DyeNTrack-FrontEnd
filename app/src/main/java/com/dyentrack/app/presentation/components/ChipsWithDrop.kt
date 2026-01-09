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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.dyentrack.app.domain.model.enum.EmphasisType
import com.dyentrack.app.presentation.theme.TertiaOne
import com.dyentrack.app.presentation.theme.TertiaThree
import com.dyentrack.app.presentation.theme.secondaryTextColor

@Composable
fun ChipDropDown(
    listOfSelectableItem: List<String>,
    canChange: Boolean = true,
    emphasys : EmphasisType ?= EmphasisType.SECONDARY,
    defaultSelected: String?=null,
    textStyle : TextStyle = MaterialTheme.typography.bodySmall,
    modifier: Modifier=Modifier,
    justOnClick: () -> Unit = {},
    onSelected: (String) -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember(defaultSelected) {
        mutableStateOf(defaultSelected ?: listOfSelectableItem.first())
    }

    Box {
        FilterChip(
            modifier = modifier,
            selected = selectedItem.isNotEmpty(),
            onClick = { if (canChange) expanded = true  else (justOnClick())},
            label = { Text(selectedItem, style = textStyle) },
            trailingIcon = {
                if (canChange) Icon(
                    if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = null
                )
            },
            shape = MaterialTheme.shapes.large,
            colors = FilterChipDefaults.filterChipColors(
                containerColor = if (emphasys == EmphasisType.PRIMARY) TertiaThree else TertiaOne,
                selectedContainerColor = if (emphasys == EmphasisType.PRIMARY) TertiaThree else TertiaOne,
                labelColor = secondaryTextColor,
                selectedLabelColor = secondaryTextColor
            ),
            border = FilterChipDefaults.filterChipBorder(
                true,
                selected = expanded,
                borderColor = secondaryTextColor.copy(alpha = 0.1f)
            )

        )

        DropdownMenu(containerColor = TertiaOne, expanded = expanded, onDismissRequest = { expanded = false }) {
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
