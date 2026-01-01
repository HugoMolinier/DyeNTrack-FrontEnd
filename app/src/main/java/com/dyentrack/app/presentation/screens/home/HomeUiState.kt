package com.dyentrack.app.presentation.screens.home

import com.dyentrack.app.domain.model.DayDataRequest
import com.dyentrack.app.domain.model.MasterDataMaps
import com.dyentrack.app.domain.model.MuscleRate
import com.dyentrack.app.domain.model.SeanceTrack

data class HomeUiState(
    val mapSeance:Map<String, SeanceTrack> = emptyMap(),
    val muscleRateOfEachDay:Map<String, Map<Long, MuscleRate>> = emptyMap(), //{day:{idMuscle:{}}
    val expandedSeance: Int? = null,
    val graphData: Map<Long, MuscleRate> = emptyMap(),
    val todaySeance: DayDataRequest?=null,
    val masterDataMaps: MasterDataMaps? = MasterDataMaps(),
    val last5DaysKeys: List<String> =emptyList()
)

