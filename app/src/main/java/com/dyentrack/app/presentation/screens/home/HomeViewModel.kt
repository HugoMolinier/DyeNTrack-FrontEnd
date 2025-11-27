package com.dyentrack.app.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dyentrack.app.domain.model.DayDataRequest
import com.dyentrack.app.domain.model.MasterDataStatic
import com.dyentrack.app.domain.model.MuscleRate
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
import kotlin.math.ceil

@HiltViewModel
class HomeViewModel @Inject constructor(
    getDayDataUseCase: GetDayDataUseCase,
    getMasterDataStaticUseCase: GetMasterDataStaticUseCase,

) : ViewModel() {

    val dayDataFlow: StateFlow<List<DayDataRequest>?> = getDayDataUseCase.dayDataFlow
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState
    var masterData: MasterDataStatic? = null



    init {
        viewModelScope.launch {
            masterData = getMasterDataStaticUseCase()

            dayDataFlow.collect { list ->
                if (list == null) {
                    _uiState.update { it.copy(mapSeance = emptyMap()) }
                    return@collect
                }
                val formatter = DateTimeFormatter.ISO_DATE
                val today = LocalDate.now()

                val last15Days = (0L..14L).map { today.minusDays(it) }.reversed()
                var todaySeanceTemp: DayDataRequest? = null
                val newMap = list
                    .mapNotNull { dayData ->
                        val day = LocalDate.parse(dayData.dayData, formatter)
                        if (day == today) todaySeanceTemp = dayData
                        if (day in last15Days && dayData.seanceTrack.plannedExercises.isNotEmpty()) {
                            dayData.dayData to dayData.seanceTrack
                        } else null
                    }
                    .toMap()

                _uiState.update { current -> current.copy(mapSeance = newMap, todaySeance = todaySeanceTemp) }
                muscleRateOfEachDay(true)
            }
        }
    }

    fun muscleRateOfEachDay(principalOnly:Boolean) {
            val master = masterData ?: return

        val lastScence = _uiState.value.mapSeance
        val muscleRate: MutableMap<String, MutableMap<Long, MuscleRate>> = mutableMapOf()
        val futurGraphData: MutableMap<Long, MuscleRate> =mutableMapOf()
        lastScence.forEach { (dayKey, seanceTrack) ->

            val dayMap = muscleRate.getOrPut(dayKey) { mutableMapOf() }



            seanceTrack.plannedExercises.forEach { plannedExerciseVO ->
                val exercise = master.officialExercise.find { it.idExercise == plannedExerciseVO.exerciseId }?: return@forEach
                exercise.muscleInfos?.forEach { muscleInfo ->
                    if (principalOnly && !muscleInfo.principal)return@forEach
                    val muscle = master.muscles.find { it.id == muscleInfo.idMuscle } ?: return@forEach
                    val prevVal = dayMap[muscle.id]
                    var numberSet =plannedExerciseVO.sets?.count() ?: 0
                    if (plannedExerciseVO.lateraliteId ==2L)  numberSet = ceil(numberSet / 2.0).toInt()
                    val exerciseCount =  1
                    //pour muscle
                    val updatedValue = prevVal?.copy(
                        numberExercise = prevVal.numberExercise + exerciseCount,
                                numberSet = prevVal.numberSet + numberSet
                    ) ?: MuscleRate(exerciseCount, numberSet)
                    dayMap[muscle.id] = updatedValue

                    //pour groupe musculmaire sur 15j:
                    val prevValgroup = futurGraphData[muscle.idMuscularGroup]
                    val updatedValuegroup = prevValgroup?.copy(
                        numberExercise = prevValgroup.numberExercise + exerciseCount,
                        numberSet = prevValgroup.numberSet + numberSet
                    ) ?: MuscleRate(exerciseCount, numberSet)
                    futurGraphData[muscle.idMuscularGroup as Long] =updatedValuegroup




                }
            }
        }

        _uiState.update { it.copy(muscleRateOfEachDay = muscleRate, graphData = futurGraphData) }
    }

    fun expandSeance(id: Int?) {
        _uiState.update { it.copy(expandedSeance = id) }
    }


}