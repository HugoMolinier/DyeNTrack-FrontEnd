package com.dyentrack.app.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dyentrack.app.R
import com.dyentrack.app.core.locale.LocaleManager
import com.dyentrack.app.domain.model.DayDataRequest
import com.dyentrack.app.domain.model.MasterDataStatic
import com.dyentrack.app.domain.model.MuscleRate
import com.dyentrack.app.domain.model.SeanceTrack
import com.dyentrack.app.domain.usecase.masterdata.GetMasterDataStaticUseCase
import com.dyentrack.app.domain.usecase.daydata.GetDayDataUseCase
import com.dyentrack.app.domain.usecase.masterdata.BuildMasterDataMapsUseCase
import com.dyentrack.app.presentation.components.LeftIcon
import com.dyentrack.app.presentation.utils.ExerciseImages
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
    private val buildMasterDataMapsUseCase: BuildMasterDataMapsUseCase,

    ) : ViewModel() {

    val dayDataFlow: StateFlow<List<DayDataRequest>?> = getDayDataUseCase.dayDataFlow
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState
    var masterData: MasterDataStatic? = null
    val languageUser = LocaleManager.getCurrentLanguage()


    init {
        viewModelScope.launch {
            masterData = getMasterDataStaticUseCase()
            val maps = buildMasterDataMapsUseCase.execute(languageUser)
            _uiState.update { it.copy(masterDataMaps=maps)}

            val formatter = DateTimeFormatter.ISO_DATE
            val today = LocalDate.now()
            val last15Days = (0L until 14L).map { today.minusDays(it) }

            dayDataFlow.collect { list ->
                if (list == null) {
                    _uiState.update { it.copy(mapSeance = emptyMap()) }
                    return@collect
                }

                var todaySeanceTemp: DayDataRequest? = null
                val last5DaysSet = java.util.TreeSet<LocalDate>(compareByDescending { it })
                val tempMap = mutableMapOf<String, SeanceTrack>()

                for (dayData in list) {
                    val day = runCatching { LocalDate.parse(dayData.dayData, formatter) }.getOrNull() ?: continue

                    if (day == today) todaySeanceTemp = dayData

                    val hasExercises = dayData.seanceTrack?.plannedExercises?.isNotEmpty() == true
                    if (day in last15Days && hasExercises) {
                        tempMap[dayData.dayData] = dayData.seanceTrack!!
                        last5DaysSet.add(day)
                        if (last5DaysSet.size > 5) last5DaysSet.pollLast() // supprime le plus petit
                    }
                }

                _uiState.update { current -> current.copy(mapSeance = tempMap, todaySeance = todaySeanceTemp,   last5DaysKeys = last5DaysSet.map { it.format(formatter) }) }
                muscleRateOfEachDay(true)
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
        val lastScence = _uiState.value.mapSeance
        val muscleRate: MutableMap<String, MutableMap<Long, MuscleRate>> = mutableMapOf()
        val futurGraphData: MutableMap<Long, MuscleRate> =mutableMapOf()
        lastScence.forEach { (dayKey, seanceTrack) ->

            val dayMap = muscleRate.getOrPut(dayKey) { mutableMapOf() }



            seanceTrack.plannedExercises.forEach { plannedExerciseVO ->
                val exercise = uiState.value.masterDataMaps?.exerciseMap?.get(plannedExerciseVO.exerciseId)?: return@forEach
                exercise.muscleInfos?.forEach { muscleInfo ->
                    if (principalOnly && !muscleInfo.principal)return@forEach
                    val muscle = uiState.value.masterDataMaps?.muscleMap?.get(muscleInfo.idMuscle)
                        ?: return@forEach
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