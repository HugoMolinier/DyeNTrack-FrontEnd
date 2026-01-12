package com.dyentrack.app.presentation.screens.seance.seanceSection.component

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Reorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dyentrack.app.R
import com.dyentrack.app.domain.model.InfoExerciseUser
import com.dyentrack.app.domain.model.MesureUnit
import com.dyentrack.app.domain.model.enum.EmphasisType
import com.dyentrack.app.domain.model.enum.ExerciseMode
import com.dyentrack.app.domain.model.exercise.ExerciseInSeance
import com.dyentrack.app.domain.model.getName
import com.dyentrack.app.domain.model.preset.Preset
import com.dyentrack.app.presentation.components.CardWithContent
import com.dyentrack.app.presentation.components.ChipDropDown
import com.dyentrack.app.presentation.components.LeftIcon
import com.dyentrack.app.presentation.components.MenuItemData
import com.dyentrack.app.presentation.screens.seance.ExpandSection
import com.dyentrack.app.presentation.screens.seance.SeanceUiState
import com.dyentrack.app.presentation.screens.seance.SeanceViewModel
import com.dyentrack.app.presentation.theme.secondaryTextColor
import com.dyentrack.app.presentation.utils.ExerciseImages
import com.dyentrack.app.presentation.utils.formatForDisplay
import com.dyentrack.app.presentation.utils.toIsoDate
import java.time.LocalDate
import kotlin.math.roundToInt


@Composable
fun ExerciseCard(
    exInSeance: ExerciseInSeance,
    state: SeanceUiState,
    viewModel: SeanceViewModel,
    navController: NavController,
    userUnit: MesureUnit,
    infoExercise: InfoExerciseUser?,
    preset: Preset?,
    dragModifier: Modifier = Modifier
) {
    val exerciseId = exInSeance.exerciseId
    val language = viewModel.languageUser
    val exercise = state.masterDataMaps?.exerciseMap?.get( exerciseId ) ?: return

    val selectedLateriteId = exInSeance.laterite
    val selectedEquipment = exInSeance.equipment

    val exerciseSets = state.seancesParJour[state.daySelected]?.setsMap[exerciseId]

    val modeActual = (state.exerciseMode )
    val textSub= exerciseSets.formatForDisplay(userUnit)
    CardWithContent(
        modifier = Modifier.padding(start = 10.dp, bottom = 4.dp, top = 4.dp),
        exercise.getName(language),
        canExtend = state.exerciseMode != ExerciseMode.CHANGE_ORDER,
        subText = textSub,
        leftIcon = LeftIcon.Single(ExerciseImages.map[exercise.idExercise]?:   R.drawable.logotransparent),
        expanded = state.expandedExerciseId == exerciseId,
        onExpandChange = { expanded ->
            viewModel.expandExercise(if (expanded) exerciseId else null)
        },

        componentRight = {
            if (modeActual.isChangeOrder()) {
                Box(
                    modifier = dragModifier
                        .fillMaxHeight()
                        .width(80.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Icon(
                        Icons.Outlined.Reorder,
                        contentDescription = "Déplacer l'exercice",
                        tint = secondaryTextColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }else {
                Icon(
                    if (state.expandedExerciseId == exInSeance.exerciseId) {
                        Icons.Outlined.KeyboardArrowUp
                    } else {
                        Icons.Outlined.KeyboardArrowDown
                    },
                    "", modifier = Modifier.size(20.dp)
                )
            }
        },
        menuOnLongClick = if (state.exerciseMode != ExerciseMode.CHANGE_ORDER) {
            buildList {
                add(
                    MenuItemData(
                        "Voir Exercice",
                        {
                            navController.navigate("exercise/${exercise.idExercise}")
                        }
                    )
                )
                if (state.exerciseMode != ExerciseMode.LOCK) {
                    add(
                        MenuItemData(
                            "Supprimer",
                            { viewModel.deleteExercice(exerciseId) }
                            , MaterialTheme.typography.bodyMedium,
                            color = Color.Red.copy(alpha = 0.8f)
                        )
                    )
                }
            }
        } else null,

        subTextEmphasis = EmphasisType.SECONDARY,
        childrenForExtend = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, bottom = 12.dp)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                        .height(32.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween

                ) {
                    Box {
                        if (!modeActual.isLock()) {
                            IconButton(
                                onClick = {
                                    viewModel.addSetToExercise(
                                        exerciseId,
                                        selectedLateriteId
                                    )
                                },
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
                                    modifier = Modifier.padding(4.dp)
                                )
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End)) {
                        ChipDropDown(
                            listOfSelectableItem = state.masterDataMaps.lateraliteMap.values.toList(),


                            defaultSelected = state.masterDataMaps.lateraliteMap[selectedLateriteId] ?: "",
                            canChange = !modeActual.isLock()

                        ) { selectedName ->
                            val lateriteId =
                                state.masterDataMaps.lateraliteMap.entries.firstOrNull { it.value == selectedName }?.key
                                    ?: 0L
                            viewModel.updateLaterite(exInSeance.exerciseId, lateriteId)
                        }
                        ChipDropDown(
                            listOfSelectableItem = exercise.equipmentInfo
                                ?.mapNotNull { idEquipment -> state.masterDataMaps?.equipmentMap?.get(idEquipment.idEquipment) }
                                ?: emptyList(),
                            defaultSelected = state.masterDataMaps.equipmentMap[selectedEquipment],
                            canChange = !modeActual.isLock()
                        ) { selectedName ->
                            val equipmentId =
                                state.masterDataMaps.equipmentMap.entries.firstOrNull { it.value == selectedName }?.key
                                    ?: 0L
                            viewModel.updateEquipment(exInSeance.exerciseId, equipmentId)
                        }

                        Icon(
                            Icons.Default.QueryStats,
                            contentDescription = "Voir stats",
                            tint = if (state.showExpand== ExpandSection.ANALYSE) MaterialTheme.colorScheme.primary else secondaryTextColor,
                            modifier = Modifier.size(20.dp).clip(CircleShape).clickable(
                                onClick = {
                                    viewModel.toggleShowAnalyseData(ExpandSection.ANALYSE)
                                    viewModel.getLastExercicePerfBetweenDate(
                                        state.daySelected,
                                        LocalDate.parse(state.daySelected).minusDays(90).toIsoDate()
                                    )
                                },
                            )
                        )


                        Icon(
                            Icons.Outlined.Info,

                            contentDescription = "Ajouter",
                            tint = if (state.showExpand== ExpandSection.COMMENT) MaterialTheme.colorScheme.primary else secondaryTextColor,
                            modifier = Modifier.size(20.dp).clip(CircleShape).clickable(
                                onClick = {
                                    viewModel.toggleShowAnalyseData(ExpandSection.COMMENT)
                                },
                            )
                        )

                    }

                }
                if(state.showExpand== ExpandSection.ANALYSE) {
                    AnalyseRow(viewModel, state, userUnit, language)
                }
                if(state.showExpand== ExpandSection.COMMENT) {
                    CommentRow(infoExercise, preset?.presetSeanceExerciseVO?.find { it.idExercise==exerciseId },    onClick={   navController.navigate("exercise/${exercise.idExercise}")})
                }


                Column {
                    if (exerciseSets?.isNotEmpty()==true){
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(15.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp, top = 4.dp)
                        ) {
                            Spacer(modifier = Modifier.width(24.dp))
                            Text("rep", modifier = Modifier.weight(1f)     .padding(start = 8.dp),style=MaterialTheme.typography.bodySmall,color=secondaryTextColor)
                            Text("poids ("+userUnit.toString().lowercase()+")", modifier = Modifier.weight(1f)     .padding(start = 8.dp),style=MaterialTheme.typography.bodySmall, color=secondaryTextColor)
                            Text("rir", modifier = Modifier.width(65.dp)     .padding(start = 8.dp),style=MaterialTheme.typography.bodySmall,color=secondaryTextColor)
                        }
                    }
                    exerciseSets?.forEachIndexed { index, set ->
                        key(set.id,set.side) {
                            var offsetY by remember { mutableFloatStateOf(0f) }
                            var isDragging by remember { mutableStateOf(false) }
                            val animOffset =
                                remember { Animatable(0f) }

                            LaunchedEffect(offsetY) {
                                animOffset.animateTo(offsetY)
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = if (isDragging) secondaryTextColor.copy(alpha = 0.2f)
                                        else MaterialTheme.colorScheme.surface,
                                        shape = MaterialTheme.shapes.medium
                                    )
                                    .offset { IntOffset(0, offsetY.roundToInt()) }
                                    .then(
                                        if (!modeActual.isLock()) {
                                            Modifier.pointerInput(Unit) {
                                                detectDragGesturesAfterLongPress(
                                                    onDragStart = { isDragging = true },
                                                    onDrag = { change, dragAmount ->
                                                        offsetY += dragAmount.y
                                                        change.consume()

                                                        val threshold = 40.dp.toPx()
                                                        val lastIndex = exerciseSets.lastIndex
                                                        val currentIndex = exerciseSets.indexOf(set)

                                                        if (offsetY > threshold && currentIndex < lastIndex) {
                                                            viewModel.swapSet(
                                                                exerciseId,
                                                                currentIndex,
                                                                currentIndex + 1
                                                            )
                                                            offsetY = 0f
                                                        } else if (offsetY < -threshold && currentIndex > 0) {
                                                            viewModel.swapSet(
                                                                exerciseId,
                                                                currentIndex,
                                                                currentIndex - 1
                                                            )
                                                            offsetY = 0f
                                                        }
                                                    },
                                                    onDragEnd = {
                                                        offsetY = 0f
                                                        isDragging = false
                                                    },
                                                    onDragCancel = {
                                                        offsetY = 0f
                                                        isDragging = false
                                                    }
                                                )
                                            }
                                        } else Modifier
                                    )
                            ) {
                                SetRow(
                                    set,
                                    index,
                                    lockInput = modeActual.isLock(),
                                    onClickBadge =
                                        { viewModel.deleteSet(exerciseId,set.id) },
                                    exerciseId = exerciseId,
                                    userUnit=userUnit

                                )
                            }
                        }
                    }
                }
            }
        }
    )
}