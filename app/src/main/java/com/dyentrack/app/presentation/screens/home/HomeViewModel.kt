package com.dyentrack.app.presentation.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dyentrack.app.R
import com.dyentrack.app.domain.model.DayDataRequest
import com.dyentrack.app.domain.model.HealthConnectStatus
import com.dyentrack.app.domain.model.MasterDataStatic
import com.dyentrack.app.domain.model.MuscleRate
import com.dyentrack.app.domain.model.SeanceTrack
import com.dyentrack.app.domain.usecase.GetMasterDataStaticUseCase
import com.dyentrack.app.domain.usecase.daydata.GetDayDataUseCase
import com.dyentrack.app.domain.usecase.health.CheckHealthConnectUseCase
import com.dyentrack.app.domain.usecase.health.RequestHealthConnectPermissionsUseCase
import com.dyentrack.app.domain.usecase.health.SyncHealthDataUseCase
import com.dyentrack.app.presentation.components.LeftIcon
import com.dyentrack.app.presentation.utils.ExerciseImages
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
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
    private val healthConnectUseCase: CheckHealthConnectUseCase,
    private val requestHealthConnectPermissionsUseCase: RequestHealthConnectPermissionsUseCase,
    private val syncHealthDataUseCas: SyncHealthDataUseCase
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

                val last15Days = (0L until 14L).map { today.minusDays(it) }
                var todaySeanceTemp: DayDataRequest? = null
                val newMap = list
                    .mapNotNull { dayData ->
                        val day = LocalDate.parse(dayData.dayData, formatter)
                        if (day == today) todaySeanceTemp = dayData
                        if (day in last15Days && dayData.seanceTrack?.plannedExercises?.isNotEmpty()==true) {
                            dayData.dayData to dayData.seanceTrack
                        } else null
                    }
                    .toMap()

                _uiState.update { current -> current.copy(mapSeance = newMap, todaySeance = todaySeanceTemp) }
                muscleRateOfEachDay(true)
            }

        }

        viewModelScope.launch {
            dayDataFlow.first { it != null }
            val status = healthConnectUseCase()
            when (status) {
                is HealthConnectStatus.Available -> {
                    val client = status.client
                    val permissionsGranted = requestHealthConnectPermissionsUseCase.arePermissionsGranted(client)
                    if (permissionsGranted) {
                       syncHealthDataUseCas.invoke(client)
                    } else {
                        Log.d("HealthSync", "Permissions non accordées")
                    }
                }
                else -> Log.d("HealthSync", "Health Connect non disponible")
            }
        }
    }

    fun getLeftIconForSeance(seance: SeanceTrack?): LeftIcon {
        if (seance == null) return LeftIcon.Single(R.drawable.logotransparent)
        val first3Icons = seance.plannedExercises
            .take(3)
            .map { ExerciseImages.map[it.exerciseId]?:R.drawable.logotransparent }.toMutableList()

        if (seance.plannedExercises.size>3){
        first3Icons.add(R.drawable.point)}
        return if (first3Icons.isNotEmpty()) {
            LeftIcon.Multiple(first3Icons)
        } else {
            LeftIcon.Single(R.drawable.logotransparent) // fallback si aucun drawable trouvé
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