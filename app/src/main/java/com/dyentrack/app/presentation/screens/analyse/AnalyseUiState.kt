package com.dyentrack.app.presentation.screens.analyse

import com.dyentrack.app.domain.model.MuscleRate
import com.dyentrack.app.domain.model.SeanceTrack

data class AnalyseUiState(
    val mapSeance:Map<String, SeanceTrack> = emptyMap(),
    //plusieur car evite le recalcule si on change de range
    val muscleRateOfEachDay:Map<String, Map<Long,MuscleRate>> = emptyMap(), //{day:{idMuscle:{}}
    val dayRatio:Map<String,MuscleRate> = emptyMap(),
    val graphData: Map<Long, MuscleRate> = emptyMap(),

    val muscleSetPercentage: List<Triple<Long, Float, MuscleRate>> = emptyList()


)


