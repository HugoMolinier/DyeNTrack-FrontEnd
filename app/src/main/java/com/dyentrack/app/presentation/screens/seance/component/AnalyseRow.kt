package com.dyentrack.app.presentation.screens.seance.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dyentrack.app.domain.model.SetType
import com.dyentrack.app.presentation.screens.seance.SeanceUiState
import com.dyentrack.app.presentation.theme.TertiaOne
import com.dyentrack.app.presentation.theme.addColor
import com.dyentrack.app.presentation.theme.secondaryTextColor
import com.dyentrack.app.presentation.screens.seance.SeanceViewModel


@Composable
fun AnalyseRow(viewModel: SeanceViewModel = viewModel(), state: SeanceUiState) {

    if (!state.showAnalyseData) return

    LazyRow(
        modifier = Modifier.padding(bottom = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        reverseLayout = true,
    ) {
        state.analyseData.forEach { (date, sets) ->
            if (date != state.daySelected) {
                item {
                    Box(
                        modifier = Modifier
                            .shadow(
                                elevation = 4.dp,
                                shape = RoundedCornerShape(8.dp),
                                clip = true
                            )
                            .background(TertiaOne, shape = RoundedCornerShape(8.dp))
                            .fillMaxWidth(0.8f)
                            .padding(end = 30.dp),
                    ) {


                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)


                        ) {
                            Row(
                                modifier = Modifier.padding(4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                IconButton(
                                    onClick = { viewModel.changeDaySelected(date) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowLeft,
                                        "go To the day",
                                        tint = MaterialTheme.colorScheme.primary
                                    )

                                }
                                Text(
                                    date,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }

                            FlowRow(
                                modifier = Modifier.padding(start = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                maxItemsInEachRow = 3
                            ) {
                                sets.forEachIndexed { index, set ->
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Badge(
                                            content = {
                                                if (set.type == SetType.EFFECTIVE) Text((index + 1).toString())
                                                else Text(set.type.toString().uppercase())
                                            },
                                            modifier = Modifier
                                                .width(20.dp)
                                                .height(20.dp),
                                            containerColor = when (set.type) {
                                                SetType.EFFECTIVE -> if (set.rir == 0) addColor.setEch else MaterialTheme.colorScheme.primary
                                                else -> addColor.setWarm
                                            },
                                            contentColor = when (set.type) {
                                                SetType.WARMUP -> MaterialTheme.colorScheme.onSurface
                                                else -> MaterialTheme.colorScheme.background
                                            },
                                        )
                                        Text(
                                            "${set.reps}${set.linkedRightSet?.let { "/${it.reps}" } ?: ""} x ${set.charge}kg",
                                            style = MaterialTheme.typography.bodySmall.copy(color = secondaryTextColor)
                                        )

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
