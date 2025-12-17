package com.dyentrack.app.presentation.screens.analyse.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dyentrack.app.domain.model.DayDataRequest
import com.dyentrack.app.domain.model.InfoExerciseUser
import com.dyentrack.app.domain.model.MasterDataStatic
import com.dyentrack.app.domain.model.MuscleRate
import com.dyentrack.app.domain.model.RateSetSomme
import com.dyentrack.app.domain.model.SeanceTrack
import com.dyentrack.app.domain.usecase.ComputePlannedExerciseMuscleRateUseCase
import com.dyentrack.app.domain.usecase.GetMasterDataStaticUseCase
import com.dyentrack.app.domain.usecase.daydata.GetDayDataUseCase
import com.dyentrack.app.domain.usecase.infoExerciseUser.GetInfoExerciseUserUseCase
import com.dyentrack.app.domain.usecase.infoExerciseUser.UpdateInfoExerciseUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.collections.get

@HiltViewModel
class AnalyseViewModel @Inject constructor(
    getDayDataUseCase: GetDayDataUseCase,
    getMasterDataStaticUseCase: GetMasterDataStaticUseCase,
    private val updateInfoExerciseUserUseCase: UpdateInfoExerciseUserUseCase,
    getInfoExerciseUserUseCase: GetInfoExerciseUserUseCase,
    private val computePlannedExerciseMuscleRateUseCase: ComputePlannedExerciseMuscleRateUseCase

    ) : ViewModel() {

    val dayDataFlow: StateFlow<List<DayDataRequest>?> = getDayDataUseCase.dayDataFlow
    private val _uiState = MutableStateFlow(AnalyseUiState())
    val uiState: StateFlow<AnalyseUiState> = _uiState
    var masterData: MasterDataStatic? = null
    val infoExerciseUserFlow =getInfoExerciseUserUseCase.infoExerciseUserFlow
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
                    if (dayData.seanceTrack?.plannedExercises?.isNotEmpty() == true) { allSeanceByDay[dayData.dayData]=dayData!!.seanceTrack}
                }

                applyDateRange(_uiState.value.dateRange.rangeInDays)
            }

        }
    }


    fun toggleBottomSheet(show: Boolean) {
        _uiState.update { it.copy(isBottomSheetVisible = show) }
    }
    fun changeTempExercice(exerciseId:Long){
        _uiState.update { it.copy(tempChose = exerciseId) }
    }
    fun choseThisExercise(){
        _uiState.update { it.copy(exerciceChose = uiState.value.tempChose) }
        updateRateOfDayDataWithRange()
    }
    fun updateRateOfDayDataWithRange() {
        val muscleRateMap: MutableMap<String, MuscleRate> = mutableMapOf()
        val formatter = DateTimeFormatter.ISO_DATE
        val today = LocalDate.now()
        val rangeInDays = _uiState.value.dateRange.rangeInDays
        val lastDays = (0L until rangeInDays).map { today.minusDays(it) }

        dayDataFlow.value?.forEach { dayData ->
            val dayDate = LocalDate.parse(dayData.dayData, formatter)
            if (dayDate in lastDays) {
                dayData.seanceTrack?.plannedExercises?.forEach { plannedExercise ->
                    if (plannedExercise.exerciseId == _uiState.value.exerciceChose) {
                        muscleRateMap[dayData.dayData] =
                            computePlannedExerciseMuscleRateUseCase.execute(plannedExercise)
                    }
                }
            }
        }

        Log.d("Analyse", "MuscleRateMap filtered by range: $muscleRateMap")
        _uiState.update { it.copy(allDataExercise = muscleRateMap) }
    }


    fun applyDateRange(rangeInDays: Long) {
        val formatter = DateTimeFormatter.ISO_DATE
        val today = LocalDate.now()
        val lastDays = (0L until rangeInDays).map { today.minusDays(it) }

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

    fun updateInfoExerciseUser(
        idExercise: Long,
        favorite: Boolean?,
        note: String?
    ) {
        viewModelScope.launch {
            val info = InfoExerciseUser(idExercise = idExercise, favorite = favorite, note = note)
            updateInfoExerciseUserUseCase(info)
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
        val muscleRateMapExercise: MutableMap<String, MuscleRate> = mutableMapOf()
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
                    if(uiState.value.exerciceChose==0L){
                        _uiState.update { it.copy(exerciceChose = plannedExerciseVO.exerciseId, tempChose = plannedExerciseVO.exerciseId) }
                    }
                    val muscle = master.muscles.find { it.id == muscleInfo.idMuscle } ?: return@forEach
                    val prevVal = dayMap[muscle.id]
                    val muscleRateComputed = computePlannedExerciseMuscleRateUseCase.execute(
                        plannedExercise = plannedExerciseVO,
                    )
                    if (_uiState.value.exerciceChose==plannedExerciseVO.exerciseId){
                        muscleRateMapExercise.put(dayKey,muscleRateComputed)
                    }
                    //pour muscle
                    val updatedValue = prevVal?.copy(
                        numberExercise = prevVal.numberExercise + 1,
                        numberSet = prevVal.numberSet +  muscleRateComputed.numberSet,
                        rateSetSomme = prevVal.rateSetSomme?.copy(
                            numberSetFail = prevVal.rateSetSomme.numberSetFail?.plus( muscleRateComputed.rateSetSomme?.numberSetFail?:0),
                            sommePond = prevVal.rateSetSomme.sommePond?.plus( muscleRateComputed.rateSetSomme?.sommePond?:0.0)
                        )
                    ) ?: muscleRateComputed
                    dayMap[muscle.id] = updatedValue

                    dayTotalExercise += muscleRateComputed.numberExercise
                    dayTotalSet += muscleRateComputed.numberSet
                    dayFailSetCount += muscleRateComputed.rateSetSomme?.numberSetFail ?: 0
                    daySommePond += muscleRateComputed.rateSetSomme?.sommePond ?: 0.0


                    //pour groupe musculmaire sur 15j:
                    val prevValgroup = futurGraphData[muscle.idMuscularGroup]
                    val updatedValuegroup = prevValgroup?.copy(
                        numberExercise = prevValgroup.numberExercise + 1,
                        numberSet = prevValgroup.numberSet  + muscleRateComputed.numberSet
                    ) ?: MuscleRate(muscleRateComputed.numberExercise, muscleRateComputed.numberSet)
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
                dayRatio = dayRatioMap,
                allDataExercise=muscleRateMapExercise
            )
        }    }


}