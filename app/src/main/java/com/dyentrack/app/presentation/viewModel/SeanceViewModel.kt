package com.dyentrack.app.presentation.viewModel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.BackoffPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.dyentrack.app.data.worker.SaveSeanceWorker
import com.dyentrack.app.domain.model.DayDataRequest
import com.dyentrack.app.domain.model.DayInfo
import com.dyentrack.app.domain.model.PlannedExerciseVO
import com.dyentrack.app.domain.model.SeanceTrack
import com.dyentrack.app.domain.model.SetData
import com.dyentrack.app.domain.model.SetSide
import com.dyentrack.app.domain.model.SetType
import com.dyentrack.app.domain.model.SetVO
import com.dyentrack.app.domain.model.exercise.ExerciseInSeance
import com.dyentrack.app.domain.usecase.GetLast30DaysUseCase
import com.dyentrack.app.domain.usecase.daydata.GetDayDataUseCase
import com.dyentrack.app.presentation.screens.seance.ExerciseMode
import com.dyentrack.app.presentation.screens.seance.SeanceDuJour
import com.dyentrack.app.presentation.screens.seance.SeanceUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class SeanceViewModel @Inject constructor(
    getLast30DaysUseCase: GetLast30DaysUseCase,
    private val getDayDataUseCase: GetDayDataUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(SeanceUiState())
    val uiState: StateFlow<SeanceUiState> = _uiState
    val days: SnapshotStateList<DayInfo> = mutableStateListOf()

    init {
        days.addAll(getLast30DaysUseCase())
        getSaveToApiSeance()
    }

    fun toggleMode(newMode: ExerciseMode) {
        _uiState.update { current ->
            if (current.exerciseMode == newMode) {
                current.copy(exerciseMode = ExerciseMode.NORMAL)
            } else {
                if (newMode == ExerciseMode.CHANGE_ORDER) {
                    current.copy(exerciseMode = newMode, expandedExerciseId = null)
                } else {
                    current.copy(exerciseMode = newMode)
                }
            }
        }
    }


    fun swapExercise(day: String, fromIndex: Int, toIndex: Int) {
        val seance = _uiState.value.seancesParJour[day] ?: return
        val exercises = seance.exercisesInSceance
        if (fromIndex !in exercises.indices || toIndex !in exercises.indices) return
        exercises.apply {
            val temp = this[fromIndex]
            this[fromIndex] = this[toIndex]
            this[toIndex] = temp
        }
        _uiState.value.seancesParJour[day] = seance.copy(
            exercisesInSceance = exercises
        )

        _uiState.value.seancesParJour[day] = seance.copy(saveLocal = false, saveAPI = false)
    }

    fun updateLaterite(exerciseId: Long, lateriteId: Long) {
        val day = _uiState.value.daySelected
        val seance = _uiState.value.seancesParJour[day] ?: return

        val index = seance.exercisesInSceance.indexOfFirst { it.exerciseId == exerciseId }
        if (index != -1) {
            seance.exercisesInSceance[index] =
                seance.exercisesInSceance[index].copy(laterite = lateriteId)
        }

        val sets = seance.setsMap[exerciseId] ?: return
        if (lateriteId == 2L) {
            sets.forEach { set ->
                if (set.side != SetSide.LEFT) set.side = SetSide.LEFT
                if (set.linkedRightSet == null) {
                    set.linkedRightSet = SetData(
                        type = set.type,
                        charge = set.charge,
                        reps = set.reps,
                        rir = set.rir,
                        side = SetSide.RIGHT
                    )
                }
            }
        } else { // bilatéral ou autre
            sets.forEach { set ->
                set.linkedRightSet = null
                set.side = SetSide.BOTH
            }
        }
        changeSaveStatus(false)
    }

    fun updateEquipment(exerciseId: Long, equipmentId: Long) {
        val day = _uiState.value.daySelected
        val seance = _uiState.value.seancesParJour[day] ?: return

        val index = seance.exercisesInSceance.indexOfFirst { it.exerciseId == exerciseId }
        if (index != -1) {
            seance.exercisesInSceance[index] =
                seance.exercisesInSceance[index].copy(equipment = equipmentId)
        }
        changeSaveStatus(false)
    }

    fun changeDaySelected(dateFormat: String) {
        _uiState.update {
            it.copy(
                daySelected = dateFormat,
                showAnalyseData = false,
                expandedExerciseId = null
            )
        }
    }

    fun toggleBottomSheet(show: Boolean) {
        _uiState.update { it.copy(isBottomSheetVisible = show) }
    }

    fun toggleShowAnalyseData(show: Boolean? = null) {
        _uiState.update { currentState ->
            currentState.copy(
                showAnalyseData = show ?: !currentState.showAnalyseData
            )
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun toggleExerciseSelection(exerciseId: Long) {
        val exercise = getExerciseToAdd()
        val exerciseList = if (exerciseId in exercise) {
            exercise - exerciseId
        } else {
            exercise + exerciseId
        }
        _uiState.update {
            it.copy(
                exerciseToAdd = exerciseList
            )
        }

    }


    private fun getOrCreateSeanceOfADay(): SeanceDuJour {
        return _uiState.value.seancesParJour.getOrPut(_uiState.value.daySelected) { SeanceDuJour() }
    }


    fun addExerciseToSeance() {
        val day = _uiState.value.daySelected
        val todaySeance = _uiState.value.seancesParJour.getOrPut(day) { SeanceDuJour() }

        todaySeance.exercisesInSceance.clear()
        val newExercises = _uiState.value.exerciseToAdd.map { exerciseId ->
            ExerciseInSeance(exerciseId = exerciseId, 1L, 1L)
        }

        todaySeance.exercisesInSceance.addAll(newExercises)

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

    fun deleteExercice(exerciseId: Long) {
        val day = _uiState.value.daySelected
        val seance = _uiState.value.seancesParJour[day] ?: return
        seance.exercisesInSceance.removeAll { it.exerciseId == exerciseId }

        seance.setsMap.remove(exerciseId)

        changeSaveStatus(false)
    }

    fun addSetToExercise(exerciseId: Long, lateraliteName: String) {
        val seance = getOrCreateSeanceOfADay()

        val sets = seance.setsMap.getOrPut(exerciseId) { mutableStateListOf() }

        val lastCharge = sets.lastOrNull()?.charge
        val lastRir = sets.lastOrNull()?.rir
        val set = SetData(
            type = SetType.EFFECTIVE,
            charge = lastCharge,
            reps = null,
            rir = lastRir,
            side = if (lateraliteName == "Unilatéral") SetSide.LEFT else SetSide.BOTH

        )

        if (lateraliteName == "Unilatéral") {
            val setright =
                SetData(
                    type = SetType.EFFECTIVE,
                    charge = lastCharge,
                    reps = null,
                    rir = null,
                    side = SetSide.RIGHT

                )
            set.linkedRightSet = setright
        }
        sets.add(set)


        changeSaveStatus(false)
    }

    fun getLastExercicePerfBetweenDate(dateRecent: String, dateBefore: String) {
        val result = mutableMapOf<String, MutableList<SetData>>()
        generateSequence(LocalDate.parse(dateRecent)) { it.minusDays(1) }
            .takeWhile { !it.isBefore(LocalDate.parse(dateBefore)) }
            .forEach { day ->
                val dataOfTheDay =
                    uiState.value.seancesParJour[day.format(DateTimeFormatter.ISO_DATE)]
                if (dataOfTheDay?.setsMap[uiState.value.expandedExerciseId]?.isNotEmpty() == true) {
                    result.getOrPut(day.toString()) { mutableListOf() }
                        .addAll(dataOfTheDay.setsMap[uiState.value.expandedExerciseId]!!.toList())

                }
            }
        Log.e("Log Info", "info: $result")
        _uiState.value = _uiState.value.copy(analyseData = result)
    }

    fun swapSet(exerciseId: Long, fromIndex: Int, toIndex: Int) {
        val seance = _uiState.value.seancesParJour[_uiState.value.daySelected] ?: return
        val sets = seance.setsMap[exerciseId] ?: return

        if (fromIndex !in sets.indices || toIndex !in sets.indices) return

        sets[fromIndex] = sets.set(toIndex, sets[fromIndex])
        changeSaveStatus(false)
    }

    fun expandExercise(exerciseId: Long?) {
        _uiState.update { it.copy(expandedExerciseId = exerciseId, showAnalyseData = false) }
    }

    fun getExerciseToAdd(): List<Long> {
        return _uiState.value.exerciseToAdd
    }

    private var autoSaveJob: kotlinx.coroutines.Job? = null
    fun changeSaveStatus(status: Boolean = false) {
        val day = _uiState.value.daySelected
        val seance = _uiState.value.seancesParJour[day] ?: return
        _uiState.value.seancesParJour[day] = seance.copy(saveAPI = status)
        if (!status) {
            autoSaveJob?.cancel()
            autoSaveJob = viewModelScope.launch {
                kotlinx.coroutines.delay(15_000L)
                autoSaveSeanceIfNeeded()
            }
        }
    }

    fun autoSaveSeanceIfNeeded() {
        val day = _uiState.value.daySelected
        val seance = _uiState.value.seancesParJour[day] ?: return
        if (seance.saveAPI) return

        val request = buildDayDataRequest(day, seance)

        val json = Json.encodeToString(DayDataRequest.serializer(), request)

        val workRequest = OneTimeWorkRequestBuilder<SaveSeanceWorker>()
            .setInputData(workDataOf("payload" to json))
            .setBackoffCriteria(BackoffPolicy.LINEAR, 15, TimeUnit.SECONDS)
            .build()

        val workManager = WorkManager.getInstance(context)

        workManager.enqueueUniqueWork(
            "auto_save_seance_$day",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )

        workManager.getWorkInfoByIdLiveData(workRequest.id).observeForever { workInfo ->
            if (workInfo != null && workInfo.state.isFinished) {
                if (workInfo.state == androidx.work.WorkInfo.State.SUCCEEDED) {
                    changeSaveStatus(true)
                }
            }
        }

    }


    fun getSaveToApiSeance() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingRequest = true) }
            try {
                val response = getDayDataUseCase()

                if (response.status == 200 || response.status == 201 || response.status == 100) {
                    // Créer un nouveau mapComp global
                    val newMap = _uiState.value.seancesParJour.toMutableMap()

                    response.data.forEach { daySeance ->
                        val day = daySeance.dayData
                        val oldDay = newMap.getOrPut(day) { SeanceDuJour() }

                        val exercisesInSeance =
                            daySeance.seanceTrack.plannedExercises.map { plannedExercise ->
                                ExerciseInSeance(
                                    exerciseId = plannedExercise.exerciseId,
                                    laterite = plannedExercise.lateraliteId,
                                    equipment = plannedExercise.equipmentId
                                )
                            }.toMutableStateList()

                        val setsMap = mutableStateMapOf<Long, SnapshotStateList<SetData>>()
                        daySeance.seanceTrack.plannedExercises.forEach { plannedExercise ->
                            val sets = plannedExercise.sets?.mapNotNull { setVO ->
                                when (setVO.side) {
                                    SetSide.BOTH -> {
                                        SetData(
                                            type = setVO.typeOfSet,
                                            charge = setVO.charge,
                                            rir = setVO.rir,
                                            reps = setVO.repsNumber,
                                            side = SetSide.BOTH
                                        )
                                    }
                                    SetSide.LEFT -> {
                                        val rightSetVO =
                                            plannedExercise.sets.firstOrNull { it.setOrder == setVO.setOrder && it.side == SetSide.RIGHT }
                                        SetData(
                                            type = setVO.typeOfSet,
                                            charge = setVO.charge,
                                            rir = setVO.rir,
                                            reps = setVO.repsNumber,
                                            side = SetSide.LEFT
                                        ).apply {
                                            linkedRightSet = rightSetVO?.let {
                                                SetData(
                                                    it.typeOfSet,
                                                    it.charge,
                                                    it.rir,
                                                    it.repsNumber,
                                                    SetSide.RIGHT
                                                )
                                            }
                                        }
                                    }
                                    else -> {
                                        null
                                    }
                                }
                            }?.toMutableStateList() ?: mutableStateListOf()

                            setsMap[plannedExercise.exerciseId] = sets
                        }

                        _uiState.value.seancesParJour[day] = oldDay.copy(
                            saveAPI = response.status != 100,
                            exercisesInSceance = exercisesInSeance,
                            setsMap = setsMap
                        )
                    }

                    Log.d("DEBUG_API", "Jour reçu: ${_uiState.value.seancesParJour}")
                } else {
                    Log.d("DEBUG_API", "Error Not 200 201: $response")
                }
            } catch (e: HttpException) {
                Log.e("API", "Erreur serveur : ${e.code()} - ${e.message()}")
            } catch (e: Exception) {
                Log.e("API", "Autre erreur : ", e)
            } finally {
                _uiState.update { it.copy(isLoadingRequest = false) }
            }
        }
    }

    private fun buildDayDataRequest(day: String, seance: SeanceDuJour): DayDataRequest {
        val plannedExercises = seance.exercisesInSceance.mapIndexed { exIndex, exercise ->
            val sets = seance.setsMap[exercise.exerciseId]?.flatMapIndexed { setIndex, setData ->
                val leftSet = SetVO(setIndex, setData.reps ?: 0, setData.rir ?: 0,
                    setData.charge ?: 0.0, setData.type, setData.side)
                val rightSet = setData.linkedRightSet?.let {
                    SetVO(setIndex, it.reps ?: 0, it.rir ?: 0, it.charge ?: 0.0, it.type, it.side)
                }
                listOf(leftSet) + listOfNotNull(rightSet)
            } ?: emptyList()

            PlannedExerciseVO(null, exIndex, exercise.exerciseId,
                exercise.laterite ?: 1L, exercise.equipment ?: 1L, sets)
        }

        return DayDataRequest(
            idDayData = null,
            dayData = day,
            seanceTrack = SeanceTrack(null, null, plannedExercises),
            null,null
        )
    }



}