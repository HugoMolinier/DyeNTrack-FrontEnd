package com.dyentrack.app.presentation.screens.seance

import android.net.http.HttpException
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dyentrack.app.domain.model.DayDataRequest
import com.dyentrack.app.domain.model.DayInfo
import com.dyentrack.app.domain.model.MasterDataStatic
import com.dyentrack.app.domain.model.PlannedExerciseVO
import com.dyentrack.app.domain.model.SeanceTrack
import com.dyentrack.app.domain.model.SetData
import com.dyentrack.app.domain.model.SetType
import com.dyentrack.app.domain.model.SetVO
import com.dyentrack.app.domain.usecase.GetDayDataUseCase
import com.dyentrack.app.domain.usecase.GetLast30DaysUseCase
import com.dyentrack.app.domain.usecase.GetMasterDataStaticUseCase
import com.dyentrack.app.domain.usecase.SaveDayDataUseCase
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
import kotlin.collections.emptyMap

@HiltViewModel
class SeanceViewModel @Inject constructor(private val getMasterDataUseCase: GetMasterDataStaticUseCase,
                                          private val saveDayDataUseCase: SaveDayDataUseCase,
    private val getLast30DaysUseCase: GetLast30DaysUseCase,
    private val getDayDataUseCase: GetDayDataUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SeanceUiState())
    val uiState: StateFlow<SeanceUiState> = _uiState
    val days: SnapshotStateList<DayInfo> = mutableStateListOf()

    init {
        days.addAll(getLast30DaysUseCase()) // exécute le use case et ajoute à la liste
        getSaveToApiSeance()
    }
    fun loadMasterData() {
        viewModelScope.launch {
            val data = getMasterDataUseCase()
            println("MasterData fetched: $data")
            if (data != null) _uiState.update { it.copy(masterDataStatic = data) }
        }
    }

    fun changeDaySelected(dateFormat: String){
        _uiState.update { it.copy(daySelected=dateFormat) }
    }
    fun toggleBottomSheet(show: Boolean) {
        _uiState.update { it.copy(isBottomSheetVisible = show) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun toggleExerciseSelection(exerciseId: Long) {
            val exercise = getExerciseToAdd()
            val exerciseList= if (exerciseId in exercise) {
                exercise - exerciseId
            }else {
                exercise + exerciseId
            }
        _uiState.update {
            it.copy(
                exerciseToAdd = exerciseList)}

    }


    private fun getOrCreateSeanceOfADay(): SeanceDuJour {
        return _uiState.value.seancesParJour[_uiState.value.daySelected]?: SeanceDuJour()
    }
    private fun getOrCreateSetOfSeance(exerciseId: Long): SnapshotStateList<SetData> {
        return getOrCreateSeanceOfADay().setsMap.getOrPut(exerciseId) { SnapshotStateList() }
    }


    fun addExerciseToSeance() {
        val todaySeance = _uiState.value.seancesParJour.getOrPut(_uiState.value.daySelected) { SeanceDuJour() }
        todaySeance.exerciseIds.clear()
        todaySeance.exerciseIds.addAll(_uiState.value.exerciseToAdd)

        _uiState.update {
            it.copy(
                exerciseToAdd = emptyList(),
                isBottomSheetVisible = false
            )
        }
        changeSaveStatus(false)
    }
    fun setExerciseToAdd(exercises: List<Long>) {
        _uiState.update { currentState ->
            currentState.copy(exerciseToAdd = exercises)
        }
    }

    fun addSetToExercise(exerciseId: Long) {
        val seance = getOrCreateSeanceOfADay()
        val sets = getOrCreateSetOfSeance(exerciseId)
        sets.add(SetData(SetType.EFFECTIVE, null, null, null))
        _uiState.value.seancesParJour[_uiState.value.daySelected] = seance
        changeSaveStatus(false)
    }


    fun expandExercise(exerciseId: Long?) {
        _uiState.update { it.copy(expandedExerciseId = exerciseId) }
    }
    fun getExerciseToAdd(): List<Long>{
        return _uiState.value.exerciseToAdd
    }

    fun changeSaveStatus(status: Boolean=false){
        _uiState.update { currentState ->
            val day = currentState.daySelected
            val oldDay = currentState.seancesParJour[day]!!
            val mapComp = currentState.seancesParJour.toMutableMap();
            mapComp[day] = oldDay.copy(save = status)
            currentState.copy(seancesParJour = mapComp)
        }
    }

    fun saveToAPISeance(){
        val seanceDuJour = _uiState.value.seancesParJour.get(_uiState.value.daySelected)
        val plannedExerciseVO= seanceDuJour?.exerciseIds?.mapIndexed { index, exerciseId ->
            val set = seanceDuJour.setsMap[exerciseId]?.mapIndexed { index,setData ->
                SetVO((index),setData.reps ?:0,setData.rir?:0,setData.charge?:0.0,setData.type)
            }?:listOf()

            PlannedExerciseVO(null,index,exerciseId,1L,1L,set)

        }?: listOf()

        val request = DayDataRequest(idDayData = null, dayData = _uiState.value.daySelected, seanceTrack = SeanceTrack(null,null,plannedExercises =plannedExerciseVO),null,null)
        viewModelScope.launch {
            try {
                val response = saveDayDataUseCase(request)
                if (response.status == 200 || response.status == 201) {
                    changeSaveStatus(true)
                }else{
                    Log.d("DEBUG_API", "not 200 201: ${response}")
                }
            } catch (e: Exception) {
                println("Exception API : ${e.message}")
            }
        }
    }
    fun getSaveToApiSeance() {
        viewModelScope.launch {
            try {
                val response = getDayDataUseCase()
                Log.d("DEBUG_API", "Response: ${response}")
                if (response.status == 200 || response.status == 201) {
                    // Créer un nouveau mapComp global
                    val newMap = _uiState.value.seancesParJour.toMutableMap()

                    response.data.forEach { daySeance ->
                        val day = daySeance.dayData
                        val oldDay = newMap.getOrPut(day) { SeanceDuJour() }

                        val newExerciseIds = daySeance.seanceTrack?.plannedExercises
                            ?.map { it.exerciseId }
                            ?.toMutableStateList() ?: mutableStateListOf()

                        val setsMap: MutableMap<Long, SnapshotStateList<SetData>> = mutableStateMapOf()
                        daySeance.seanceTrack?.plannedExercises?.forEach { plannedExercise ->
                            val setsList = plannedExercise.sets?.map { setDTO ->
                                SetData(
                                    reps = setDTO.repsNumber,
                                    rir = setDTO.rir,
                                    charge = setDTO.charge,
                                    type = when(setDTO.typeOfSet) {
                                        SetType.WARMUP -> SetType.WARMUP
                                        SetType.TEMPO -> SetType.TEMPO
                                        else -> SetType.EFFECTIVE
                                    }
                                )
                            }?.toMutableStateList()
                            setsMap[plannedExercise.exerciseId] = setsList?: mutableStateListOf()
                        }

                        newMap[day] = oldDay.copy(
                            save = true,
                            exerciseIds = newExerciseIds,
                            setsMap = setsMap
                        )
                    }

                    // Un seul update global
                    _uiState.update { it.copy(seancesParJour = newMap) }
                    Log.d("DEBUG_API", "Jour reçu: ${_uiState.value.seancesParJour}")
                }
                else {
                    Log.d("DEBUG_API", "Error Not 200 201: ${response}")
                }
            } catch (e: retrofit2.HttpException) {
                Log.e("API", "Erreur serveur : ${e.code()} - ${e.message()}")
            } catch (e: Exception) {
                Log.e("API", "Autre erreur : ",e)
            }
        }
    }


}