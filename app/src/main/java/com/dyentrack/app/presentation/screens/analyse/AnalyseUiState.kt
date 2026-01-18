package com.dyentrack.app.presentation.screens.analyse

import com.dyentrack.app.domain.model.MasterDataMaps
import com.dyentrack.app.domain.model.MuscleRate
import com.dyentrack.app.domain.model.PresetId
import com.dyentrack.app.domain.model.dayData.SeanceTrack
import com.dyentrack.app.domain.model.enums.AnalyseModeLine
import com.dyentrack.app.domain.model.enums.BottomSheetVisibleType
import com.dyentrack.app.domain.model.preset.Preset
import java.time.LocalDate
import java.time.temporal.ChronoUnit


data class AnalyseUiState(
    val mapSeance: Map<String, SeanceTrack> = emptyMap(),
    //plusieur car evite le recalcule si on change de range
    val muscleRateByDay: Map<String, DayExerciseData> = emptyMap(), // jour -> presetId + exercises
    val caloriesMap: Map<String, Int> = emptyMap(),
    val dateRange: DateRangeSelected = DateRangeSelected(),
    val masterDataMaps: MasterDataMaps? = MasterDataMaps(),

    val exerciceChose: Long? = 0L,
    val presetChose: Preset? = null,
    val modeAnalyse :AnalyseModeLine?=AnalyseModeLine.RATIO,

    val notIncludeGroupMuscle: Set<Long>? = emptySet(),
    val isBottomSheetVisible: BottomSheetVisibleType = BottomSheetVisibleType.NO,
)


data class DayExerciseData(
    // evite le max de calcul par la suite dans l'ui
    val presetId: PresetId? = PresetId(null, null), //API, Local
    val perExerciseStat: Map<Long, MuscleRate> = mutableMapOf(),
    val AllStat: MuscleRate,
    val perGroupMuscularStat: Map<Long, MuscleRate> = emptyMap(),             // groupe musculaire global
    val perMuscleStat: Map<Long, MuscleRate> = emptyMap()

)

data class DateRangeSelected(
    val rangeInDays: Long = 14L,
    val recentDate: LocalDate = LocalDate.now(),
    val minExistingDate: LocalDate? = null
) {
    val oldDate: LocalDate
        get() {
            // MODE ALL
            if (rangeInDays == -1L && minExistingDate != null) {
                return minExistingDate
            }
            val computedOldDate = recentDate.minusDays(rangeInDays - 1)
            return if (minExistingDate != null && computedOldDate.isBefore(minExistingDate)) {
                minExistingDate
            } else computedOldDate
        }

    val numberBetweenDate: Int
        get() {
            // MODE ALL
            if (rangeInDays == -1L && minExistingDate != null) {
                return ChronoUnit.DAYS.between(minExistingDate, recentDate).toInt() + 1
            }
            return rangeInDays.toInt()
        }
}
