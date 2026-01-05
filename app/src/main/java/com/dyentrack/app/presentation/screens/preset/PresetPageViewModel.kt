package com.dyentrack.app.presentation.screens.preset

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dyentrack.app.core.locale.LocaleManager
import com.dyentrack.app.domain.model.DayDataRequest
import com.dyentrack.app.domain.model.MasterDataStatic
import com.dyentrack.app.domain.model.enum.BottomSheetVisibleType
import com.dyentrack.app.domain.model.enum.ExerciceInPropertyType
import com.dyentrack.app.domain.model.enum.SaveType
import com.dyentrack.app.domain.model.exercise.Exercise
import com.dyentrack.app.domain.model.exercise.ExerciseInSeance
import com.dyentrack.app.domain.model.preset.ExerciceInPreset
import com.dyentrack.app.domain.model.preset.Preset
import com.dyentrack.app.domain.usecase.daydata.GetDayDataUseCase
import com.dyentrack.app.domain.usecase.infoExerciseUser.GetInfoExerciseUserUseCase
import com.dyentrack.app.domain.usecase.infoExerciseUser.UpdateInfoExerciseUserUseCase
import com.dyentrack.app.domain.usecase.masterdata.BuildMasterDataMapsUseCase
import com.dyentrack.app.domain.usecase.masterdata.GetMasterDataStaticUseCase
import com.dyentrack.app.domain.usecase.muscleRate.ComputePlannedExerciseMuscleRateUseCase
import com.dyentrack.app.domain.usecase.preset.GetPresetUseCase
import com.dyentrack.app.presentation.screens.exercise.ExerciseUiState
import com.dyentrack.app.presentation.screens.seance.viewModel.SeanceDuJour
import com.dyentrack.app.presentation.screens.seance.viewModel.SeanceUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PresetPageViewModel @Inject constructor(

    private val buildMasterDataMapsUseCase: BuildMasterDataMapsUseCase,
    getMasterDataStaticUseCase: GetMasterDataStaticUseCase,

    ) : ViewModel() {


    val masterData= getMasterDataStaticUseCase.masterDataFlow.stateIn(viewModelScope, started = SharingStarted.Lazily, initialValue = null)
    private val _uiState = MutableStateFlow(PresetUiState())
    val uiState: StateFlow<PresetUiState> = _uiState

    val languageUser = LocaleManager.getCurrentLanguage()


    init {
        viewModelScope.launch {
            val maps = buildMasterDataMapsUseCase.execute(languageUser)
            _uiState.update { it.copy(masterDataMaps = maps) }
        }
    }
    fun setPreset(preset: Preset) {
        _uiState.update { it.copy(currentPreset=preset) }
    }
    fun addExerciseToSeance() {
        val newExercises = _uiState.value.exerciseToAdd.map { exerciseId ->
            val existing  = _uiState.value.currentPreset?.presetSeanceExerciseVO?.find { it.idExercise == exerciseId }

            ExerciceInPreset(
                idExercise = exerciseId,
                nbSet = existing?.nbSet?:1,
                rangeRepInf = existing?.rangeRepInf,
                rangeRepSup = existing?.rangeRepSup ,
                idLateralite = existing?.idLateralite?:1L ,
                idEquipment = existing?.idEquipment ?:1L
            )
        }
        _uiState.update { state ->
            state.copy(
                currentPreset = state.currentPreset?.copy(
                    presetSeanceExerciseVO = newExercises
                ),
                exerciseToAdd = emptyList(),
                isBottomSheetVisible = BottomSheetVisibleType.NO
            )
        }
    }


    private fun updateExercise(
        exerciseId: Long,
        updater: (ExerciceInPreset) -> ExerciceInPreset
    ) {
        _uiState.update { state ->
            val preset = state.currentPreset ?: return@update state
            val updatedExercises = preset.presetSeanceExerciseVO?.map { ex ->
                if (ex.idExercise == exerciseId) updater(ex) else ex
            }
            state.copy(
                currentPreset = preset.copy(
                    presetSeanceExerciseVO = updatedExercises
                )
            )
        }
        Log.d("state","${_uiState.value.currentPreset}")
    }

    fun changeNbSet(exerciseId: Long, delta: Int) {
        updateExercise(exerciseId) { ex ->
            val newValue = ex.nbSet + delta
            ex.copy(nbSet = newValue.coerceIn(1, 20))
        }
    }








    fun toggleBottomSheet(show: BottomSheetVisibleType) {
        _uiState.update { it.copy(isBottomSheetVisible = show) }
    }

    fun toggleExerciseSelection(exerciseId: Long) {
        _uiState.update { state ->
            val updatedList = if (exerciseId in state.exerciseToAdd)
                state.exerciseToAdd - exerciseId
            else
                state.exerciseToAdd + exerciseId

            state.copy(exerciseToAdd = updatedList)
        }
    }

    fun updateExerciseRange(
        exerciseId: Long,
        rangeRepInf: Int? = null,
        rangeRepSup: Int? = null
    ) {
        Log.d("range","${rangeRepInf}_${rangeRepSup}")
        updateExercise(exerciseId) { ex ->
            val min = rangeRepInf?.coerceAtLeast(1)
            val max = rangeRepSup?.coerceAtLeast(1)

            val correctedMin = if (min != null && max != null && min > max) max else min
            val correctedMax = if (min != null && max != null && min > max) min else max

            ex.copy(
                rangeRepInf = correctedMin,
                rangeRepSup = correctedMax
            )

        }
    }

    fun setExerciseToAdd() {
        _uiState.update { state ->
            state.copy(
                exerciseToAdd = state.currentPreset?.presetSeanceExerciseVO
                    ?.map { it.idExercise }
                    .orEmpty()
            )
        }
    }

    fun updatePresetProperty(
        exerciseId: Long,
        property: ExerciceInPropertyType,
        valueId: Long
    ) {
        updateExercise(exerciseId) { ex ->
            when (property) {
                ExerciceInPropertyType.LATERALITE -> ex.copy(idLateralite = valueId)
                ExerciceInPropertyType.EQUIPMENT -> ex.copy(idEquipment = valueId)
            }
        }
    }
    fun expandExercise(exerciseId: Long?) {
        _uiState.update { it.copy(expandedExerciseId = exerciseId) }
    }
}