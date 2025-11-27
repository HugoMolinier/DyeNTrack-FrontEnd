package com.dyentrack.app.presentation.screens.analyse

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dyentrack.app.presentation.components.ChipDropDown
import com.dyentrack.app.presentation.components.MenuItemData
import com.dyentrack.app.presentation.components.SectionSelector
import com.dyentrack.app.presentation.components.TopNavigationBar
import com.dyentrack.app.presentation.screens.analyse.components.FilterMuscleGroupRow
import com.dyentrack.app.presentation.screens.home.HomeViewModel
import com.dyentrack.app.presentation.screens.home.RadarChartSample
import com.dyentrack.app.presentation.theme.secondaryTextColor
import kotlin.collections.forEach
import kotlin.math.roundToInt


@Composable
fun AnalysePage(modifier:Modifier = Modifier,navController: NavController,
               analyseViewModel: AnalyseViewModel = hiltViewModel<AnalyseViewModel>(),) {
    val state by analyseViewModel.uiState.collectAsState()
    Column(modifier=modifier.fillMaxWidth()) {
        TopNavigationBar(
            "Détail de l’exercice", leftIcon = Icons.AutoMirrored.Filled.ArrowBack,
            onClickLeft = { navController.popBackStack() })
        LazyColumn(modifier = Modifier            .fillMaxWidth().padding(start = 12.dp, end = 12.dp, top = 20.dp)) {
            item { SectionSelector(modifier=Modifier ,itemSection = listOf(
                MenuItemData(
                    "2 Weeks",
                    { null }
                ),
                MenuItemData(
                    "Month",
                    { null }
                )
                ,
                MenuItemData(
                    "Custom",
                    { null }
                )
            ) ) }
            item {
                Column(modifier= Modifier.padding(top=24.dp, start = 12.dp, end = 12.dp, bottom = 12.dp),) {
                    Text("Indicateur de performance", style = MaterialTheme.typography.bodyLarge,modifier= Modifier.padding(top=8.dp,bottom = 8.dp))
                    Text("Filtre par groupe musculaire", style =MaterialTheme.typography.bodySmall.copy(color = secondaryTextColor))
                    FilterMuscleGroupRow(analyseViewModel.masterData)
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
                                sommeSetFail += mr.numberSetFail ?: 0
                                sommePond+=mr.sommePond?:0.0
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

                                    Text(  sommePond.toString()+"kg", style = MaterialTheme.typography.bodyLarge)

                            }


                        }
                    }

                }
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp).padding(horizontal = 10.dp)


                        .wrapContentHeight()
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = MaterialTheme.shapes.medium
                        )
                ) {
                    Column (modifier= Modifier.padding( top = 20.dp, bottom = 8.dp)){
                        Text("Répartition", style = MaterialTheme.typography.bodyLarge)
                    }
                    RadarChartSample(state.graphData, analyseViewModel.masterData)
                }
            }
        }
    }


}
