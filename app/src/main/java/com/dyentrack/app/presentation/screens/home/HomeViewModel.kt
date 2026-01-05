package com.dyentrack.app.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dyentrack.app.R
import com.dyentrack.app.core.locale.LocaleManager
import com.dyentrack.app.domain.model.DayDataRequest
import com.dyentrack.app.domain.model.MuscleRate
import com.dyentrack.app.domain.model.SeanceTrack
import com.dyentrack.app.domain.model.enum.BottomSheetVisibleType
import com.dyentrack.app.domain.usecase.daydata.GetDayDataUseCase
import com.dyentrack.app.domain.usecase.masterdata.BuildMasterDataMapsUseCase
import com.dyentrack.app.domain.usecase.masterdata.GetMasterDataStaticUseCase
import com.dyentrack.app.presentation.components.LeftIcon
import com.dyentrack.app.presentation.utils.ExerciseImages
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
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
    val masterData = getMasterDataStaticUseCase.masterDataFlow.stateIn(viewModelScope, started = SharingStarted.Lazily, initialValue = null)
    val languageUser = LocaleManager.getCurrentLanguage()



    init {
        viewModelScope.launch {
            val maps = buildMasterDataMapsUseCase.execute(languageUser)
            _uiState.update { it.copy(masterDataMaps=maps)}

            val formatter = DateTimeFormatter.ISO_DATE
            val today = LocalDate.now()
            val last15DaysSet = (0L until 15L).map { today.minusDays(it) }.toSet()

            dayDataFlow.collect { list ->
                if (list == null) {
                    _uiState.update { it.copy(mapSeance = emptyMap()) }
                    return@collect
                }

                var todaySeance: DayDataRequest? = null
                val last5DaysSorted  = java.util.TreeSet<LocalDate>(compareByDescending { it })
                val tempMap = mutableMapOf<String, SeanceTrack>()

                list.forEach { dayData ->
                    val day = runCatching { LocalDate.parse(dayData.dayData, formatter) }.getOrNull() ?: return@forEach
                    if (day == today) todaySeance = dayData

                    val exercises = dayData.seanceTrack?.plannedExercises
                    if (day in last15DaysSet && !exercises.isNullOrEmpty()) {
                        tempMap[dayData.dayData] = dayData.seanceTrack!!
                        last5DaysSorted.add(day)
                        if (last5DaysSorted.size > 5) last5DaysSorted.pollLast()
                    }
                }

                _uiState.update { current ->
                    current.copy(
                        mapSeance = tempMap,
                        todaySeance = todaySeance,
                        last5DaysKeys = last5DaysSorted.map { it.format(formatter) }
                    )
                }
                muscleRateOfEachDay(true)
            }

        }

    }

    fun getLeftIconForSeance(seance: SeanceTrack?): LeftIcon {
        if (seance == null) return LeftIcon.Single(R.drawable.logotransparent)
        val first3Icons = seance.plannedExercises
            .asSequence()                     // lazy
            .sortedBy { it.exerciseOrder }
            .map { ExerciseImages.map[it.exerciseId] ?: R.drawable.logotransparent } // map
            .take(3)
            .toMutableList()

        if (seance.plannedExercises.size>3){
        first3Icons.add(R.drawable.point)}
        return if (first3Icons.isNotEmpty()) {
            LeftIcon.Multiple(first3Icons)
        } else {
            LeftIcon.Single(R.drawable.logotransparent)
        }
    }

    fun toggleBottomSheet(show: BottomSheetVisibleType) {
        _uiState.update { it.copy(isBottomSheetVisible = show) }
    }
    fun muscleRateOfEachDay(principalOnly:Boolean) {
        val lastScence = _uiState.value.mapSeance

        val muscleRate: MutableMap<String, MutableMap<Long, MuscleRate>> = mutableMapOf()
        val futurGraphData: MutableMap<Long, MuscleRate> =mutableMapOf()
        lastScence.forEach { (dayKey, seanceTrack) ->

            val dayMap = muscleRate.getOrPut(dayKey) { mutableMapOf() }



            seanceTrack.plannedExercises.forEach { plannedExerciseVO ->
                val exercise = uiState.value.masterDataMaps?.exerciseMap?.get(plannedExerciseVO.exerciseId)?: return@forEach
                val setsCount = plannedExerciseVO.sets?.count() ?: 0
                val adjustedSets = if (plannedExerciseVO.lateraliteId == 2L) ceil(setsCount / 2.0).toInt() else setsCount

                exercise.muscleInfos
                    ?.filter { !principalOnly || it.principal }
                    ?.forEach { muscleInfo ->
                        val muscleId = muscleInfo.idMuscle
                        val groupId = _uiState.value.masterDataMaps?.muscleMap?.get(muscleId)?.idMuscularGroup?:0L


                        val prev = dayMap[muscleId]
                        dayMap[muscleId] = prev?.copy(
                            numberExercise = prev.numberExercise + 1,
                            numberSet = prev.numberSet + adjustedSets
                        ) ?: MuscleRate(1, adjustedSets)


                        //pour groupe musculmaire sur 15j:
                        val prevGroup = futurGraphData[groupId]
                        futurGraphData[groupId] = prevGroup?.copy(
                            numberExercise = prevGroup.numberExercise + 1,
                            numberSet = prevGroup.numberSet + adjustedSets
                        ) ?: MuscleRate(1, adjustedSets)




                }
            }
            muscleRate[dayKey] = dayMap
        }

        _uiState.update { it.copy(muscleRateOfEachDay = muscleRate, graphData = futurGraphData) }
    }

    fun expandSeance(id: Int?) {
        _uiState.update { it.copy(expandedSeance = id) }
    }


}