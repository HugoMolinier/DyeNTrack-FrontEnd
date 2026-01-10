package com.dyentrack.app.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dyentrack.app.R
import com.dyentrack.app.core.locale.LocaleManager
import com.dyentrack.app.domain.model.MuscleRate
import com.dyentrack.app.domain.model.dayData.DayDataRequest
import com.dyentrack.app.domain.model.dayData.SeanceTrack
import com.dyentrack.app.domain.model.enum.BottomSheetVisibleType
import com.dyentrack.app.domain.usecase.GetRadarDataUseCase
import com.dyentrack.app.domain.usecase.daydata.GetDayDataUseCase
import com.dyentrack.app.domain.usecase.masterdata.BuildMasterDataMapsUseCase
import com.dyentrack.app.domain.usecase.masterdata.GetMasterDataStaticUseCase
import com.dyentrack.app.presentation.components.LeftIcon
import com.dyentrack.app.presentation.utils.ExerciseImages
import com.dyentrack.app.presentation.utils.toIsoDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import kotlin.math.ceil

@HiltViewModel

class HomeViewModel @Inject constructor(
    getDayDataUseCase: GetDayDataUseCase,
    getMasterDataStaticUseCase: GetMasterDataStaticUseCase,
    private val buildMasterDataMapsUseCase: BuildMasterDataMapsUseCase,
    private val getRadarDataUseCase: GetRadarDataUseCase,
) : ViewModel() {

    private val dayDataFlow: StateFlow<List<DayDataRequest>?> = getDayDataUseCase.dayDataFlow
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState
    val masterData = getMasterDataStaticUseCase.masterDataFlow.stateIn(
        viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )
    val languageUser = LocaleManager.getCurrentLanguage()


    init {
        val today = LocalDate.now()
        val todayIso = today.toIsoDate()
        //Evite de parser 100date et compare juste des string
        val last15DaysSet = (0L until 15L).map { today.minusDays(it).toIsoDate() }.toSet()

        viewModelScope.launch {
            val maps = buildMasterDataMapsUseCase.execute(languageUser)
            _uiState.update { it.copy(masterDataMaps = maps) }

            dayDataFlow.collect { list ->
                if (list == null) {
                    _uiState.update {
                        it.copy(
                            mapSeance = emptyMap(),
                            todaySeance = null,
                            last5DaysKeys = emptyList(),
                            muscleRateOfEachDay = emptyMap(),
                            groupRateOfEachDay = emptyMap()
                        )
                    }

                    return@collect
                }

                var todaySeance: DayDataRequest? = null
                val last5DaysSorted = java.util.TreeSet<LocalDate>(compareByDescending { it })
                val tempMap = mutableMapOf<String, SeanceTrack>()

                list.forEach { dayData ->
                    if (dayData.dayData !in last15DaysSet) return@forEach // seulement 15 derniers jours

                    if (dayData.dayData == todayIso) todaySeance = dayData

                    val exercises = dayData.seanceTrack?.plannedExercises
                    if (!exercises.isNullOrEmpty()) {
                        tempMap[dayData.dayData] = dayData.seanceTrack
                        last5DaysSorted.add(dayData.localDate)
                        if (last5DaysSorted.size > 5) last5DaysSorted.pollLast()
                    }
                }

                _uiState.update { current ->
                    current.copy(
                        mapSeance = tempMap,
                        todaySeance = todaySeance,
                        last5DaysKeys = last5DaysSorted.map { it.toIsoDate() }
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

        if (seance.plannedExercises.size > 3) {
            first3Icons.add(R.drawable.point)
        }
        return if (first3Icons.isNotEmpty()) {
            LeftIcon.Multiple(first3Icons)
        } else {
            LeftIcon.Single(R.drawable.logotransparent)
        }
    }

    fun getRadarData(
        data: Map<String, Map<Long, MuscleRate>>,
        labelsMap: Map<Long, String>
    ): Triple<List<String>, List<Float>, List<Float>> {
        return getRadarDataUseCase(data, labelsMap)
    }

    fun toggleBottomSheet(show: BottomSheetVisibleType) {
        _uiState.update { it.copy(isBottomSheetVisible = show) }
    }

    fun muscleRateOfEachDay(principalOnly: Boolean) {
        val lastSeance = _uiState.value.mapSeance

        val muscleRateByDay: MutableMap<String, MutableMap<Long, MuscleRate>> = mutableMapOf()
        val groupRateByDay: MutableMap<String, MutableMap<Long, MuscleRate>> =
            mutableMapOf() // par jour

        lastSeance.forEach { (dayKey, seanceTrack) ->
            val dayMap = muscleRateByDay.getOrPut(dayKey) { mutableMapOf() }
            val groupMap = groupRateByDay.getOrPut(dayKey) { mutableMapOf() }

            seanceTrack.plannedExercises.forEach { exerciseVO ->
                val exercise =
                    _uiState.value.masterDataMaps?.exerciseMap?.get(exerciseVO.exerciseId)
                        ?: return@forEach
                val setsCount = exerciseVO.sets?.count() ?: 0
                val adjustedSets =
                    if (exerciseVO.lateraliteId == 2L) ceil(setsCount / 2.0).toInt() else setsCount


                val muscleId = exercise.mainMuscle ?: 0L
                val groupId =
                    _uiState.value.masterDataMaps?.muscleMap?.get(muscleId)?.idMuscularGroup ?: 0L

                // Muscle individuel
                dayMap[muscleId] = dayMap[muscleId]?.copy(
                    numberExercise = dayMap[muscleId]!!.numberExercise + 1,
                    numberSet = dayMap[muscleId]!!.numberSet + adjustedSets
                ) ?: MuscleRate(1, adjustedSets)

                // Groupe musculaire
                groupMap[groupId] = groupMap[groupId]?.copy(
                    numberExercise = groupMap[groupId]!!.numberExercise + 1,
                    numberSet = groupMap[groupId]!!.numberSet + adjustedSets
                ) ?: MuscleRate(1, adjustedSets)

            }

            muscleRateByDay[dayKey] = dayMap
            groupRateByDay[dayKey] = groupMap
        }

        _uiState.update {
            it.copy(
                muscleRateOfEachDay = muscleRateByDay,
                groupRateOfEachDay = groupRateByDay
            )
        }
    }


    fun expandSeance(id: Int?) {
        _uiState.update { it.copy(expandedSeance = id) }
    }


}