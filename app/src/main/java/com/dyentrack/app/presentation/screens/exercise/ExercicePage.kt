package com.dyentrack.app.presentation.screens.exercise

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dyentrack.app.domain.model.MesureUnit
import com.dyentrack.app.domain.model.exercise.Exercise
import com.dyentrack.app.presentation.components.FavoriteIconButton
import com.dyentrack.app.presentation.components.InfoRow
import com.dyentrack.app.presentation.components.InputCustom
import com.dyentrack.app.presentation.components.InputVariant
import com.dyentrack.app.presentation.components.MenuItemData
import com.dyentrack.app.presentation.components.SectionSelector
import com.dyentrack.app.presentation.components.TopNavigationBar
import com.dyentrack.app.presentation.components.YouTubePlayerViewComposable
import com.dyentrack.app.presentation.components.chart.CombinedChartMP
import com.dyentrack.app.presentation.components.type.VariantType
import com.dyentrack.app.presentation.globalViewModel.UserPreferenceViewModel
import com.dyentrack.app.presentation.theme.secondaryTextColor
import com.dyentrack.app.presentation.utils.convertWeight

@Composable
fun ExercisePage(
    modifier: Modifier = Modifier, navController: NavController, exercise: Exercise,
    exerciseViewModel: ExerciseViewModel = hiltViewModel<ExerciseViewModel>(),
    userPreferenceViewModel: UserPreferenceViewModel= hiltViewModel<UserPreferenceViewModel>(),

    ) {

    val userPref by userPreferenceViewModel.userPreference.collectAsState()
    val userUnit = userPref?.mesureUnit?: MesureUnit.KG
    LaunchedEffect(exercise) {
        exerciseViewModel.setExercise(exercise)
    }
    val idUser = 1.toLong()
    val state by exerciseViewModel.uiState.collectAsState()
    val infoMapState by exerciseViewModel.infoExerciseUserFlow.collectAsState()



    val focusManager = LocalFocusManager.current
    Box(modifier = Modifier.fillMaxSize().pointerInput(Unit) {
        detectTapGestures(onTap = {
            focusManager.clearFocus()
        })}
    ) {
        Column {
            TopNavigationBar(
                "Détail de l’exercice", leftIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onClickLeft = { navController.popBackStack() })


            LazyColumn(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Box (modifier= Modifier.padding(top = 6.dp, bottom = 12.dp)) {
                        SectionSelector(
                            variant = VariantType.FILLED,
                            selectedIndex = if(state.sectionChose==SectionChose.EXPLAIN)0 else 1 ,
                            itemSection = listOf(
                                MenuItemData(
                                    "Explication",
                                    { exerciseViewModel.changeSection(SectionChose.EXPLAIN) }
                                ),
                                MenuItemData(
                                    "Statistique",
                                    {  exerciseViewModel.changeSection(SectionChose.ANALYSE) }
                                )
                            )


                        )
                    }
                }
                if(state.sectionChose==SectionChose.EXPLAIN) {
                    item {
                        Column {
                            Row(
                                modifier = modifier
                                    .fillMaxWidth()
                                    .padding(top = 0.dp, end = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(exercise.nameFR)


                                FavoriteIconButton(
                                    isFavorite = infoMapState[exercise.idExercise]?.favorite
                                        ?: false,
                                    onClick = {
                                        val currentFavorite =
                                            infoMapState[exercise.idExercise]?.favorite ?: false
                                        exerciseViewModel.updateInfoExerciseUser(
                                            exercise.idExercise,
                                            !currentFavorite,
                                            null
                                        )
                                    }
                                )
                            }
                            Text(
                                text = "Crée par " + when (exercise.idCreator) {
                                    null -> "DyeNTrack"
                                    idUser -> "Vous"
                                    else -> "un utilisateur"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(color = secondaryTextColor)
                            )


                        }
                    }


                    if (exercise.description?.isNotBlank() == true) {
                        item {
                            Text(
                                exercise.description,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    if (exercise.linkVideo?.isNotBlank() == true) {
                        item { YouTubePlayerViewComposable(exercise.linkVideo) }

                    }

                    if (exercise.muscleInfos?.isNotEmpty() == true) {
                        val muscleUse = exerciseViewModel.masterData?.muscles
                            ?.filter { it.id in exercise.muscleInfos.map { it -> it.idMuscle } }
                            ?.map { it.nameFR } ?: emptySet()

                        item {
                            Column(modifier = modifier.fillMaxWidth()) {
                                Text(
                                    "Détails",
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                InfoRow(
                                    label = "Aussi Appellé",
                                    value = "future nom anglais",
                                )
                                InfoRow(
                                    label = "Muscle utilisé",
                                    value = muscleUse.joinToString(", "),
                                    border = true
                                )
                                if(exercise.muscleInfos.size>1){
                                    InfoRow(
                                        label = "Muscle Principal",
                                        value = exercise.muscleInfos
                                            .firstOrNull { it.principal }
                                            ?.idMuscle
                                            ?.let { id -> exerciseViewModel.masterData?.muscles?.firstOrNull { it.id == id }?.nameFR }
                                            ?: "Aucun",
                                    )
                                }

                                InfoRow(
                                    label = "Equipement possible",
                                    value = exerciseViewModel.masterData?.equipment?.joinToString(", ") { it.nameFR }
                                        ?: "Aucun",
                                    border = true
                                )

                                InfoRow(
                                    label = "Exécution possible",
                                    value = exerciseViewModel.masterData?.lateralites?.joinToString(
                                        ", "
                                    ) { it.nameFR }
                                        ?: "Aucune",
                                )


                            }


                        }
                        item {
                            val noteState = remember {
                                mutableStateOf(
                                    TextFieldValue(
                                        infoMapState[exercise.idExercise]?.note ?: ""
                                    )
                                )
                            }


                            InputCustom(
                                modifier = Modifier
                                    .padding(top = 12.dp)
                                    .height(70.dp),
                                noteState,
                                placeholder = "Ajouter une note",
                                variant = InputVariant.OUTLINED,
                                singleLine = false,
                                onFocusLost = { finalText ->
                                    exerciseViewModel.updateInfoExerciseUser(
                                        exercise.idExercise,
                                        null,
                                        finalText
                                    )
                                }
                            )


                        }

                    }
                }
                else {
                    item{                 Text("Indicateur de performance", style = MaterialTheme.typography.bodyLarge)}
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp).padding(vertical = 8.dp)


                                .wrapContentHeight()
                                .background(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = MaterialTheme.shapes.medium
                                )
                        ) {
                            val numberSize= state.allDataExercise.values.size
                            CombinedChartMP(
                                labels = state.allDataExercise.keys.toList(),
                                barValues = state.allDataExercise.values.map {
                                    convertWeight(it.rateSetSomme?.sommePond, MesureUnit.KG, userUnit)?.toFloat() ?: 0f
                                },
                                lineValues = state.allDataExercise.values.map {
                                    val totalSet = it.numberSet
                                    val failSet = it.rateSetSomme?.numberSetFail ?: 0
                                    if (totalSet > 0) failSet.toFloat() / totalSet else 0f
                                },
                                showValueBar = numberSize<35,
                                showValueLine = numberSize<20,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }

                    }
                    item {
                        Column {
                            InfoRow(
                                label = "Moyenne Poids 5 occurences",
                                value = String.format(
                                    "%.1f%s",
                                    convertWeight(
                                        exerciseViewModel.getAveragePerSet(5, 0)?.toDoubleOrNull(),
                                        MesureUnit.KG,
                                        userUnit
                                    ) ?: 0.0,
                                    userUnit.toString().lowercase()
                                )
                            )
                            val last14Day = exerciseViewModel.getLast14DaysDataDivided()

                            InfoRow(
                                label = "Exercice/semaine",
                                border = true,
                                value = exerciseViewModel.getFrequencyPerWeek(last14Day).toString()
                            )
                            InfoRow(
                                label = "Set/semaine",
                                border = true,
                                value = exerciseViewModel.getTotalSetsPerWeek(last14Day).toString()
                            )
                            InfoRow(
                                label = "Dernière Occurence",
                                value = exerciseViewModel.getLastDoneDate()
                            )
                        }
                    }
                }
            }
        }
    }
}