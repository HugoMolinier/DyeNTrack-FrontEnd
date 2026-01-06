package com.dyentrack.app.presentation.mapper

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.dyentrack.app.domain.model.PresetId
import com.dyentrack.app.domain.model.dayData.DayDataRequest
import com.dyentrack.app.domain.model.dayData.NutritionTrack
import com.dyentrack.app.domain.model.dayData.PhysioTrack
import com.dyentrack.app.domain.model.SetData
import com.dyentrack.app.domain.model.SetSide
import com.dyentrack.app.domain.model.enum.SaveType
import com.dyentrack.app.domain.model.exercise.ExerciseInSeance
import com.dyentrack.app.presentation.screens.seance.SeanceDuJour


fun DayDataRequest.toSeanceDuJour(): SeanceDuJour {
    val exercises = this.seanceTrack?.plannedExercises?.sortedBy { it.exerciseOrder }?.map { plannedExercise ->
        ExerciseInSeance(
            exerciseId = plannedExercise.exerciseId,
            laterite = plannedExercise.lateraliteId,
            equipment = plannedExercise.equipmentId,

        )
    }?.toMutableStateList() ?: mutableStateListOf()

    val setsMap = mutableStateMapOf<Long, SnapshotStateList<SetData>>()
    this.seanceTrack?.plannedExercises?.forEach { plannedExercise ->
        val sets = plannedExercise.sets?.mapNotNull { setVO ->
            when (setVO.side) {
                SetSide.BOTH -> SetData(
                    type = setVO.typeOfSet,
                    charge = setVO.charge,
                    reps = setVO.repsNumber,
                    rir = setVO.rir,
                    side = SetSide.BOTH
                )

                SetSide.LEFT -> {
                    val rightSetVO =
                        plannedExercise.sets.firstOrNull { it.setOrder == setVO.setOrder && it.side == SetSide.RIGHT }
                    val leftSet = SetData(
                        type = setVO.typeOfSet,
                        charge = setVO.charge,
                        reps = setVO.repsNumber,
                        rir = setVO.rir,
                        side = SetSide.LEFT,
                        linkedRightSet = rightSetVO?.let {
                            SetData(
                                type = it.typeOfSet,
                                charge = it.charge,
                                reps = it.repsNumber,
                                rir = it.rir,
                                side = SetSide.RIGHT
                            )
                        }
                    )
                    leftSet
                }

                else -> null
            }
        }?.toMutableStateList() ?: mutableStateListOf()

        setsMap[plannedExercise.exerciseId] = sets
    }

    return SeanceDuJour(
        exercisesInSceance = exercises,
        nutritionTrack = this.nutritionTrack ?: NutritionTrack(),
        physioTrack = this.physioTrack ?: PhysioTrack(),
        setsMap = setsMap,
        saveAPI = SaveType.APISAVE,
        idPreset = PresetId(this.seanceTrack?.presetSeanceId, this.seanceTrack?.presetLocalId)
    )

}

