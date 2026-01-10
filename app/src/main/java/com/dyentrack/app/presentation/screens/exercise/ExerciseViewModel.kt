package com.dyentrack.app.presentation.screens.exercise

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dyentrack.app.R
import com.dyentrack.app.core.locale.LocaleManager
import com.dyentrack.app.domain.model.dayData.DayDataRequest
import com.dyentrack.app.domain.model.MesureUnit
import com.dyentrack.app.domain.model.MuscleRate
import com.dyentrack.app.domain.model.exercise.Exercise
import com.dyentrack.app.domain.model.getName
import com.dyentrack.app.domain.usecase.muscleRate.ComputePlannedExerciseMuscleRateUseCase
import com.dyentrack.app.domain.usecase.masterdata.GetMasterDataStaticUseCase
import com.dyentrack.app.domain.usecase.daydata.GetDayDataUseCase
import com.dyentrack.app.domain.usecase.masterdata.BuildMasterDataMapsUseCase
import com.dyentrack.app.presentation.utils.convertWeight
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    getDayDataUseCase: GetDayDataUseCase,
    getMasterDataStaticUseCase: GetMasterDataStaticUseCase,
    private val computePlannedExerciseMuscleRateUseCase: ComputePlannedExerciseMuscleRateUseCase,
    private val buildMasterDataMapsUseCase: BuildMasterDataMapsUseCase,

) : ViewModel() {

    val dayDataFlow: StateFlow<List<DayDataRequest>?> = getDayDataUseCase.dayDataFlow
    private val _uiState = MutableStateFlow(ExerciseUiState())
    val uiState: StateFlow<ExerciseUiState> = _uiState
    val masterData= getMasterDataStaticUseCase.masterDataFlow.stateIn(viewModelScope, started = SharingStarted.Eagerly, initialValue = null)
    private val _currentExercise = MutableStateFlow<Exercise?>(null)
    val currentExercise: StateFlow<Exercise?> = _currentExercise

    val languageUser = LocaleManager.getCurrentLanguage()
    fun setExercise(exerciseId: Long) {
        _currentExercise.value = _uiState.value.masterDataMaps?.exerciseMap?.get(exerciseId)
    }

    init {
        viewModelScope.launch {
            val maps = buildMasterDataMapsUseCase.execute(languageUser)
            _uiState.update { it.copy(masterDataMaps=maps)}
            launch {
                _currentExercise.collect { exercise ->
                    dayDataFlow.value?.let { dayDataList ->
                        loadRateOFDayData()
                    }
                }
            }
            launch {
                dayDataFlow.collect { dayDataList ->
                    _currentExercise.value?.let { exercise ->
                        loadRateOFDayData()
                    }
                }
            }


        }


    }

    fun changeSection(newSection: SectionChose) {
        _uiState.update { current ->
            current.copy(sectionChose = newSection)
        }

    }

    fun loadRateOFDayData() {
        val muscleRateMap: MutableMap<String, MuscleRate> = mutableMapOf()

        dayDataFlow.value?.forEach { it ->
            it.seanceTrack?.plannedExercises?.forEach { itPlannedExercise ->
                if (itPlannedExercise.exerciseId == currentExercise.value?.idExercise)
                    muscleRateMap.put(
                        it.dayData,
                        computePlannedExerciseMuscleRateUseCase.execute(itPlannedExercise)
                    )
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
        return String.format(Locale.getDefault(),"%.1f", avg)
    }

    fun getAverageWeightFormatted(top: Int = 5, skip: Int = 0, userUnit: MesureUnit): String {
        val avg = getAveragePerSet(top, skip).toDoubleOrNull() ?: 0.0
        return String.format(Locale.getDefault(),
            "%.1f%s",
            convertWeight(avg, MesureUnit.KG, userUnit) ?: 0.0,
            userUnit.toString().lowercase()
        )
    }

    fun getLast14DaysDataDivided(): Map<String, MuscleRate> {
        val allData = _uiState.value.allDataExercise
        val today = LocalDate.now()
        val fourteenDaysAgo = today.minusDays(14)

        val filtered = allData.filter { (dayKey, _) ->
            val dayDate = try {
                LocalDate.parse(dayKey)
            } catch (_: Exception) {
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
        if (allData.isEmpty()) return "NaN"
        val lastDate = allData.keys.maxOrNull()
        return lastDate ?: "NaN"
    }

    fun getMuscleUsedNames(exercise: Exercise): Set<String> {
        return exercise.muscleInfos
            ?.mapNotNull { muscleInfo ->
                uiState.value.masterDataMaps?.muscleMap?.get(muscleInfo.idMuscle)?.getName(languageUser)
            }?.toSet() ?: emptySet()
    }
    fun getPrimaryMuscleName(exercise: Exercise): String {
        return uiState.value.masterDataMaps?.muscleMap?.get(exercise.mainMuscle)?.getName(languageUser) ?: "NaN"
    }

    fun getEquipmentNames(): String {
        return masterData.value?.equipment?.joinToString(", ") { it.getName(languageUser) } ?: "NaN"
    }

    fun getLateralities(): String {
        return masterData.value?.lateralites?.joinToString(", ") { it.getName(languageUser) } ?: "NaN"
    }

    @Composable
    fun getExerciseCreatorName(exercise: Exercise, idUser: Long): String {
        return when (exercise.idCreator) {
            null -> stringResource(R.string.app_name)
            idUser -> stringResource(R.string.you)
            else -> stringResource(R.string.a_user)
        }
    }


    fun getChartBarValues(userUnit: MesureUnit): List<Float> {
        return _uiState.value.allDataExercise.values.map {
            convertWeight(it.rateSetSomme?.sommePond, MesureUnit.KG, userUnit)?.toFloat() ?: 0f
        }
    }

    fun getChartLineValues(): List<Float> {
        return _uiState.value.allDataExercise.values.map {
            val totalSet = it.numberSet
            val failSet = it.rateSetSomme?.numberSetFail ?: 0
            if (totalSet > 0) failSet.toFloat() / totalSet else 0f
        }
    }


}