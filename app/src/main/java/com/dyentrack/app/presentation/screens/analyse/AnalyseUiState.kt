package com.dyentrack.app.presentation.screens.analyse

import com.dyentrack.app.domain.model.MasterDataMaps
import com.dyentrack.app.domain.model.MuscleRate
import com.dyentrack.app.domain.model.dayData.SeanceTrack
import java.time.LocalDate


data class AnalyseUiState(
    val mapSeance:Map<String, SeanceTrack> = emptyMap(),
    //plusieur car evite le recalcule si on change de range
    val muscleRateOfEachDay:Map<String, Map<Long, MuscleRate>> = emptyMap(), //{day:{idMuscle:{}}
    val dayRatio:Map<String, MuscleRate> = emptyMap(),
    val graphData: Map<Long, MuscleRate> = emptyMap(),

    val muscleSetPercentage: List<Triple<Long, Float, MuscleRate>> = emptyList(),

    val dateRange : DateRangeSelected= DateRangeSelected(),
    val masterDataMaps: MasterDataMaps? = MasterDataMaps(),

    val exerciceChose: Long=0L,
    val tempChose:Long=0L,
    val isBottomSheetVisible: Boolean=false,
    val allDataExercise: Map<String, MuscleRate> =emptyMap(),





    )


data class DateRangeSelected(
    val rangeInDays: Long = 14L,
    val recentDate: LocalDate = LocalDate.now(),
    val minExistingDate: LocalDate? = null
) {
    val oldDate: LocalDate
        get() {
            val computedOldDate = recentDate.minusDays(rangeInDays - 1)
            return if (minExistingDate != null && computedOldDate.isBefore(minExistingDate)) {
                minExistingDate
            } else computedOldDate
        }
}