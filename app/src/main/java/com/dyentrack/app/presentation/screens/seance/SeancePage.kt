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
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.dyentrack.app.presentation.components.TopNavigationBar
import com.dyentrack.app.presentation.screens.seance.component.BottomSheetSelectionExercise
import com.dyentrack.app.presentation.screens.seance.component.DateSelectorRange
import com.dyentrack.app.presentation.screens.seance.component.ExerciseCard
import com.dyentrack.app.presentation.screens.seance.component.ModeMenu
import com.dyentrack.app.presentation.screens.seance.component.SectionSelector
import com.dyentrack.app.presentation.screens.seance.component.ShowInfoOfSeance
import com.dyentrack.app.presentation.theme.secondaryTextColor
import com.dyentrack.app.presentation.viewModel.AppViewModel
import com.dyentrack.app.presentation.viewModel.SeanceViewModel
import kotlin.math.roundToInt

@Composable

fun SeancePage(
    modifier: Modifier = Modifier,
    viewModel: SeanceViewModel = hiltViewModel<SeanceViewModel>(),
    appViewModel: AppViewModel,
    navController: NavController
) {

    val state by viewModel.uiState.collectAsState()
    val appState by appViewModel.infoAllUser.collectAsState()


    val lateriteNameMap = appViewModel.masterData?.lateralites?.map { it.nameFR } ?: emptyList()
    val equipmentNameMap = appViewModel.masterData?.equipment?.map { it.nameFR } ?: emptyList()


    Box(modifier = modifier.fillMaxSize()) {
        Column {
            TopNavigationBar(
                "Séance du jour", leftIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onClickLeft = { navController.navigate("home") },
                rightSide = {
                    if (state.isLoadingRequest) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {


                        ModeMenu(
                            currentMode = state.exerciseMode,
                            onToggleMode = { mode ->
                                viewModel.toggleMode(mode) // la fonction dans le ViewModel gère NORMAL / LOCK / CHANGE_ORDER
                            }
                        )
                    }
                })


            LazyColumn {
                item { DateSelectorRange(uiState = state, viewModel = viewModel) }
                item { SectionSelector() }

                val exercises =
                    state.seancesParJour[state.daySelected]?.exercisesInSceance ?: listOf()

                if (exercises.isNotEmpty()) {
                    itemsIndexed(exercises, key = { _, ex -> ex.exerciseId }) { _, exInSeance ->
                        var offsetY by remember { mutableFloatStateOf(0f) }

                        Box(
                            modifier = Modifier
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


                                                    val currentIndex = exercises.indexOf(exInSeance)

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
                                appViewModel,
                                state,
                                viewModel,
                                navController,
                                lateriteNameMap,
                                equipmentNameMap
                            )
                        }
                    }
                    item { ShowInfoOfSeance(appViewModel = appViewModel) }


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
                            //image

                        }
                    }
                }

            }


        }

        if (state.exerciseMode != ExerciseMode.LOCK) {
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
    }







    BottomSheetSelectionExercise(state, appState, viewModel, appViewModel, navController)
}