package com.dyentrack.app.presentation.screens.analyse

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dyentrack.app.presentation.components.MenuItemData
import com.dyentrack.app.presentation.components.ProgressLine
import com.dyentrack.app.presentation.components.RadarChartMP
import com.dyentrack.app.presentation.components.SectionSelector
import com.dyentrack.app.presentation.components.TopNavigationBar
import com.dyentrack.app.presentation.screens.analyse.components.CombinedChartMP
import com.dyentrack.app.presentation.screens.analyse.components.FilterMuscleGroupRow
import com.dyentrack.app.presentation.screens.home.RadarChartSample
import com.dyentrack.app.presentation.theme.secondaryTextColor
import com.dyentrack.app.presentation.utils.toAbbreviatedDate
import kotlin.math.roundToInt


@Composable
fun AnalysePage(modifier:Modifier = Modifier,navController: NavController,
               analyseViewModel: AnalyseViewModel = hiltViewModel<AnalyseViewModel>(),) {
    val state by analyseViewModel.uiState.collectAsState()
    var inputIndex by remember { mutableIntStateOf(0) }
    var extendFilter by remember { mutableStateOf(false) }

    Column(modifier=modifier.fillMaxWidth()) {
        TopNavigationBar(
            "Détail de l’exercice", leftIcon = Icons.AutoMirrored.Filled.ArrowBack,
            onClickLeft = { navController.popBackStack() })
        LazyColumn(modifier = Modifier            .fillMaxWidth().padding(start = 12.dp, end = 12.dp, top = 12.dp)) {
            item { SectionSelector(modifier=Modifier , selectedIndex = inputIndex,itemSection = listOf(
                MenuItemData(
                    "2 Weeks",
                    { analyseViewModel.applyDateRange(14L);inputIndex=0 }
                ),
                MenuItemData(
                    "Month",
                    { analyseViewModel.applyDateRange(30L);inputIndex=1 }
                )
                ,
                MenuItemData(
                    "Custom",
                    { null }
                )
            ) ) }
            item {
                Row(modifier= Modifier.fillMaxWidth().padding(top = 6.dp), horizontalArrangement = Arrangement.Center) {

                    Text(
                        state.dateRange.oldDate.toAbbreviatedDate() +
                                if (state.dateRange.oldDate.year != state.dateRange.recentDate.year) {
                                    " ${state.dateRange.oldDate.year} - "
                                } else {
                                    " - "
                                } +
                                state.dateRange.recentDate.toAbbreviatedDate() +
                                " ${state.dateRange.recentDate.year}",
                        style = MaterialTheme.typography.bodySmall.copy(color = secondaryTextColor)
                    ) }
            }
            item {
                Column(modifier= Modifier.padding(top=20.dp)) {
                    Row(modifier= Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Indicateur de performance", style = MaterialTheme.typography.bodyLarge)
                        IconButton(onClick = {extendFilter=!extendFilter},modifier= Modifier.size(20.dp)) {Icon(Icons.Default.FilterAlt,"Afficher les filtres",     tint = if (extendFilter) MaterialTheme.colorScheme.onSurface else secondaryTextColor) }
                    }
                        AnimatedVisibility(
                            visible = extendFilter,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column {
                                Text(
                                    "Filtre par groupe musculaire",
                                    style = MaterialTheme.typography.bodySmall.copy(color = secondaryTextColor)
                                )

                                FilterMuscleGroupRow(analyseViewModel.masterData)
                            }
                                        }
                    Column {
                        //debut composant
                        Row( horizontalArrangement = Arrangement.Absolute.SpaceBetween) {
                            Column(Modifier.padding(vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Nombre de sessions", style = MaterialTheme.typography.bodySmall.copy(color = secondaryTextColor))
                                Text(  state.mapSeance.filter { it.value.plannedExercises.isNotEmpty() }.count().toString(), style = MaterialTheme.typography.bodyLarge)

                            }

                        }
                        HorizontalDivider()
                        //fin
                        var sommeSet = 0
                        var sommeEx = 0
                        var sommeSetFail = 0
                        var sommePond = 0.0

                        state.muscleRateOfEachDay.values.forEach { innerMap ->
                            innerMap.values.forEach { mr ->
                                sommeSet += mr.numberSet
                                sommeEx += mr.numberExercise
                                sommeSetFail += mr.rateSetSomme?.numberSetFail ?: 0
                                sommePond+=mr.rateSetSomme?.sommePond?:0.0
                            }
                        }
                        Row( horizontalArrangement = Arrangement.Absolute.SpaceBetween) {
                            Column(Modifier.padding(vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Moyenne Set/ Exercices", style = MaterialTheme.typography.bodySmall.copy(color = secondaryTextColor))
                                Text(  (((sommeSet.toDouble() / sommeEx) * 100).roundToInt() / 100.0).toString(), style = MaterialTheme.typography.bodyLarge)

                            }

                        }
                        HorizontalDivider()
                        Row( horizontalArrangement = Arrangement.Absolute.SpaceBetween) {
                            Column(Modifier.padding(vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Proportion Set échec", style = MaterialTheme.typography.bodySmall.copy(color = secondaryTextColor))
                                Text(  String.format("%.2f%%", (sommeSetFail.toDouble() / sommeSet * 100)), style = MaterialTheme.typography.bodyLarge)

                            }

                        }
                        HorizontalDivider()
                        Row( horizontalArrangement = Arrangement.Absolute.SpaceBetween) {
                            Column(Modifier.padding(vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        "Somme pondéré",
                                        style = MaterialTheme.typography.bodySmall.copy(color = secondaryTextColor)
                                    )
                                    Icon(Icons.Default.Info,"voir info", tint = secondaryTextColor,modifier=Modifier.size(20.dp))
                                }

                                    Text(  sommePond.toInt().toString()+"kg", style = MaterialTheme.typography.bodyLarge)

                            }


                        }
                    }

                }
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp).padding( vertical = 8.dp)


                        .wrapContentHeight()
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = MaterialTheme.shapes.medium
                        )
                ) {
                    CombinedChartMP(
                        labels = state.dayRatio.keys.toList(),
                        barValues = state.dayRatio.values.map { it.rateSetSomme?.sommePond?.toFloat() ?: 0f },
                        lineValues = state.dayRatio.values.map {
                            val totalSet = it.numberSet
                            val failSet = it.rateSetSomme?.numberSetFail ?: 0
                            if (totalSet > 0) failSet.toFloat() / totalSet else 0f
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
            item {Text("Répartition", style = MaterialTheme.typography.bodyLarge,modifier=Modifier.padding(top = 20.dp, bottom = 8.dp))}
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(top=8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {


                    item {  Box(
                        modifier = Modifier
                            .width(300.dp)
                            .height(328.dp)


                            .wrapContentHeight()
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = MaterialTheme.shapes.medium
                            )
                    ) {
                        RadarChartSample(state.graphData, analyseViewModel.masterData)
                    }
                    }
                    item {  Box(
                        modifier = Modifier
                            .width(300.dp)
                            .height(328.dp)


                            .wrapContentHeight()
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = MaterialTheme.shapes.medium
                            )
                    ) {
                        val labels = state.muscleSetPercentage
                            .mapNotNull { (muscleId, _, _) ->
                                analyseViewModel.masterData?.muscles?.find { it.id == muscleId }?.nameFR
                            }
                        val values = state.muscleSetPercentage
                            .map { it.third.numberSet.toFloat() }
                        val values2 = state.muscleSetPercentage
                            .map { it.third.numberExercise.toFloat() }
                        RadarChartMP(modifier,labels,values2,values)
                    }
                    }
                }
            }

            item {
                FlowRow(maxItemsInEachRow = 2, modifier= Modifier.padding(vertical = 20.dp),verticalArrangement = Arrangement.spacedBy(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    state.muscleSetPercentage.forEach { (key, value) ->
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp),modifier = Modifier.fillMaxWidth(0.48f)) {
                            Row (modifier= Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                analyseViewModel.masterData?.muscles?.find { it.id == key }?.nameFR?.let {
                                    Text(
                                        it,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Text(
                                    String.format("%.2f%%", value * 100),
                                    style = MaterialTheme.typography.bodySmall.copy(color = secondaryTextColor)
                                )
                            }
                            ProgressLine(value,height=4.dp)
                        }
                        analyseViewModel.masterData?.muscles?.find { it.id == key }?.nameFR
                    }
                }
            }
        }
    }


}
