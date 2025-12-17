package com.dyentrack.app.presentation.screens.analyse.viewModel

import com.dyentrack.app.domain.model.MuscleRate
import com.dyentrack.app.domain.model.SeanceTrack
import java.time.LocalDate

data class AnalyseUiState(
    val mapSeance:Map<String, SeanceTrack> = emptyMap(),
    //plusieur car evite le recalcule si on change de range
    val muscleRateOfEachDay:Map<String, Map<Long,MuscleRate>> = emptyMap(), //{day:{idMuscle:{}}
    val dayRatio:Map<String,MuscleRate> = emptyMap(),
    val graphData: Map<Long, MuscleRate> = emptyMap(),

    val muscleSetPercentage: List<Triple<Long, Float, MuscleRate>> = emptyList(),

    val dateRange : DateRangeSelected= DateRangeSelected(),

    val exerciceChose: Long=0L,
    val tempChose:Long=0L,
    val isBottomSheetVisible: Boolean=false,
    val allDataExercise: Map<String, MuscleRate> =emptyMap(),



)


data class DateRangeSelected(
    val rangeInDays: Long = 14L, // 7, 14, 30, etc.
    val recentDate: LocalDate = LocalDate.now()
) {
    val oldDate: LocalDate
        get() = recentDate.minusDays(rangeInDays - 1L)
}
