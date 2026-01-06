package com.dyentrack.app.presentation.screens.home

import com.dyentrack.app.domain.model.dayData.DayDataRequest
import com.dyentrack.app.domain.model.MasterDataMaps
import com.dyentrack.app.domain.model.MuscleRate
import com.dyentrack.app.domain.model.dayData.SeanceTrack
import com.dyentrack.app.domain.model.enum.BottomSheetVisibleType

data class HomeUiState(
    val mapSeance:Map<String, SeanceTrack> = emptyMap(),
    val muscleRateOfEachDay:Map<String, Map<Long, MuscleRate>> = emptyMap(), //{day:{idMuscle:{}}
    val expandedSeance: Int? = null,
    val isBottomSheetVisible: BottomSheetVisibleType =BottomSheetVisibleType.NO,
    val graphData: Map<Long, MuscleRate> = emptyMap(),
    val todaySeance: DayDataRequest?=null,
    val masterDataMaps: MasterDataMaps? = MasterDataMaps(),
    val last5DaysKeys: List<String> =emptyList()
)

