package com.dyentrack.app.presentation.screens.seance.viewModel

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateSet
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.BackoffPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.dyentrack.app.data.worker.SaveSeanceWorker
import com.dyentrack.app.domain.model.DayDataRequest
import com.dyentrack.app.domain.model.DayInfo
import com.dyentrack.app.domain.model.InfoExerciseUser
import com.dyentrack.app.domain.model.MasterDataStatic
import com.dyentrack.app.domain.model.NutritionTrack
import com.dyentrack.app.domain.model.PhysioTrack
import com.dyentrack.app.domain.model.PlannedExerciseVO
import com.dyentrack.app.domain.model.SeanceTrack
import com.dyentrack.app.domain.model.SetData
import com.dyentrack.app.domain.model.SetSide
import com.dyentrack.app.domain.model.SetType
import com.dyentrack.app.domain.model.SetVO
import com.dyentrack.app.domain.model.exercise.ExerciseInSeance
import com.dyentrack.app.domain.usecase.GetDaysRangeUseCase
import com.dyentrack.app.domain.usecase.GetMasterDataStaticUseCase
import com.dyentrack.app.domain.usecase.daydata.GetDayDataUseCase
import com.dyentrack.app.domain.usecase.daydata.UpdateDayDataToLocalUseCase
import com.dyentrack.app.domain.usecase.infoExerciseUser.GetInfoExerciseUserUseCase
import com.dyentrack.app.domain.usecase.infoExerciseUser.UpdateInfoExerciseUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class SeanceViewModel @Inject constructor(
    getDaysRangeUseCase: GetDaysRangeUseCase,
    @ApplicationContext private val context: Context,
    getDayDataUseCase: GetDayDataUseCase,
    getMasterDataStaticUseCase: GetMasterDataStaticUseCase,
    getInfoExerciseUserUseCase: GetInfoExerciseUserUseCase,
    private val updateInfoExerciseUserUseCase: UpdateInfoExerciseUserUseCase,
    private val updateDayDataToLocalUseCase: UpdateDayDataToLocalUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SeanceUiState())
    val uiState: StateFlow<SeanceUiState> = _uiState
    val days: SnapshotStateSet<DayInfo> = mutableStateSetOf()
    var masterData: MasterDataStatic? = null
    val dayDataFlow: StateFlow<List<DayDataRequest>?> = getDayDataUseCase.dayDataFlow
    val infoExerciseUserFlow =getInfoExerciseUserUseCase.infoExerciseUserFlow

    init {
        days.addAll(getDaysRangeUseCase.getLast30Days())
        viewModelScope.launch {
            masterData = getMasterDataStaticUseCase()
            dayDataFlow.collect { dayDataList ->
                if (dayDataList == null) {
                    return@collect
                }
                val minDayDataDate = dayDataList.minOfOrNull { LocalDate.parse(it.dayData) }
                if (minDayDataDate != null) {
                    val additionalDays = getDaysRangeUseCase.extendDaysIfNeeded(days, minDayDataDate)
                    if (additionalDays.isNotEmpty()) {
                        days.addAll( additionalDays)
                    }
                    Log.d("days:",days.toString())
                }
                val newMap = mutableStateMapOf<String, SeanceDuJour>()
                dayDataList.forEach { dayData ->
                    newMap[dayData.dayData] = dayData.toSeanceDuJour()
                }
                _uiState.update { current ->
                    current.copy(seancesParJour = newMap)
                }
            }
        }

    }

    fun setDaySelected(daySelected:String){
        _uiState.update { current-> current.copy(daySelected=daySelected) }
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

        changeSaveStatus(SaveType.NOTSAVE)
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
        changeSaveStatus(SaveType.NOTSAVE)
    }

    fun updateEquipment(exerciseId: Long, equipmentId: Long) {
        val day = _uiState.value.daySelected
        val seance = _uiState.value.seancesParJour[day] ?: return

        val index = seance.exercisesInSceance.indexOfFirst { it.exerciseId == exerciseId }
        if (index != -1) {
            seance.exercisesInSceance[index] =
                seance.exercisesInSceance[index].copy(equipment = equipmentId)
        }
        changeSaveStatus(SaveType.NOTSAVE)
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

        val newExercises = _uiState.value.exerciseToAdd.map { exerciseId ->
            val exerciseAlreadyHave = todaySeance.exercisesInSceance.find { it.exerciseId== exerciseId}
            ExerciseInSeance(exerciseId = exerciseId, exerciseAlreadyHave?.laterite ?:1L, exerciseAlreadyHave?.equipment ?:1L)
        }
        todaySeance.exercisesInSceance.clear()

        todaySeance.exercisesInSceance.addAll(newExercises)

        _uiState.update {
            it.copy(
                exerciseToAdd = emptyList(),
                isBottomSheetVisible = false
            )
        }
        changeSaveStatus(SaveType.NOTSAVE)
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

        changeSaveStatus(SaveType.NOTSAVE)
    }

    fun addSetToExercise(exerciseId: Long, lateraliteName: String) {
        val seance = getOrCreateSeanceOfADay()

        val sets = seance.setsMap.getOrPut(exerciseId) { mutableStateListOf() }

        val lastCharge = sets.lastOrNull()?.charge
        val lastRir = sets.lastOrNull()?.rir?:1
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


        changeSaveStatus(SaveType.NOTSAVE)
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
        changeSaveStatus(SaveType.NOTSAVE)
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun deleteLastSet(exerciseId: Long){
        val seance = _uiState.value.seancesParJour[_uiState.value.daySelected] ?: return
        val sets = seance.setsMap[exerciseId] ?: return
        sets.removeLast()
        changeSaveStatus(SaveType.NOTSAVE)



    }

    fun expandExercise(exerciseId: Long?) {
        _uiState.update { it.copy(expandedExerciseId = exerciseId, showAnalyseData = false) }
    }

    fun getExerciseToAdd(): List<Long> {
        return _uiState.value.exerciseToAdd
    }

    fun changeSection(sectionSelectType: SectionSelectType){
        _uiState.update { it.copy( sectionSelect = sectionSelectType) }
    }


    // +++++++++++ Nutrition
    fun updateNutrition(
        field: NutritionField,
        value: Int
    ) {
        val day = _uiState.value.daySelected
        val todaySeance = _uiState.value.seancesParJour.getOrPut(day) { SeanceDuJour() }
        val currentTrack = todaySeance.nutritionTrack

        val newNutritionTrack = when (field) {
            NutritionField.CALORIES -> currentTrack.copy(calories = value)
            NutritionField.PROTEINS -> currentTrack.copy(proteins = value)
            NutritionField.LIPIDS -> currentTrack.copy(lipids = value)
            NutritionField.CARBOHYDRATES -> currentTrack.copy(carbohydrates = value)
            NutritionField.FIBER -> currentTrack.copy(fiber = value)
            NutritionField.CAFEINS -> currentTrack.copy(cafeins = value)
        }

        _uiState.value.seancesParJour[day] = todaySeance.copy(
            nutritionTrack = newNutritionTrack
        )

        changeSaveStatus(SaveType.NOTSAVE)
    }
    // +++++++++++ Physio

    fun updatePhysio(
        field: PhysioField,
        value: Any
    ) {
        val day = _uiState.value.daySelected
        val todaySeance = _uiState.value.seancesParJour.getOrPut(day) { SeanceDuJour() }
        val currentTrack = todaySeance.physioTrack

        val newPhysioTrack = when (field) {
            PhysioField.WEIGHT -> currentTrack.copy(weight = value as? Double ?: currentTrack.weight)
            PhysioField.STEP -> currentTrack.copy(step = value as? Int ?: currentTrack.step)
            PhysioField.HOUR_OF_SLEEP -> currentTrack.copy(hourOfSleep = value as? Double ?: currentTrack.hourOfSleep)
            PhysioField.MOOD -> currentTrack.copy(mood = value as? String ?: currentTrack.mood)
        }

        _uiState.value.seancesParJour[day] = todaySeance.copy(
            physioTrack = newPhysioTrack
        )

        changeSaveStatus(SaveType.NOTSAVE)
    }




    private val autoSaveJobs = mutableMapOf<String, Job>()

    fun changeSaveStatus(status: SaveType) {
        val day = _uiState.value.daySelected
        val seance = _uiState.value.seancesParJour[day] ?: return
        _uiState.value.seancesParJour[day] = seance.copy(saveAPI = status)
        if (status == SaveType.NOTSAVE||status == SaveType.LocalSAVE) {
            if (status != SaveType.LocalSAVE) {
                viewModelScope.launch {
                    updateDayDataToLocalUseCase(buildDayDataRequest(day, seance))
                    _uiState.value.seancesParJour[day] = seance.copy(saveAPI = SaveType.LocalSAVE)
                }
            }
            autoSaveJobs[day]?.cancel()
            val seanceForAutoSave = seance.copy() // capture immuable pour le Worker
            autoSaveJobs[day] = viewModelScope.launch {
                delay(5_000L)
                autoSaveSeanceIfNeeded(day, seanceForAutoSave)
            }
        }
    }

    fun autoSaveSeanceIfNeeded(day: String, seance: SeanceDuJour) {
        val currentDayData = buildDayDataRequest(day, seance)


        val json = Json.Default.encodeToString(DayDataRequest.serializer(), currentDayData)

        val workRequest = OneTimeWorkRequestBuilder<SaveSeanceWorker>()
            .setInputData(workDataOf("payload" to json))
            .setBackoffCriteria(BackoffPolicy.LINEAR, 8, TimeUnit.SECONDS)
            .build()

        val workManager = WorkManager.Companion.getInstance(context)
        val workName = "auto_save_seance_$day"
        workManager.enqueueUniqueWork(
            workName,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )

        workManager.getWorkInfoByIdLiveData(workRequest.id).observeForever { workInfo ->
            if (workInfo != null &&   workInfo.state == WorkInfo.State.SUCCEEDED) {

                    _uiState.value.seancesParJour[day] = seance.copy(saveAPI = SaveType.APISAVE)

            }
        }

    }


    private fun buildDayDataRequest(day: String, seance: SeanceDuJour): DayDataRequest {
        val plannedExercises = seance.exercisesInSceance.mapIndexed { exIndex, exercise ->
            val sets = seance.setsMap[exercise.exerciseId]?.flatMapIndexed { setIndex, setData ->
                val leftSet = SetVO(
                    setIndex, setData.reps ?: 0, setData.rir ?: 0,
                    setData.charge ?: 0.0, setData.type, setData.side
                )
                val rightSet = setData.linkedRightSet?.let {
                    SetVO(setIndex, it.reps ?: 0, it.rir ?: 0, it.charge ?: 0.0, it.type, it.side)
                }
                listOf(leftSet) + listOfNotNull(rightSet)
            } ?: emptyList()

            PlannedExerciseVO(
                null, exIndex, exercise.exerciseId,
                exercise.laterite, exercise.equipment , sets
            )
        }

        return DayDataRequest(
            idDayData = null,
            dayData = day,
            physioTrack = seance.physioTrack,
            seanceTrack = SeanceTrack(null, null, plannedExercises),
            nutritionTrack = seance.nutritionTrack
        )
    }

    private fun DayDataRequest.toSeanceDuJour(): SeanceDuJour {
        val exercises = seanceTrack.plannedExercises.map { plannedExercise ->
            ExerciseInSeance(
                exerciseId = plannedExercise.exerciseId,
                laterite = plannedExercise.lateraliteId,
                equipment = plannedExercise.equipmentId
            )
        }.toMutableStateList()

        val setsMap = mutableStateMapOf<Long, SnapshotStateList<SetData>>()
        seanceTrack.plannedExercises.forEach { plannedExercise ->
            val sets = plannedExercise.sets?.mapNotNull { setVO ->
                when (setVO.side) {
                    SetSide.BOTH -> SetData(
                        type = setVO.typeOfSet,
                        charge = setVO.charge,
                        reps = setVO.repsNumber,
                        rir = setVO.rir,
                        side = SetSide.BOTH
                    )
                    SetSide.LEFT -> {
                        val rightSetVO = plannedExercise.sets.firstOrNull { it.setOrder == setVO.setOrder && it.side == SetSide.RIGHT }
                        SetData(
                            type = setVO.typeOfSet,
                            charge = setVO.charge,
                            reps = setVO.repsNumber,
                            rir = setVO.rir,
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
                    else -> null
                }
            }?.toMutableStateList() ?: mutableStateListOf()

            setsMap[plannedExercise.exerciseId] = sets
        }

        return SeanceDuJour(
            exercisesInSceance = exercises,
            nutritionTrack = nutritionTrack?: NutritionTrack(),
            physioTrack = physioTrack ?: PhysioTrack(),
            setsMap = setsMap,
            saveAPI = if (dayDataFlow.value?.any { it.dayData == dayData } == true) SaveType.APISAVE else SaveType.NOTSAVE
        )
    }




}