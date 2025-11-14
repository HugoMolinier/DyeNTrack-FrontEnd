package com.dyentrack.app.presentation.screens.home

import com.dyentrack.app.domain.model.DayDataRequest
import com.dyentrack.app.domain.model.SeanceTrack

data class HomeUiState(
    val mapSeance:Map<String, SeanceTrack> = emptyMap(),
    val muscleRateOfEachDay:Map<String, Map<Long,MuscleRate>> = emptyMap(), //{day:{idMuscle:{}}
    val expandedSeance: Int? = null,
    val graphData: Map<Long, MuscleRate> = emptyMap(),
    val todaySeance: DayDataRequest?=null

)

data class MuscleRate(

    val numberExercise: Int,
    val numberSet: Int
)
