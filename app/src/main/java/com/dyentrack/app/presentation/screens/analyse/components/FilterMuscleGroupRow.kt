package com.dyentrack.app.presentation.screens.analyse.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dyentrack.app.domain.model.MasterDataStatic
import com.dyentrack.app.presentation.components.ChipDropDown

@Composable
fun FilterMuscleGroupRow(masterData: MasterDataStatic ?){
    FlowRow(
        modifier = Modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        itemVerticalAlignment = Alignment.CenterVertically,

    ) {
        masterData?.musclesGroup?.forEach {
            if  (it.id>6L) return@forEach
            ChipDropDown(
                canChange = false,
                listOfSelectableItem = listOf(it.nameFR),
                textStyle = MaterialTheme.typography.bodySmall
            ) }
    }
}