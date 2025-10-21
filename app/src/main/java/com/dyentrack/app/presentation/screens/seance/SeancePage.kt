package com.dyentrack.app.presentation.screens.seance

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
import com.dyentrack.app.presentation.components.SubTextEmphasis
import com.dyentrack.app.presentation.components.TopNavigationBar
import com.dyentrack.app.presentation.theme.TertiaOne

@Composable

fun SeancePage(modifier: Modifier = Modifier, viewModel: SeanceViewModel = viewModel()) {

    val state by viewModel.uiState.collectAsState()

    val filteredExercises = remember(state.searchQuery, state.masterDataStatic.officialExercise) {
        state.masterDataStatic.officialExercise.filter {
            it.nameFR.contains(state.searchQuery, ignoreCase = true)
        }
    }

    val searchQueryState = remember { mutableStateOf(TextFieldValue(state.searchQuery)) }
    LaunchedEffect(Unit) {
        viewModel.loadMasterData()
    }




    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            TopNavigationBar("Séance du jour", leftIcon = Icons.AutoMirrored.Filled.ArrowBack,
                rightSide = {
                    IconButton (
                    onClick = {},
                    Modifier

                        .size(40.dp)
                ) {
                        if (state.exerciseOfTheSeance.isNotEmpty()){
                        Icon(
                            Icons.Default.Save,
                            contentDescription = "Save",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(4.dp)
                        )}
                    }
                })

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.exerciseOfTheSeance) { exerciseId ->

                    val exercise =
                        state.masterDataStatic.officialExercise.find { it.idExercise == exerciseId }
                            ?: return@items
                    val exerciseSets = state.setsMap[exerciseId] ?: listOf()
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
                                        .padding(bottom = 20.dp)
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
                                        onClick = {},
                                        Modifier
                                            .border(
                                                1.dp,
                                                color = TertiaOne,
                                                shape = CircleShape
                                            )
                                            .size(22.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.QueryStats,
                                            contentDescription = "Voir stats",
                                            tint = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(4.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = {},
                                        Modifier
                                            .border(
                                                1.dp,
                                                color = TertiaOne,
                                                shape = CircleShape
                                            )
                                            .size(22.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Info,
                                            contentDescription = "Ajouter",
                                            tint = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(4.dp)
                                        )
                                    }


                                }

                                exerciseSets.forEachIndexed { index, set ->
                                    val chargeState =
                                        remember { mutableStateOf(TextFieldValue(set.charge?.toString()?:"",
                                            TextRange(0,6)
                                        )) }
                                    val rirState =
                                        remember { mutableStateOf(TextFieldValue(set.rir?.toString()?:"",TextRange(0,3))) }
                                    val repsState =
                                        remember { mutableStateOf(TextFieldValue(set.reps?.toString()?:"",TextRange(0,3))) }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(15.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 8.dp)
                                    ) {
                                        Badge(
                                            content = {
                                                if (set.type == SetType.EFFECTIVE) Text((index + 1).toString())
                                                else Text(set.type.toString().uppercase())
                                            },
                                            modifier = Modifier
                                                .width(24.dp)
                                                .height(24.dp)
                                        )

                                        InputCustom(
                                            text = chargeState,
                                            placeholder = "Charge",
                                            modifier = Modifier.weight(1f),
                                            keyboardType = KeyboardType.Decimal
                                        )
                                        LaunchedEffect(chargeState.value.text) {
                                            set.charge =
                                                chargeState.value.text.toDoubleOrNull() ?: 0.0
                                        }

                                        InputCustom(
                                            text = rirState,
                                            placeholder = "RIR",
                                            modifier = Modifier.weight(1f),
                                            keyboardType = KeyboardType.Number
                                        )
                                        LaunchedEffect(rirState.value.text) {
                                            set.rir = rirState.value.text.toIntOrNull() ?: 0
                                        }

                                        InputCustom(
                                            text = repsState,
                                            placeholder = "Reps",
                                            modifier = Modifier.weight(1f),
                                            keyboardType = KeyboardType.Number
                                        )
                                        LaunchedEffect(repsState.value.text) {
                                            set.reps = repsState.value.text.toIntOrNull() ?: 0
                                        }
                                    }
                                }
                            }
                        }
                    )
                }

            }

        }
        FloatingActionButton(
            onClick = {    viewModel.setExerciseToAdd(state.exerciseOfTheSeance)
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
                text = searchQueryState,
                placeholder = "Search",
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                modifier = Modifier.padding(12.dp, 20.dp)
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
                Modifier.fillMaxWidth().padding(12.dp, 20.dp),
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