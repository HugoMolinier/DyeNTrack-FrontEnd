package com.dyentrack.app.presentation.mapper

import com.dyentrack.app.domain.model.dayData.DayDataRequest
import com.dyentrack.app.domain.model.dayData.PlannedExerciseVO
import com.dyentrack.app.domain.model.dayData.SeanceTrack
import com.dyentrack.app.domain.model.dayData.SetVO
import com.dyentrack.app.presentation.screens.seance.SeanceDuJour

fun SeanceDuJour.toDayDataRequest(day: String): DayDataRequest {

    val plannedExercises = this.exercisesInSceance.mapIndexed { exIndex, exercise ->
        val sets = this.setsMap[exercise.exerciseId]?.flatMapIndexed { setIndex, setData ->
            val leftSet = SetVO(
                setIndex,
                setData.reps ?: 0,
                setData.rir ?: 0,
                setData.charge ?: 0.0,
                setData.type,
                setData.side
            )
            val rightSet = setData.linkedRightSet?.let {
                SetVO(setIndex, it.reps ?: 0, it.rir ?: 0, it.charge ?: 0.0, it.type, it.side)
            }
            listOf(leftSet) + listOfNotNull(rightSet)
        } ?: emptyList()

        PlannedExerciseVO(
            null, exIndex, exercise.exerciseId, exercise.laterite, exercise.equipment, sets
        )
    }
    return DayDataRequest(
        idDayData = null,
        dayData = day,
        physioTrack = this.physioTrack,
        seanceTrack = SeanceTrack(null, idPreset?.apiId, idPreset?.localId, plannedExercises),
        nutritionTrack = this.nutritionTrack
    )
}