package com.dyentrack.app.presentation.screens.exercise

import android.widget.Toast
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dyentrack.app.R
import com.dyentrack.app.domain.model.LANGUAGE
import com.dyentrack.app.domain.model.MesureUnit
import com.dyentrack.app.domain.model.enums.VariantType
import com.dyentrack.app.domain.model.getDescription
import com.dyentrack.app.domain.model.getName
import com.dyentrack.app.presentation.components.FavoriteIconButton
import com.dyentrack.app.presentation.components.InfoRow
import com.dyentrack.app.presentation.components.InputCustom
import com.dyentrack.app.presentation.components.MenuItemData
import com.dyentrack.app.presentation.components.SectionSelector
import com.dyentrack.app.presentation.components.TopNavigationBar
import com.dyentrack.app.presentation.components.YouTubePlayerViewComposable
import com.dyentrack.app.presentation.components.chart.CombinedChartMP
import com.dyentrack.app.presentation.globalViewModel.AuthViewModel
import com.dyentrack.app.presentation.globalViewModel.InfoUserViewModel
import com.dyentrack.app.presentation.globalViewModel.UserPreferenceViewModel
import com.dyentrack.app.presentation.theme.secondaryTextColor

@Composable
fun ExercisePage(
    modifier: Modifier = Modifier, navController: NavController,
    exerciseId: Long,
    exerciseViewModel: ExerciseViewModel = hiltViewModel<ExerciseViewModel>(),
    userPreferenceViewModel: UserPreferenceViewModel = hiltViewModel<UserPreferenceViewModel>(),
    authViewModel: AuthViewModel = hiltViewModel<AuthViewModel>(),
    infoUserViewModel: InfoUserViewModel = hiltViewModel<InfoUserViewModel>(),
) {
    val context = LocalContext.current
    val textToast = stringResource(R.string.favorite_login_prompt)
    val userPref by userPreferenceViewModel.userPreference.collectAsState()
    val userUnit = userPref?.mesureUnit ?: MesureUnit.KG
    LaunchedEffect(exerciseId) {
        exerciseViewModel.setExercise(exerciseId)
    }
    val userConnected by authViewModel.authState.collectAsState()
    val idUser = userConnected.user?.id ?: 0L
    val state by exerciseViewModel.uiState.collectAsState()
    val infoMapState by infoUserViewModel.infoExerciseUserFlow.collectAsState()
    val language = exerciseViewModel.languageUser
    val currentExercise by exerciseViewModel.currentExercise.collectAsState()
    val exercise = currentExercise
    val focusManager = LocalFocusManager.current
    Box(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTapGestures(onTap = {
                focusManager.clearFocus()
            })
        }) {
        Column {
            TopNavigationBar(
                stringResource(R.string.exercise_detail),
                leftIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onClickLeft = { navController.popBackStack() })


            LazyColumn(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Box(modifier = Modifier.padding(top = 6.dp, bottom = 12.dp)) {
                        SectionSelector(
                            variant = VariantType.FILLED,
                            selectedIndex = if (state.sectionChose == SectionChose.EXPLAIN) 0 else 1,
                            itemSection = listOf(
                                MenuItemData(
                                    stringResource(R.string.explanation),
                                    { exerciseViewModel.changeSection(SectionChose.EXPLAIN) }),
                                MenuItemData(
                                    stringResource(R.string.statistics),
                                    { exerciseViewModel.changeSection(SectionChose.ANALYSE) }))


                        )
                    }
                }
                if (state.sectionChose == SectionChose.EXPLAIN && exercise != null) {
                    item {
                        Column {
                            Row(
                                modifier = modifier
                                    .fillMaxWidth()
                                    .padding(top = 0.dp, end = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(exercise.getName(language))


                                FavoriteIconButton(
                                    isFavorite = infoMapState[exercise.idExercise]?.favorite
                                        ?: false, onClick = {
                                        if (userConnected.isConnected) {
                                            val currentFavorite =
                                                infoMapState[exercise.idExercise]?.favorite ?: false
                                            infoUserViewModel.updateInfoExerciseUser(
                                                exercise.idExercise, !currentFavorite, null
                                            )
                                        } else {
                                            Toast.makeText(context, textToast, Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                    })
                            }
                            Text(
                                text = stringResource(
                                    R.string.made_by,
                                    exerciseViewModel.getExerciseCreatorName(exercise, idUser)
                                ),
                                style = MaterialTheme.typography.bodySmall.copy(color = secondaryTextColor)
                            )


                        }
                    }


                    if (exercise.getDescription(language).isNotBlank()) {
                        item {
                            Text(
                                exercise.getDescription(language),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    if (exercise.linkVideo?.isNotBlank() == true) {
                        item { YouTubePlayerViewComposable(exercise.linkVideo) }

                    }

                    if (exercise.muscleInfos?.isNotEmpty() == true) {


                        item {
                            Column(modifier = modifier.fillMaxWidth()) {
                                Text(
                                    stringResource(R.string.details),
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                if (language != LANGUAGE.EN && exercise.nameFR != exercise.nameEN) {
                                    InfoRow(
                                        label = "Aussi Appellé",
                                        value = exercise.getName(LANGUAGE.EN)
                                    )
                                }
                                InfoRow(
                                    label = stringResource(R.string.muscle_use),
                                    value = exerciseViewModel.getMuscleUsedNames(exercise)
                                        .joinToString(", "),
                                    border = true
                                )
                                if (exercise.muscleInfos.size > 1) {
                                    InfoRow(
                                        label = stringResource(R.string.primary_muscle),
                                        value = exerciseViewModel.getPrimaryMuscleName(exercise)
                                    )
                                }

                                InfoRow(
                                    label = stringResource(R.string.possible_equipment),
                                    value = exerciseViewModel.getEquipmentNames(),
                                    border = true
                                )

                                InfoRow(
                                    label = stringResource(R.string.possible_execution),
                                    value = exerciseViewModel.getLateralities()
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
                                placeholder = stringResource(R.string.add_note),
                                variant = VariantType.OUTLINED,
                                singleLine = false,
                                onFocusLost = { finalText ->
                                    infoUserViewModel.updateInfoExerciseUser(
                                        exercise.idExercise, null, finalText
                                    )
                                })


                        }

                    }
                } else {
                    item {
                        Text(
                            stringResource(R.string.performance_indicator),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .padding(vertical = 8.dp)


                                .wrapContentHeight()
                                .background(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = MaterialTheme.shapes.medium
                                )
                        ) {
                            val numberSize = state.allDataExercise.values.size
                            CombinedChartMP(
                                labels = state.allDataExercise.keys.toList(),
                                barValues = exerciseViewModel.getChartBarValues(userUnit),
                                lineValues = exerciseViewModel.getChartLineValues(),
                                showValueBar = numberSize < 35,
                                showValueLine = numberSize < 20,
                                modifier = Modifier.fillMaxWidth(),
                                barLegendLabel = stringResource(R.string.weighted_sum),
                                lineLegendLabel = stringResource(R.string.fail_set_ratio)

                            )
                        }

                    }
                    item {
                        Column {
                            InfoRow(
                                label = stringResource(R.string.average_weight_5),
                                value = exerciseViewModel.getAverageWeightFormatted(
                                    top = 5, userUnit = userUnit
                                )
                            )
                            val last14Day = exerciseViewModel.getLast14DaysDataDivided()

                            InfoRow(
                                label = stringResource(R.string.exercise_per_week),
                                border = true,
                                value = exerciseViewModel.getFrequencyPerWeek(last14Day).toString()
                            )
                            InfoRow(
                                label = stringResource(R.string.sets_per_week),
                                border = true,
                                value = exerciseViewModel.getTotalSetsPerWeek(last14Day).toString()
                            )
                            InfoRow(
                                label = stringResource(R.string.last_occurrence),
                                value = exerciseViewModel.getLastDoneDate()
                            )
                        }
                    }
                }
            }
        }
    }
}