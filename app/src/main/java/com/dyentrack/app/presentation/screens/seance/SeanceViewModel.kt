package com.dyentrack.app.presentation.screens.seance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dyentrack.app.domain.model.DayDataRequest
import com.dyentrack.app.domain.model.MasterDataStatic
import com.dyentrack.app.domain.model.PlannedExerciseVO
import com.dyentrack.app.domain.model.SeanceTrack
import com.dyentrack.app.domain.model.SetData
import com.dyentrack.app.domain.model.SetVO
import com.dyentrack.app.domain.usecase.SaveDayDataUseCase
import com.dyentrack.app.domain.usecase.GetMasterDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SeanceViewModel @Inject constructor(private val getMasterDataUseCase: GetMasterDataUseCase,
                                          private val saveDayDataUseCase: SaveDayDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SeanceUiState())
    val uiState: StateFlow<SeanceUiState> = _uiState

    fun loadMasterData() {
        viewModelScope.launch {
            val data = getMasterDataUseCase()
            println("MasterData fetched: $data")
            if (data != null) _uiState.update { it.copy(masterDataStatic = data) }
        }
    }

    fun toggleBottomSheet(show: Boolean) {
        _uiState.update { it.copy(isBottomSheetVisible = show) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun toggleExerciseSelection(exerciseId: Long) {
        _uiState.update { state ->
            val newList = if (exerciseId in state.exerciseToAdd)
                state.exerciseToAdd - exerciseId
            else
                state.exerciseToAdd + exerciseId
            state.copy(exerciseToAdd = newList)
        }
    }

    fun addExerciseToSeance() {
        _uiState.update {
            it.copy(
                exerciseOfTheSeance = it.exerciseToAdd,
                exerciseToAdd = emptyList(),
                isBottomSheetVisible = false
            )
        }
    }
    fun setExerciseToAdd(exercises: List<Long>) {
        _uiState.update { currentState ->
            currentState.copy(exerciseToAdd = exercises)
        }
    }

    fun addSetToExercise(exerciseId: Long) {
        _uiState.update { state ->
            val updatedSets = state.setsMap.toMutableMap()
            val list = updatedSets.getOrPut(exerciseId) { mutableListOf() }.toMutableList()
            list.add(SetData(charge = null, rir = null, reps = null))
            updatedSets[exerciseId] = list
            state.copy(setsMap = updatedSets)
        }
    }

    fun expandExercise(exerciseId: Long?) {
        _uiState.update { it.copy(expandedExerciseId = exerciseId) }
    }


    fun saveToAPISeance(){
        val today = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
        val plannedExerciseVO= _uiState.value.exerciseOfTheSeance.mapIndexed { index, exerciseId ->
            val set = _uiState.value.setsMap[exerciseId]?.mapIndexed { index,setData ->
                SetVO((index),setData.reps ?:0,setData.rir?:0,setData.charge?:0.0,setData.type)
            }?:listOf()

            PlannedExerciseVO(index,exerciseId,1L,1L,set)

        }

        val request = DayDataRequest(dayData = today, seanceTrack = SeanceTrack(null,null,plannedExerciseVOs =plannedExerciseVO))
        viewModelScope.launch {
            try {
                val response = saveDayDataUseCase(request)
            } catch (e: Exception) {
                println("Exception API : ${e.message}")
            }
        }
    }

}