package com.dyentrack.app.presentation.screens.analyse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dyentrack.app.core.locale.LocaleManager
import com.dyentrack.app.domain.model.ChartData
import com.dyentrack.app.domain.model.MesureUnit
import com.dyentrack.app.domain.model.MuscleRate
import com.dyentrack.app.domain.model.PresetId
import com.dyentrack.app.domain.model.RateSetSomme
import com.dyentrack.app.domain.model.UserPreference
import com.dyentrack.app.domain.model.dayData.DayDataRequest
import com.dyentrack.app.domain.model.dayData.SeanceTrack
import com.dyentrack.app.domain.model.enum.BottomSheetVisibleType
import com.dyentrack.app.domain.model.preset.Preset
import com.dyentrack.app.domain.usecase.GetRadarDataUseCase
import com.dyentrack.app.domain.usecase.daydata.GetDayDataUseCase
import com.dyentrack.app.domain.usecase.masterdata.BuildMasterDataMapsUseCase
import com.dyentrack.app.domain.usecase.masterdata.GetMasterDataStaticUseCase
import com.dyentrack.app.domain.usecase.muscleRate.ComputePlannedExerciseMuscleRateUseCase
import com.dyentrack.app.domain.usecase.preference.FetchUserPreferenceUseCase
import com.dyentrack.app.presentation.utils.convertWeight
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class AnalyseViewModel @Inject constructor(
    getDayDataUseCase: GetDayDataUseCase,
    getMasterDataStaticUseCase: GetMasterDataStaticUseCase,
    fetchUserPreferenceUseCase: FetchUserPreferenceUseCase,
    private val computePlannedExerciseMuscleRateUseCase: ComputePlannedExerciseMuscleRateUseCase,
    private val buildMasterDataMapsUseCase: BuildMasterDataMapsUseCase,
    private val getRadarDataUseCase: GetRadarDataUseCase,
    ) : ViewModel() {

    val dayDataFlow: StateFlow<List<DayDataRequest>?> = getDayDataUseCase.dayDataFlow
    private val _uiState = MutableStateFlow(AnalyseUiState())
    val uiState: StateFlow<AnalyseUiState> = _uiState
    val masterData = getMasterDataStaticUseCase.masterDataFlow.stateIn(
        viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )


    private val allSeanceByDay = mutableMapOf<String, Pair<SeanceTrack, LocalDate>>()
    val userPreference: StateFlow<UserPreference?> =
        fetchUserPreferenceUseCase.userPreferenceFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )
    val languageUser = LocaleManager.getCurrentLanguage()

    init {
        viewModelScope.launch {
            val maps = buildMasterDataMapsUseCase.execute(languageUser)
            _uiState.update { it.copy(masterDataMaps = maps) }
            dayDataFlow.collect { list ->
                if (list == null) {
                    _uiState.update { it.copy(mapSeance = emptyMap()) }
                    return@collect
                }

                var minDate: LocalDate? = null
                allSeanceByDay.clear()
                list.forEach { dayData ->
                    val seance = dayData.seanceTrack
                    if (seance?.plannedExercises?.isNotEmpty() == true) {
                        allSeanceByDay[dayData.dayData] = seance to dayData.localDate
                        val currentDate = dayData.localDate
                        if (minDate == null || currentDate.isBefore(minDate)) {
                            minDate = currentDate
                        }
                    }
                }

                _uiState.update {
                    it.copy(
                        dateRange = it.dateRange.copy(minExistingDate = minDate)
                    )
                }
                computeMuscleRateByDay()
            }

        }
    }


    fun toggleBottomSheet(show: BottomSheetVisibleType) {
        _uiState.update { it.copy(isBottomSheetVisible = show) }
    }


    fun choseThisExercise(exercise: Long?) {
        _uiState.update { it.copy(exerciceChose = exercise) }
    }

    fun choseThisPreset(preset: Preset?) {
        _uiState.update { it.copy(presetChose = preset) }
    }


    fun moyenneSetParEx(global: MuscleRate): Double =
        if (global.numberExercise > 0)
            ((global.numberSet.toDouble() / global.numberExercise) * 100).roundToInt() / 100.0
        else 0.0

    fun proportionSetFail(global: MuscleRate): Double =
        if (global.numberSet > 0)
            (global.rateSetSomme?.numberSetFail ?: 0).toDouble() / global.numberSet * 100
        else 0.0

    fun calculateMuscleSetPercentage(
        data: Map<String, DayExerciseData>
    ): Map<Long, Float> {
        val muscleTotals = mutableMapOf<Long, Float>()
        var totalSets = 0f

        data.values.forEach { day ->
            day.perMuscleStat.forEach { (muscleId, rate) ->
                muscleTotals[muscleId] = (muscleTotals[muscleId] ?: 0f) + rate.numberSet.toFloat()
                totalSets += rate.numberSet.toFloat()
            }
        }

        // Normalisation sur 100%
        return muscleTotals.mapValues { (_, value) ->
            if (totalSets > 0f) value / totalSets else 0f
        }.toList().sortedByDescending { it.second }
            .toMap(linkedMapOf())
    }

    fun getFilteredMuscleRateByDay(
        start: LocalDate,
        end: LocalDate,
        notIncludeGroupMuscle: Set<Long>? = emptySet()
    ): Map<String, DayExerciseData> {

        val excludedGroups = notIncludeGroupMuscle.orEmpty()
        val masterMaps = _uiState.value.masterDataMaps ?: return emptyMap()

        return _uiState.value.muscleRateByDay.mapNotNull { (dayStr, dayData) ->

            //  Filtre date
            allSeanceByDay[dayStr]?.second
                ?.takeIf { !it.isBefore(start) && !it.isAfter(end) }
                ?: return@mapNotNull null

            //  Filtre exercices
            val filteredExercises = dayData.perExerciseStat.filterKeys { exerciseId ->
                masterMaps.exerciseMap[exerciseId]
                    ?.muscleInfos
                    ?.filter { it.principal }
                    ?.none { info ->
                        masterMaps.muscleMap[info.idMuscle]?.idMuscularGroup in excludedGroups
                    } ?: true
            }

            if (filteredExercises.isEmpty()) return@mapNotNull null

            //  Reconstruction des stats à partir des exercices restants
            val perMuscleStat = mutableMapOf<Long, MuscleRate>()
            val perGroupStat = mutableMapOf<Long, MuscleRate>()

            var allStat = MuscleRate()

            filteredExercises.forEach { (exerciseId, rate) ->
                val exercise = masterMaps.exerciseMap[exerciseId] ?: return@forEach

                allStat = allStat.plus(rate)

                exercise.muscleInfos
                    ?.filter { it.principal }
                    ?.forEach { info ->
                        val muscle = masterMaps.muscleMap[info.idMuscle] ?: return@forEach
                        val groupId = muscle.idMuscularGroup ?: return@forEach

                        perMuscleStat[muscle.id] =
                            perMuscleStat[muscle.id]?.plus(rate) ?: rate

                        perGroupStat[groupId] =
                            perGroupStat[groupId]?.plus(rate) ?: rate
                    }
            }

            dayStr to dayData.copy(

                perExerciseStat = filteredExercises,
                perMuscleStat = perMuscleStat,
                perGroupMuscularStat = perGroupStat,
                AllStat = allStat
            )
        }.toMap()
    }


    fun changeNotIncludeList(idGroupMuscular: Long) {
        _uiState.update { state ->
            val currentSet = state.notIncludeGroupMuscle?.toMutableSet() ?: mutableSetOf()
            if (idGroupMuscular in currentSet) {
                currentSet.remove(idGroupMuscular)
            } else {
                currentSet.add(idGroupMuscular)
            }
            state.copy(notIncludeGroupMuscle = currentSet)
        }
    }

    fun applyDateRange(rangeInDays: Long) {
        _uiState.update {
            it.copy(
                dateRange = it.dateRange.copy(rangeInDays = rangeInDays)//refresh
            )
        }
    }
    fun applyDateRange(recentDate: LocalDate,oldDate: LocalDate){
        val nbday = ChronoUnit.DAYS.between(recentDate, oldDate)+1
        _uiState.update {
            it.copy(
                dateRange = it.dateRange.copy(rangeInDays = nbday, recentDate = oldDate)//refresh
            )
        }

    }

    /** Calcule tout MuscleRate pour chaque exercice et chaque jour */
    private fun computeMuscleRateByDay() {
        val masterDataMaps = _uiState.value.masterDataMaps ?: return

        val muscleRateByDay = mutableMapOf<String, DayExerciseData>()

        allSeanceByDay.forEach { (dayStr, pair) ->
            val (seanceTrack, _) = pair

            var totalDayRate = MuscleRate()

            val perExerciseStat = mutableMapOf<Long, MuscleRate>()
            val perMuscleStat = mutableMapOf<Long, MuscleRate>()
            val perGroupMuscularStat = mutableMapOf<Long, MuscleRate>()

            seanceTrack.plannedExercises.forEach { exerciseVO ->
                val muscleRate = computePlannedExerciseMuscleRateUseCase.execute(exerciseVO)
                perExerciseStat[exerciseVO.exerciseId] = muscleRate

                // Stats globales
                 totalDayRate = totalDayRate.plus(muscleRate)

                // Stats par muscle et groupe musculaire
                val exercise = masterDataMaps.exerciseMap[exerciseVO.exerciseId] ?: return@forEach
                exercise.muscleInfos?.forEach { info ->
                    if (!info.principal) return@forEach
                    val muscle = masterDataMaps.muscleMap[info.idMuscle] ?: return@forEach
                    val groupId = muscle.idMuscularGroup ?: 0L
                    perMuscleStat[muscle.id] =
                        perMuscleStat[muscle.id]?.plus(muscleRate) ?: muscleRate

                    perGroupMuscularStat[groupId] =
                        perGroupMuscularStat[groupId]?.plus(muscleRate) ?: muscleRate
                }
            }

            muscleRateByDay[dayStr] = DayExerciseData(
                presetId = PresetId(apiId = seanceTrack.presetSeanceId, localId = seanceTrack.presetLocalId),
                perExerciseStat = perExerciseStat,
                AllStat = totalDayRate,
                perMuscleStat = perMuscleStat,
                perGroupMuscularStat = perGroupMuscularStat
            )
        }

        _uiState.update {
            it.copy(
                muscleRateByDay = muscleRateByDay,
                mapSeance = allSeanceByDay.mapValues { it.value.first })
        }
    }


    fun getCombinedChartData(data: Map<String, MuscleRate>, userUnit: MesureUnit): ChartData {
        val labels = data.keys.toList()
        val barValues = data.values.map {
            convertWeight(it.rateSetSomme?.sommePond, MesureUnit.KG, userUnit)?.toFloat() ?: 0f
        }
        val lineValues = data.values.map {
            val totalSet = it.numberSet
            val failSet = it.rateSetSomme?.numberSetFail ?: 0
            if (totalSet > 0) failSet.toFloat() / totalSet else 0f
        }
        return ChartData(
            labels = labels,
            barValues = barValues,
            lineValues = lineValues,
            showValueBar = data.size < 35,
            showValueLine = data.size < 25
        )
    }

    fun getRadarData(
        data: Map<String, Map<Long, MuscleRate>>,
        labelsMap: Map<Long, String>
    ): Triple<List<String>, List<Float>, List<Float>> {
        return getRadarDataUseCase(data,labelsMap)
    }


    fun computeGlobalMuscleRate(data: Map<String, DayExerciseData>): MuscleRate =
        data.values
            .map { it.AllStat }
            .fold(MuscleRate()) { acc, stat -> acc.plus(stat) }



    private fun MuscleRate.plus(other: MuscleRate) = MuscleRate(
        numberExercise = numberExercise + other.numberExercise,
        numberSet = numberSet + other.numberSet,
        rateSetSomme = RateSetSomme(
            numberSetFail = (rateSetSomme?.numberSetFail ?: 0)
                    + (other.rateSetSomme?.numberSetFail ?: 0),
            sommePond = (rateSetSomme?.sommePond ?: 0.0)
                    + (other.rateSetSomme?.sommePond ?: 0.0),
            numberRep = (rateSetSomme?.numberRep ?: 0.0)
                    + (other.rateSetSomme?.numberRep ?: 0.0)
        )
    )

}