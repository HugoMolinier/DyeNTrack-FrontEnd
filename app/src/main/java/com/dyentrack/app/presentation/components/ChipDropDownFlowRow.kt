package com.dyentrack.app.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ChipDropDownFlowRow(muscleGroupMap: Map<Long, String> ){
    FlowRow(
        modifier = Modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        itemVerticalAlignment = Alignment.CenterVertically,

        ) {
        muscleGroupMap.forEach {
            if  (it.key>6L) return@forEach
            ChipDropDown(
                canChange = false,
                listOfSelectableItem = listOf(it.value),
                textStyle = MaterialTheme.typography.bodySmall
            ) }
    }
}