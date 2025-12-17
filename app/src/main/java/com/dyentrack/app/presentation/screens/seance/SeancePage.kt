package com.dyentrack.app.presentation.screens.seance

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dyentrack.app.presentation.components.ButtonEmphasis
import com.dyentrack.app.presentation.components.CustomButton
import com.dyentrack.app.presentation.components.MenuItemData
import com.dyentrack.app.presentation.components.SectionSelector
import com.dyentrack.app.presentation.components.TopNavigationBar
import com.dyentrack.app.presentation.components.VariantSelection
import com.dyentrack.app.presentation.components.BottomSheetSelectionExercise
import com.dyentrack.app.presentation.screens.seance.component.DateSelectorRange
import com.dyentrack.app.presentation.screens.seance.component.ModeMenu
import com.dyentrack.app.presentation.screens.seance.dataSection.DataSection
import com.dyentrack.app.presentation.screens.seance.seanceSection.component.ExerciseCard
import com.dyentrack.app.presentation.screens.seance.seanceSection.component.ShowInfoOfSeance
import com.dyentrack.app.presentation.screens.seance.viewModel.ExerciseMode
import com.dyentrack.app.presentation.screens.seance.viewModel.SeanceDuJour
import com.dyentrack.app.presentation.screens.seance.viewModel.SeanceViewModel
import com.dyentrack.app.presentation.screens.seance.viewModel.SectionSelectType
import com.dyentrack.app.presentation.theme.secondaryTextColor
import kotlin.math.roundToInt

@Composable

fun SeancePage(
    modifier: Modifier = Modifier,
    viewModel: SeanceViewModel = hiltViewModel<SeanceViewModel>(),
    navController: NavController,
    daySelected: String?=null
) {
    LaunchedEffect(Unit) {
        daySelected?.let { viewModel.setDaySelected(it) }
    }
    val state by viewModel.uiState.collectAsState()
    val infoMapUser by viewModel.infoExerciseUserFlow.collectAsState()

    val lateriteNameMap = viewModel.masterData?.lateralites?.map { it.nameFR } ?: emptyList()
    val equipmentNameMap = viewModel.masterData?.equipment?.map { it.nameFR } ?: emptyList()


    Box(modifier = modifier.fillMaxSize()) {
        Column {
            TopNavigationBar(
                "Séance du jour", leftIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onClickLeft = { navController.navigate("home") },
                rightSide = {
                    if (viewModel.dayDataFlow.collectAsState().value == null) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {


                        ModeMenu(
                            currentMode = state.exerciseMode,
                            onToggleMode = { mode ->
                                viewModel.toggleMode(mode)
                            }
                        )
                    }
                })


            LazyColumn {
                item { DateSelectorRange(uiState = state, viewModel = viewModel) }
                item {
                    Box (modifier= Modifier.padding(top = 6.dp, bottom = 20.dp)) {
                        SectionSelector(
                            variant = VariantSelection.OUTLINE,
                            itemSection = listOf(
                                MenuItemData(
                                    "Scéances",
                                    { viewModel.changeSection(SectionSelectType.SEANCE) }
                                ),
                                MenuItemData(
                                    "Data",
                                    { viewModel.changeSection(SectionSelectType.DATA) }
                                )
                            ),
                            selectedIndex = if (state.sectionSelect == SectionSelectType.SEANCE) 0 else 1


                        )
                    }
                }

                when (state.sectionSelect) {
                    SectionSelectType.SEANCE -> {
                        val exercises =
                            state.seancesParJour[state.daySelected]?.exercisesInSceance ?: listOf()

                        if (exercises.isNotEmpty()) {
                            itemsIndexed(
                                exercises,
                                key = { _, ex -> ex.exerciseId }) { _, exInSeance ->
                                var offsetY by remember { mutableFloatStateOf(0f) }

                                Box(
                                    modifier = Modifier
                                        .padding(10.dp, 4.dp)
                                        .offset { IntOffset(0, offsetY.roundToInt()) }
                                        .then(
                                            if (state.exerciseMode == ExerciseMode.CHANGE_ORDER) {
                                                Modifier.pointerInput(exInSeance) {
                                                    detectDragGestures(
                                                        onDragStart = { },
                                                        onDrag = { change, dragAmount ->
                                                            offsetY += dragAmount.y
                                                            change.consume()
                                                            val threshold = 70.dp.toPx()
                                                            val lastIndex = exercises.lastIndex


                                                            val currentIndex =
                                                                exercises.indexOf(exInSeance)

                                                            if (offsetY > threshold && currentIndex < lastIndex) {
                                                                viewModel.swapExercise(
                                                                    state.daySelected,
                                                                    currentIndex,
                                                                    currentIndex + 1
                                                                )
                                                                offsetY = 0f
                                                            } else if (offsetY < -threshold && currentIndex > 0) {
                                                                viewModel.swapExercise(
                                                                    state.daySelected,
                                                                    currentIndex,
                                                                    currentIndex - 1
                                                                )
                                                                offsetY = 0f
                                                            }
                                                        },
                                                        onDragEnd = {
                                                            offsetY = 0f

                                                        },
                                                        onDragCancel = {
                                                            offsetY = 0f

                                                        }
                                                    )
                                                }
                                            } else Modifier
                                        )
                                ) {
                                    ExerciseCard(
                                        exInSeance,
                                        state,
                                        viewModel,
                                        navController,
                                        lateriteNameMap,
                                        equipmentNameMap
                                    )
                                }
                            }
                            item { ShowInfoOfSeance(viewModel) }


                        } else {
                            item {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp, vertical = 20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        "Aucun exercice pour le moment",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        "Ajouter un exercice ou une routine",
                                        style = MaterialTheme.typography.bodyMedium.copy(color = secondaryTextColor)
                                    )

                                }
                            }
                        }
                    }

                    SectionSelectType.DATA ->
                        item {
                            DataSection(viewModel,    state.seancesParJour[state.daySelected]?: SeanceDuJour(),state.exerciseMode == ExerciseMode.LOCK,state.daySelected,state)
                        }

                    else -> null
                }

            }


        }

        if (state.exerciseMode != ExerciseMode.LOCK && state.sectionSelect == SectionSelectType.SEANCE) {
            FloatingActionButton(

                onClick = {
                    viewModel.setExerciseToAdd(
                        state.seancesParJour[state.daySelected]?.exercisesInSceance
                            ?.map { it.exerciseId }
                            ?.toList()
                            ?: listOf()
                    )
                    viewModel.toggleBottomSheet(true)
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 12.dp, bottom = 16.dp)
                    .size(58.dp),
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Ajouter",
                    tint = MaterialTheme.colorScheme.background,
                    modifier = Modifier.padding(12.dp)
                )

            }
        }
    }







    BottomSheetSelectionExercise(
        bottomSheetVisibility = state.isBottomSheetVisible,
        selectedExerciseIds = state.exerciseToAdd,
        navController = navController,
        onClose = { viewModel.toggleBottomSheet(false) },
        onClickExercise = { exerciseId ->
            viewModel.toggleExerciseSelection(exerciseId)
        },
        masterDataStatic = viewModel.masterData,
        infoMapUser = infoMapUser,
        onClickFavorite = { viewModel.updateInfoExerciseUser(it, !(infoMapUser[it]?.favorite ?: false), null) },
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CustomButton(
                label = "Annuler",
                emphasis = ButtonEmphasis.SECONDARY,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.toggleBottomSheet(false) }
            )
            CustomButton(
                label = "Ajouter",
                modifier = Modifier.weight(1f),
                onClick = { viewModel.addExerciseToSeance() }
            )
        }
    }
}