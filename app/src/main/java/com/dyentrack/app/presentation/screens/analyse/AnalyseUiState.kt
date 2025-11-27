package com.dyentrack.app.presentation.screens.analyse

import com.dyentrack.app.domain.model.MuscleRate
import com.dyentrack.app.domain.model.SeanceTrack

data class AnalyseUiState(
    val mapSeance:Map<String, SeanceTrack> = emptyMap(),
    val muscleRateOfEachDay:Map<String, Map<Long,MuscleRate>> = emptyMap(), //{day:{idMuscle:{}}
    val graphData: Map<Long, MuscleRate> = emptyMap(),

)


