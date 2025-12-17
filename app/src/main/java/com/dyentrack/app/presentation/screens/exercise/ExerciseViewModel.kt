package com.dyentrack.app.presentation.screens.exercise

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dyentrack.app.domain.model.DayDataRequest
import com.dyentrack.app.domain.model.InfoExerciseUser
import com.dyentrack.app.domain.model.MasterDataStatic
import com.dyentrack.app.domain.model.MuscleRate
import com.dyentrack.app.domain.model.RateSetSomme
import com.dyentrack.app.domain.model.exercise.Exercise
import com.dyentrack.app.domain.usecase.ComputePlannedExerciseMuscleRateUseCase
import com.dyentrack.app.domain.usecase.GetMasterDataStaticUseCase
import com.dyentrack.app.domain.usecase.daydata.GetDayDataUseCase
import com.dyentrack.app.domain.usecase.infoExerciseUser.GetInfoExerciseUserUseCase
import com.dyentrack.app.domain.usecase.infoExerciseUser.UpdateInfoExerciseUserUseCase
import com.dyentrack.app.presentation.screens.home.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.forEach
import kotlin.collections.groupBy
import kotlin.collections.orEmpty

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    getInfoExerciseUserUseCase: GetInfoExerciseUserUseCase,
    private val updateInfoExerciseUserUseCase: UpdateInfoExerciseUserUseCase,
    getDayDataUseCase: GetDayDataUseCase,
    getMasterDataStaticUseCase: GetMasterDataStaticUseCase,
    private val computePlannedExerciseMuscleRateUseCase: ComputePlannedExerciseMuscleRateUseCase
) : ViewModel() {

    val infoExerciseUserFlow =getInfoExerciseUserUseCase.infoExerciseUserFlow
    val dayDataFlow: StateFlow<List<DayDataRequest>?> = getDayDataUseCase.dayDataFlow
    private val _uiState = MutableStateFlow(ExerciseUiState())
    val uiState: StateFlow<ExerciseUiState> = _uiState
    var masterData: MasterDataStatic? = null
    private val _currentExercise = MutableStateFlow<Exercise?>(null)
    val currentExercise: StateFlow<Exercise?> = _currentExercise

    fun setExercise(exercise: Exercise) {
        _currentExercise.value = exercise
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

    init {
        viewModelScope.launch {
            masterData = getMasterDataStaticUseCase()
            launch {
                _currentExercise.collect { exercise ->
                    dayDataFlow.value?.let { dayDataList ->
                        loadRateOFDayData()
                    }
                }
            }

            // Collect dayDataFlow et recalculer si currentExercise est déjà défini
            launch {
                dayDataFlow.collect { dayDataList ->
                    _currentExercise.value?.let { exercise ->
                        loadRateOFDayData()
                    }
                }
            }


        }


    }

    fun changeSection(newSection :SectionChose){
        _uiState.update { current ->
            current.copy(sectionChose = newSection)
        }

    }
    fun loadRateOFDayData(){
        val muscleRateMap: MutableMap<String, MuscleRate> = mutableMapOf()

        dayDataFlow.value?.forEach { it->
            it.seanceTrack?.plannedExercises?.forEach {itPlannedExercise->
                if (itPlannedExercise.exerciseId==currentExercise.value?.idExercise)
                    muscleRateMap.put(it.dayData,computePlannedExerciseMuscleRateUseCase.execute(itPlannedExercise))
            }
        }
        _uiState.update { it.copy(allDataExercise = muscleRateMap) }

    }

    fun getAveragePerSet(top: Int? = null, skip: Int = 0): String {
        val allData = _uiState.value.allDataExercise
            .toSortedMap(reverseOrder())
            .values.toList()

        if (allData.isEmpty()) return "0.0"

        val selected = allData.drop(skip).let { list ->
            if (top != null) list.take(top) else list
        }

        val totalPond = selected.sumOf { it.rateSetSomme?.sommePond ?: 0.0 }
        val totalRep = selected.sumOf { it.rateSetSomme?.numberRep ?: 0.0 }

        val avg = if (totalRep > 0) totalPond / totalRep else "0.0"
        return String.format("%.1f", avg)
    }
    fun getLast14DaysDataDivided(): Map<String, MuscleRate> {
        val allData = _uiState.value.allDataExercise
        val today = java.time.LocalDate.now()
        val fourteenDaysAgo = today.minusDays(14)

        val filtered = allData.filter { (dayKey, _) ->
            val dayDate = try {
                java.time.LocalDate.parse(dayKey)
            } catch (e: Exception) {
                null
            }
            dayDate != null && !dayDate.isBefore(fourteenDaysAgo)
        }

        return filtered
    }

    fun getFrequencyPerWeek(lastDays: Map<String, MuscleRate>): Double {
        // divise le total par 2 pour ramener sur 1 semaine
        return lastDays.size / 2.0
    }

    fun getTotalSetsPerWeek(lastDays: Map<String, MuscleRate>): Double {
        val totalSets = lastDays.values.sumOf { it.numberSet }
        return totalSets / 2.0
    }

    fun getLastDoneDate(): String {
        val allData = _uiState.value.allDataExercise
        if (allData.isEmpty()) return "Aucune"
        val lastDate = allData.keys.maxOrNull()
        return lastDate ?: "Aucune"
    }









}