package com.dyentrack.app.presentation.screens.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Reorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.room.util.TableInfo
import com.dyentrack.app.presentation.components.ButtonEmphasis
import com.dyentrack.app.presentation.components.ButtonVariant
import com.dyentrack.app.presentation.components.CardWithContent
import com.dyentrack.app.presentation.components.CustomButton
import com.dyentrack.app.presentation.components.RadarChartMP
import com.dyentrack.app.presentation.screens.seance.ExerciseMode
import com.dyentrack.app.presentation.theme.PrimaryColor
import com.dyentrack.app.presentation.theme.secondaryTextColor

@Composable
fun HomePage(
    homeViewModel: HomeViewModel = hiltViewModel<HomeViewModel>(),
    navController: NavController
) {
    val state by homeViewModel.uiState.collectAsState()
    val muscleName: Map<Long, String> = homeViewModel.masterData?.muscles
        ?.associate { muscle -> muscle.id to muscle.nameFR }
        ?: emptyMap()
    LazyColumn(modifier = Modifier.padding(top = 100.dp)) {
        item {
            Column(
                modifier = Modifier.padding(top = 32.dp, start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row {
                    Text("Scéances précédentes", Modifier.padding(bottom = 12.dp))
                }
                for (i in state.mapSeance.size - 1 downTo (state.mapSeance.size - 5).coerceAtLeast(0)) {
                    val dayKey = state.mapSeance.keys.elementAt(i)
                    val setGroupMuscu: MutableSet<String> = mutableSetOf()

                    state.muscleRateOfEachDay[dayKey]?.keys?.forEach { muscleId ->
                        val muscle = homeViewModel.masterData?.muscles?.find { it.id == muscleId }
                        val groupeMusculaire =
                            homeViewModel.masterData?.musclesGroup?.find { it.id == muscle?.idMuscularGroup }
                        groupeMusculaire?.nameFR?.let { setGroupMuscu.add(it) }
                    }
                    CardWithContent(
                        modifier = Modifier.padding(12.dp, 4.dp),
                        text = setGroupMuscu.joinToString(", "),
                        dayKey,
                        onExpandChange = { expanded ->
                            homeViewModel.expandSeance(if (expanded) i else null)
                        },
                        canExtend = true,
                        expanded = state.expandedSeance == i,

                        componentRight = {
                            Icon(
                                if (state.expandedSeance == i) {
                                    Icons.Default.ArrowDropUp
                                } else {
                                    Icons.Default.ArrowDropDown
                                }, ""
                            )
                        },
                        hasLineShadowBetweenMainAndChildren = true,
                        childrenForExtend = {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(start = 20.dp, end = 20.dp, bottom = 12.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp, bottom = 2.dp)
                                ) {
                                    Text(
                                        "Muscles",
                                        style = MaterialTheme.typography.bodySmall.copy(color = secondaryTextColor),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f)
                                    )
                                    Text(
                                        "Exercice",
                                        style = MaterialTheme.typography.bodySmall.copy(color = secondaryTextColor),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f),
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        "Série",
                                        style = MaterialTheme.typography.bodySmall.copy(color = secondaryTextColor),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f),
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.width(24.dp))
                                }

                                state.muscleRateOfEachDay[dayKey]?.entries?.forEachIndexed { index, (key, value) ->
                                    val isLast =
                                        index == state.muscleRateOfEachDay[dayKey]!!.size - 1
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(30.dp)
                                    ) {
                                        Text(
                                            muscleName[key] ?: "Not found",
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f)
                                        )
                                        Text(
                                            value.numberExercise.toString(),
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.weight(1f),
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            value.numberSet.toString(),
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f),
                                            textAlign = TextAlign.Center
                                        )
                                        if (isLast) {
                                            IconButton(
                                                onClick = {},
                                                modifier = Modifier.size(24.dp)
                                            ) { Icon(Icons.Default.Fullscreen, "go to seance") }
                                        } else {
                                            Spacer(modifier = Modifier.width(24.dp))
                                        }
                                    }
                                }


                            }

                        }
                    )


                }

            }
        }
        item {
            CustomButton(
                "Voir les séances",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 8.dp),
                variant = ButtonVariant.OUTLINED,
                emphasis = ButtonEmphasis.SECONDARY,
                onClick = { navController.navigate("seance") }
            )
        }
        item {

                Column(Modifier.padding(top = 40.dp, end = 16.dp, start = 16.dp, bottom = 16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Analyse", style = MaterialTheme.typography.titleSmall)
                        Text(
                            "Voir plus",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary)
                        )
                    }
                    Text("Cycle de "+state.mapSeance.count()+" jours", style =MaterialTheme.typography.bodyMedium.copy(color = secondaryTextColor) )



                }
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f).padding(16.dp,20.dp)
                    .aspectRatio(0.7f,true)


                    .wrapContentHeight()
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = MaterialTheme.shapes.medium
                    )
            ) {
                RadarChartSample(state, homeViewModel)
            }

        }
    }
}

@Composable
fun RadarChartSample(state: HomeUiState, homeViewModel: HomeViewModel) {

    val radarData = remember(state.graphData, homeViewModel.masterData) {
        val radarLabels = mutableListOf<String>()
        val values2 = mutableListOf<Float>()
        val values3 = mutableListOf<Float>()

        state.graphData.forEach { (muscleGroupId, stats) ->
            val groupName =
                homeViewModel.masterData?.musclesGroup?.find { it.id == muscleGroupId }?.nameFR
                    ?: "N/A"
            radarLabels.add(groupName)
            values2.add(stats.numberExercise.toFloat())
            values3.add(stats.numberSet.toFloat())
        }

        Triple(radarLabels, values2, values3)
    }

    val radarLabels = radarData.first
    if (radarLabels.size < 3) {
        Text("Pas assez de données pour le radar")
        return
    }
    RadarChartMP(
        Modifier.fillMaxSize(),
        radarLabels,
        values1 = radarData.second,
        values2 = radarData.third
    )

}
