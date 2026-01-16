package com.dyentrack.app.presentation.components.bottomSheet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dyentrack.app.R
import com.dyentrack.app.domain.model.preset.Preset
import com.dyentrack.app.presentation.components.ChipDropDown
import com.dyentrack.app.presentation.theme.secondaryTextColor

@Composable
fun BottomSheetPreset(
    bottomSheetVisibility: Boolean,
    presetList: List<Preset>?=emptyList(),
    onNew: () -> Unit= {},
    onClose: () -> Unit= {},
    onClickPreset: (Preset) -> Unit= {},
    title:String="",
    titleAction: (@Composable () -> Unit)? = null,
    canAddPreset:Boolean=true

    ) {


    BottomSheetContent(
        title = title,
        titleAction = titleAction,
        showBottomSheet = bottomSheetVisibility,
        onDismiss = { onClose() },
        isfullScreen = false,
        childrenForExtend = {
            Column(
                modifier = Modifier.padding(
                    top = 12.dp,
                    start = 12.dp,
                    end = 12.dp,
                    bottom = 20.dp
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.Absolute.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ChipDropDown(listOf(        stringResource(R.string.sort_by_name),
                        stringResource(R.string.sort_by_creation_date),
                        stringResource(R.string.sort_by_last_occurrence)))
                    Icon(Icons.Outlined.FilterList, "Croissant")
                }


                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    maxItemsInEachRow = 4,
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {

                    presetList?.forEach { preset ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.clickable {
                                onClickPreset(preset)
                            }
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(MaterialTheme.shapes.extraLarge)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = preset.name.first().uppercase(),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }

                            Text(
                                text = preset.name,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1
                            )
                        }
                    }

                    // Bouton Ajouter
                    if (canAddPreset) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.clickable {
                                onNew()
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(MaterialTheme.shapes.extraLarge)
                                    .border(
                                        width = 1.dp,
                                        color = secondaryTextColor,
                                        shape = MaterialTheme.shapes.extraLarge
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = stringResource(R.string.add),
                                    tint = secondaryTextColor
                                )
                            }

                            Text(
                                text = stringResource(R.string.add),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

            }
        }
    )
}