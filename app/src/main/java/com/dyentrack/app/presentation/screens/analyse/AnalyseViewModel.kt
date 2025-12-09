package com.dyentrack.app.presentation.screens.analyse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dyentrack.app.domain.model.DayDataRequest
import com.dyentrack.app.domain.model.MasterDataStatic
import com.dyentrack.app.domain.model.MuscleRate
import com.dyentrack.app.domain.model.RateSetSomme
import com.dyentrack.app.domain.model.SeanceTrack
import com.dyentrack.app.domain.usecase.GetMasterDataStaticUseCase
import com.dyentrack.app.domain.usecase.daydata.GetDayDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.get
import kotlin.collections.sortedBy
import kotlin.math.ceil

@HiltViewModel
class AnalyseViewModel @Inject constructor(
    getDayDataUseCase: GetDayDataUseCase,
    getMasterDataStaticUseCase: GetMasterDataStaticUseCase,

    ) : ViewModel() {

    val dayDataFlow: StateFlow<List<DayDataRequest>?> = getDayDataUseCase.dayDataFlow
    private val _uiState = MutableStateFlow(AnalyseUiState())
    val uiState: StateFlow<AnalyseUiState> = _uiState
    var masterData: MasterDataStatic? = null

    private val allSeanceByDay = mutableMapOf<String, SeanceTrack>()


    init {
        viewModelScope.launch {
            masterData = getMasterDataStaticUseCase()

            dayDataFlow.collect { list ->
                if (list == null) {
                    _uiState.update { it.copy(mapSeance = emptyMap()) }
                    return@collect
                }

                allSeanceByDay.clear()
                list.forEach { dayData ->
                    allSeanceByDay[dayData.dayData]=dayData.seanceTrack
                }

                applyDateRange(_uiState.value.dateRange.rangeInDays)
            }

        }
    }


    fun applyDateRange(rangeInDays: Long) {
        val formatter = DateTimeFormatter.ISO_DATE
        val today = LocalDate.now()
        val lastDays = (0L..rangeInDays).map { today.minusDays(it-1L) }

        val filteredMap = allSeanceByDay.filter { (dateStr, seance) ->
            val day = LocalDate.parse(dateStr, formatter)
            day in lastDays && seance.plannedExercises.isNotEmpty()
        }

        _uiState.update {
            it.copy(
                mapSeance = filteredMap,
                dateRange = it.dateRange.copy(rangeInDays = rangeInDays)
            )
        }

        muscleRateOfEachDay(true)

        val musclePercentages = computeSetPercentagePerMuscle()
        _uiState.update {
            it.copy(muscleSetPercentage = musclePercentages)
        }
    }

    fun computeSetPercentagePerMuscle(): List<Triple<Long, Float, MuscleRate>> {
        val allDays = _uiState.value.muscleRateOfEachDay

        val totalSetPerMuscle = mutableMapOf<Long, Int>()
        val totalExercisePerMuscle = mutableMapOf<Long, Int>()
        var globalTotalSet = 0

        allDays.values.forEach { dayMap ->
            dayMap.forEach { (muscleId, muscleRate) ->
                totalSetPerMuscle[muscleId] = totalSetPerMuscle.getOrDefault(muscleId, 0) + muscleRate.numberSet
                totalExercisePerMuscle[muscleId] = totalExercisePerMuscle.getOrDefault(muscleId, 0) + muscleRate.numberExercise
                globalTotalSet += muscleRate.numberSet
            }
        }

        if (globalTotalSet == 0) return emptyList()

        return totalSetPerMuscle.map { (muscleId, muscleSet) ->
            val muscleRate = MuscleRate(
                numberExercise = totalExercisePerMuscle[muscleId] ?: 0,
                numberSet = muscleSet
            )
            Triple(muscleId, muscleSet.toFloat() / globalTotalSet.toFloat(), muscleRate)
        }.sortedByDescending { it.second }
    }

    fun muscleRateOfEachDay(principalOnly:Boolean) {
        val master = masterData ?: return

        val lastScence = _uiState.value.mapSeance
        val muscleRate: MutableMap<String, MutableMap<Long, MuscleRate>> = mutableMapOf()
        val futurGraphData: MutableMap<Long, MuscleRate> =mutableMapOf()
        val dayRatioMap: MutableMap<String, MuscleRate> = mutableMapOf()
        lastScence.forEach { (dayKey, seanceTrack) ->
            // CHAQUE jOUR
            var daySommePond = 0.0
            var dayFailSetCount = 0
            var dayTotalSet = 0
            var dayTotalExercise = 0


            val dayMap = muscleRate.getOrPut(dayKey) { mutableMapOf() }



            seanceTrack.plannedExercises.forEach { plannedExerciseVO ->
                val exercise = master.officialExercise.find { it.idExercise == plannedExerciseVO.exerciseId }?: return@forEach
                exercise.muscleInfos?.forEach { muscleInfo ->
                    if (principalOnly && !muscleInfo.principal)return@forEach
                    val muscle = master.muscles.find { it.id == muscleInfo.idMuscle } ?: return@forEach
                    val prevVal = dayMap[muscle.id]
                    val sets = plannedExerciseVO.sets.orEmpty()
                    var sommePond = 0.0
                    var numberSet = 0
                    var numberSetFail = 0
                    if (plannedExerciseVO.lateraliteId==2L) {
                        // ----- UNILATERAL -----
                        val grouped = sets.groupBy { it.setOrder }

                        grouped.forEach { (_, groupedSets) ->
                            // Moyenne reps gauche + reps droite
                            val avgReps = groupedSets.map { it.repsNumber }.average()
                            sommePond += avgReps * groupedSets.first().charge
                            numberSet += 1
                            if (groupedSets.first().rir==0) numberSetFail += 1 //
                        }

                    } else {
                        sets.forEach {
                            sommePond += it.repsNumber * it.charge
                            numberSet += 1
                            if (it.rir == 0) numberSetFail += 1
                        }
                    }
                    if (plannedExerciseVO.lateraliteId ==2L)  numberSet = ceil(numberSet / 2.0).toInt()
                    //pour muscle
                    val updatedValue = prevVal?.copy(
                        numberExercise = prevVal.numberExercise + 1,
                        numberSet = prevVal.numberSet + numberSet,
                        rateSetSomme = prevVal.rateSetSomme?.copy(
                            numberSetFail = prevVal.rateSetSomme.numberSetFail?.plus(numberSetFail),
                            sommePond = prevVal.rateSetSomme.sommePond?.plus(sommePond)
                        )
                    ) ?: MuscleRate(
                        numberExercise = 1,
                        numberSet = numberSet,
                        rateSetSomme = RateSetSomme(
                            numberSetFail = numberSetFail,
                            sommePond = sommePond
                        )
                    )
                    dayMap[muscle.id] = updatedValue

                    dayTotalExercise += 1
                    dayTotalSet += numberSet
                    dayFailSetCount += numberSetFail
                    daySommePond += sommePond


                    //pour groupe musculmaire sur 15j:
                    val prevValgroup = futurGraphData[muscle.idMuscularGroup]
                    val updatedValuegroup = prevValgroup?.copy(
                        numberExercise = prevValgroup.numberExercise + 1,
                        numberSet = prevValgroup.numberSet + numberSet
                    ) ?: MuscleRate(1, numberSet)
                    futurGraphData[muscle.idMuscularGroup as Long] =updatedValuegroup




                }
                dayRatioMap[dayKey] = MuscleRate(
                    numberExercise = dayTotalExercise,
                    numberSet = dayTotalSet,
                    rateSetSomme = RateSetSomme(
                        numberSetFail = dayFailSetCount,
                        sommePond = daySommePond
                    )
                )
            }

        }


        _uiState.update {
            it.copy(
                muscleRateOfEachDay = muscleRate,
                graphData = futurGraphData,
                dayRatio = dayRatioMap
            )
        }    }


}