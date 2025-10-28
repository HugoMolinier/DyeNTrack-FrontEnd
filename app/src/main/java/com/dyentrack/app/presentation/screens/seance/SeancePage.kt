package com.dyentrack.app.presentation.screens.seance

import AnalyseRow
import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Badge
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dyentrack.app.domain.model.SetType
import com.dyentrack.app.presentation.components.BottomSheetContent
import com.dyentrack.app.presentation.components.ButtonEmphasis
import com.dyentrack.app.presentation.components.CardWithContent
import com.dyentrack.app.presentation.components.ChipDropDown
import com.dyentrack.app.presentation.components.CustomButton
import com.dyentrack.app.presentation.components.InputCustom
import com.dyentrack.app.presentation.components.InputVariant
import com.dyentrack.app.presentation.components.SubTextEmphasis
import com.dyentrack.app.presentation.components.TopNavigationBar
import com.dyentrack.app.presentation.screens.seance.component.DateSelectorRange
import com.dyentrack.app.presentation.screens.seance.component.SectionSelector
import com.dyentrack.app.presentation.screens.seance.component.SetRow
import com.dyentrack.app.presentation.screens.seance.component.ShowInfoOfSeance
import com.dyentrack.app.presentation.theme.TertiaOne

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable

fun SeancePage(modifier: Modifier = Modifier, viewModel: SeanceViewModel = hiltViewModel(),navController: NavController) {

    val state by viewModel.uiState.collectAsState()

    val filteredExercises = remember(state.searchQuery, state.masterDataStatic.officialExercise) {
        state.masterDataStatic.officialExercise.filter {
            it.nameFR.contains(state.searchQuery, ignoreCase = true)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadMasterData()
    }




    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            TopNavigationBar("Séance du jour", leftIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onClickLeft = {navController.navigate("home")},
                rightSide = {
                    IconButton (
                    onClick = {viewModel.saveToAPISeance()},
                    Modifier

                        .size(40.dp)
                ) {
                        if (state.seancesParJour[state.daySelected]?.exerciseIds?.isNotEmpty() == true && state.seancesParJour[state.daySelected]?.save == false){
                        Icon(
                            Icons.Default.Save,
                            contentDescription = "Save",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(4.dp)
                        )}
                    }
                })


            LazyColumn {
                item { DateSelectorRange() }
                item { SectionSelector() }

                items(state.seancesParJour[state.daySelected]?.exerciseIds?:listOf()) { exerciseId ->

                    val exercise =
                        state.masterDataStatic.officialExercise.find { it.idExercise == exerciseId }
                            ?: return@items
                    val exerciseSets = state.seancesParJour[state.daySelected]?.setsMap[exerciseId]//viewModel.getSetOfSeance(exerciseId)
                    CardWithContent(
                        exercise.nameFR,
                        canExtend = true,
                        subText = "0",
                        expanded = state.expandedExerciseId == exerciseId, // état centralisé
                        onExpandChange = { expanded ->
                            viewModel.expandExercise(if (expanded) exerciseId else null)
                        },
                        modifier = Modifier.padding(10.dp, 4.dp),
                        iconRightForAction = if (state.expandedExerciseId == exerciseId) {
                            Icons.Default.ArrowDropUp
                        } else {
                            Icons.Default.ArrowDropDown
                        },
                        menuOnLongClick = mapOf(
                            "Voir Exercice" to {
                                navController.currentBackStackEntry?.savedStateHandle?.set("exercise", exercise)
                                navController.navigate("exercise")
                            },
                            "Supprimer" to { viewModel.deleteExercice(exerciseId) }
                        ),
                        subTextEmphasis = SubTextEmphasis.SECONDARY,

                        childrenForExtend = {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(start = 20.dp, end = 20.dp, bottom = 12.dp)
                            ) {
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp)
                                        .height(32.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically

                                ) {
                                    IconButton(
                                        onClick = { viewModel.addSetToExercise(exerciseId) },
                                        Modifier
                                            .border(
                                                1.dp,
                                                color = MaterialTheme.colorScheme.primary,
                                                shape = CircleShape
                                            )
                                            .height(24.dp)
                                            .width(24.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Add,
                                            contentDescription = "Ajouter",

                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(4.dp) // espace autour de l'icône
                                        )
                                    }
                                    ChipDropDown(
                                        listOfSelectableItem = state.masterDataStatic.lateralites.map { it.nameFR }
                                    )
                                    ChipDropDown(
                                        listOfSelectableItem = state.masterDataStatic.equipment.map { it.nameFR }
                                    )
                                    IconButton(
                                        onClick = {
                                            viewModel.toggleShowAnalyseData()
                                            viewModel.getLastExercicePerfBetweenDate(state.daySelected,"2025-10-20")},
                                        Modifier
                                            .border(
                                                1.dp,
                                                color = if (state.showAnalyseData) MaterialTheme.colorScheme.primary else TertiaOne,
                                                shape = CircleShape
                                            )

                                            .size(22.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.QueryStats,
                                            contentDescription = "Voir stats",
                                            tint = if (state.showAnalyseData) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(4.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = {



                                                  },
                                        Modifier
                                            .border(
                                                1.dp,
                                                color = TertiaOne,
                                                shape = CircleShape
                                            )
                                            .size(30.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Info,
                                            contentDescription = "Ajouter",
                                            tint = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(4.dp)
                                        )
                                    }


                                }
                                AnalyseRow()

                                exerciseSets?.forEachIndexed { index, set ->
                                    SetRow(set,index)
                                }
                            }
                        }
                    )
                }
                item { ShowInfoOfSeance() }

            }




        }
        FloatingActionButton(
            onClick = {    viewModel.setExerciseToAdd(state.seancesParJour[state.daySelected]?.exerciseIds?:listOf())
            viewModel.toggleBottomSheet(true)},
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 16.dp),
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Ajouter",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(12.dp) // espace autour de l'icône
            )

        }
        }







    BottomSheetContent(
        title = "Ajouter un exercice",
        showBottomSheet = state.isBottomSheetVisible,
        onDismiss = { viewModel.toggleBottomSheet(false) },
        childrenForExtend = {
            InputCustom(
                text = remember { mutableStateOf(TextFieldValue(state.searchQuery)) },
                onValueChange = { newValue ->
                    viewModel.updateSearchQuery(newValue)
                },
                placeholder = "Search",
                leadingIcon = Icons.Default.Search,
                modifier = Modifier.padding(12.dp, 20.dp),
                variant = InputVariant.OUTLINED
            )

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filteredExercises) { exercise ->
                    val isSelected = exercise.idExercise in state.exerciseToAdd
                    CardWithContent(
                        exercise.nameFR,
                        subText = state.masterDataStatic.musclesGroup
                            .firstOrNull { it.id == exercise.mainFocusGroup }?.nameFR?.uppercase() ?: "NaN",
                        modifier = Modifier.padding(12.dp, 4.dp),
                        iconRightForAction = Icons.Default.StarBorder,
                        subTextEmphasis = SubTextEmphasis.PRIMARY,
                        onClick = { viewModel.toggleExerciseSelection(exercise.idExercise) },
                        badgeText = if (isSelected) (state.exerciseToAdd.indexOf(exercise.idExercise) + 1).toString() else null
                    )
                }
            }
        },
        footerComponent = {
            Row(
                Modifier.fillMaxWidth().padding(start = 12.dp, end = 12.dp, top = 20.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CustomButton(
                    label = "Annuler",
                    emphasis = ButtonEmphasis.SECONDARY,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.toggleBottomSheet(false)
                    }
                )
                CustomButton(
                    label = "Ajouter",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.addExerciseToSeance()
                    }
                )
            }

        }
    )
}