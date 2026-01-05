package com.dyentrack.app.presentation.screens.seance.viewModel

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.snapshots.SnapshotStateSet
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.util.fastRoundToInt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.BackoffPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.dyentrack.app.core.locale.LocaleManager
import com.dyentrack.app.data.worker.SaveSeanceWorker
import com.dyentrack.app.domain.model.DayDataRequest
import com.dyentrack.app.domain.model.DayInfo
import com.dyentrack.app.domain.model.InfoExerciseUser
import com.dyentrack.app.domain.model.MasterDataStatic
import com.dyentrack.app.domain.model.MesureUnit
import com.dyentrack.app.domain.model.SetData
import com.dyentrack.app.domain.model.SetField
import com.dyentrack.app.domain.model.SetSide
import com.dyentrack.app.domain.model.SetType
import com.dyentrack.app.domain.model.TypeOfAction
import com.dyentrack.app.domain.model.enum.BottomSheetVisibleType
import com.dyentrack.app.domain.model.enum.NutritionField
import com.dyentrack.app.domain.model.enum.PhysioField
import com.dyentrack.app.domain.model.enum.SaveType
import com.dyentrack.app.domain.model.enum.SectionSelectType
import com.dyentrack.app.domain.model.exercise.ExerciseInSeance
import com.dyentrack.app.domain.model.getName
import com.dyentrack.app.domain.usecase.GetDaysRangeUseCase
import com.dyentrack.app.domain.usecase.GetLastExercisePerfBetweenDateUseCase
import com.dyentrack.app.domain.usecase.daydata.GetDayDataUseCase
import com.dyentrack.app.domain.usecase.daydata.UpdateDayDataToLocalUseCase
import com.dyentrack.app.domain.usecase.infoExerciseUser.GetInfoExerciseUserUseCase
import com.dyentrack.app.domain.usecase.masterdata.BuildMasterDataMapsUseCase
import com.dyentrack.app.domain.usecase.masterdata.GetMasterDataStaticUseCase
import com.dyentrack.app.presentation.components.SeanceSummary
import com.dyentrack.app.presentation.mapper.toDayDataRequest
import com.dyentrack.app.presentation.mapper.toSeanceDuJour
import com.dyentrack.app.presentation.utils.convertWeight
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class SeanceViewModel @Inject constructor(
    getDaysRangeUseCase: GetDaysRangeUseCase,
    private val getLastExercisePerfBetweenDateUseCase: GetLastExercisePerfBetweenDateUseCase,
    @ApplicationContext private val context: Context,
    getDayDataUseCase: GetDayDataUseCase,
    getMasterDataStaticUseCase: GetMasterDataStaticUseCase,
    private val buildMasterDataMapsUseCase: BuildMasterDataMapsUseCase,
    private val updateDayDataToLocalUseCase: UpdateDayDataToLocalUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SeanceUiState())
    val uiState: StateFlow<SeanceUiState> = _uiState
    val days: SnapshotStateSet<DayInfo> = mutableStateSetOf()
    val masterData= getMasterDataStaticUseCase.masterDataFlow.stateIn(viewModelScope, started = SharingStarted.Lazily, initialValue = null)

    val languageUser = LocaleManager.getCurrentLanguage()
    val dayDataFlow: StateFlow<List<DayDataRequest>?> = getDayDataUseCase.dayDataFlow

    init {
        days.addAll(getDaysRangeUseCase.getLast30Days(languageUser))
        viewModelScope.launch {
            val maps = buildMasterDataMapsUseCase.execute(languageUser)
            dayDataFlow.collect { dayDataList ->
                if (dayDataList == null) {
                    return@collect
                }
                val minDayDataDate = dayDataList.minOfOrNull { LocalDate.parse(it.dayData) }
                if (minDayDataDate != null) {
                    val additionalDays = getDaysRangeUseCase.extendDaysIfNeeded(days, minDayDataDate,languageUser)
                    if (additionalDays.isNotEmpty()) {
                        days.addAll( additionalDays)
                    }
                }
                val newMap = mutableStateMapOf<String, SeanceDuJour>()
                dayDataList.forEach { dayData ->
                    newMap[dayData.dayData] = dayData.toSeanceDuJour()
                }
                _uiState.update { current ->
                    current.copy(seancesParJour = newMap, masterDataMaps = maps)
                }
            }
        }

    }



    fun formatExerciseSets(
        exerciseSets: List<SetData>?,
        userUnit: MesureUnit
    ): String {
        if (exerciseSets.isNullOrEmpty()) return ""
        val sb = StringBuilder()
        var previousCharge: Double? = null
        var stop = false
        for (set in exerciseSets) {
            if (stop) break
            if (previousCharge != null && set.charge != previousCharge) {
                sb.append(
                    convertWeight(previousCharge, MesureUnit.KG, userUnit)
                        ?.let { String.format("%.1f", it) }.orEmpty()
                )
                sb.append(userUnit.toString().lowercase())
                sb.append(" | ")
            }
            sb.append(set.reps ?: " ")
            if (set.side == SetSide.LEFT) {
                sb.append("/")
                sb.append(set.linkedRightSet?.reps ?: "_")
            }
            sb.append(" - ")
            previousCharge = set.charge
            if (sb.length >= 40) {
                sb.append("...")
                stop = true
            }
        }
        if (!stop && previousCharge != null) {
            sb.append(
                convertWeight(previousCharge, MesureUnit.KG, userUnit)
                    ?.let { String.format("%.1f", it) }.orEmpty()
            )
            sb.append(userUnit.toString().lowercase())
        }
        return sb.toString()
    }

    fun setDaySelected(daySelected:String){
        _uiState.update { current-> current.copy(daySelected=daySelected) }
    }

    fun setSectionSelected(section:String){
        if(section=="data"){
            _uiState.update { current-> current.copy(sectionSelect = SectionSelectType.DATA) }
        }
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

        changeSaveStatus(SaveType.NOTSAVE)
    }

    fun updateSet(exerciseId: Long,index: Int,newValue : SetField,rightSet: Boolean,unilateral: Boolean){
        val day = _uiState.value.daySelected
        val seance = _uiState.value.seancesParJour[day] ?: return
        val sets = seance.setsMap[exerciseId] ?: return
        if (index !in sets.indices) return
        val left = sets[index]
        val right = left.linkedRightSet

        val updatedSet = when (newValue) {
            // ---------- REPS ----------
            is SetField.Reps -> {
                if (unilateral && rightSet && right != null) {
                    left.copy(
                        linkedRightSet = right.copy(reps = newValue.value)
                    )
                } else {
                    left.copy(reps = newValue.value)
                }
            }
            // ---------- CHARGE (sync L + R) ----------
            is SetField.Charge -> {
                if (unilateral) {
                    left.copy(
                        charge = newValue.value,
                        linkedRightSet = right?.copy(charge = newValue.value)
                    )
                } else {
                    left.copy(charge = newValue.value)
                }
            }

            // ---------- RIR (sync L + R) ----------
            is SetField.Rir -> {
                if (unilateral) {
                    left.copy(
                        rir = newValue.value,
                        linkedRightSet = right?.copy(rir = newValue.value)
                    )
                } else {
                    left.copy(rir = newValue.value)
                }
            }
        }
        sets[index] = updatedSet
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
        if (lateriteId == 2L) { // unilatéral
            for (i in sets.indices) {
                val set = sets[i]
                val updatedRightSet = set.linkedRightSet?.copy(
                    reps = set.linkedRightSet?.reps ?: set.reps,
                    charge = set.linkedRightSet?.charge ?: set.charge,
                    rir = set.linkedRightSet?.rir ?: set.rir,
                    type = set.linkedRightSet?.type ?: set.type
                ) ?: SetData(
                    type = set.type,
                    charge = set.charge,
                    reps = set.reps,
                    rir = set.rir,
                    side = SetSide.RIGHT
                )
                sets[i] = set.copy(
                    side = SetSide.LEFT,
                    linkedRightSet = updatedRightSet
                )
            }
        } else { // bilatéral ou autre
            for (i in sets.indices) {
                val set = sets[i]
                sets[i] = set.copy(
                    side = SetSide.BOTH,
                    linkedRightSet = null
                )
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

    fun toggleBottomSheet(show: BottomSheetVisibleType) {
        _uiState.update { it.copy(isBottomSheetVisible = show) }
    }

    fun toggleShowAnalyseData(show: Boolean? = null) {
        _uiState.update { currentState ->
            currentState.copy(
                showAnalyseData = show ?: !currentState.showAnalyseData
            )
        }
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
                isBottomSheetVisible = BottomSheetVisibleType.NO
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

    fun addSetToExercise(exerciseId: Long, lateriteId: Long) {
        val seance = getOrCreateSeanceOfADay()

        val sets = seance.setsMap.getOrPut(exerciseId) { mutableStateListOf() }

        val lastCharge = sets.lastOrNull()?.charge
        val lastRir = sets.lastOrNull()?.rir?:1
        val newSet = if (lateriteId == 2L) {
            val rightSet = SetData(
                type = SetType.EFFECTIVE,
                charge = lastCharge,
                reps = null,
                rir = null,
                side = SetSide.RIGHT,
                linkedRightSet = null
            )
            SetData(
                type = SetType.EFFECTIVE,
                charge = lastCharge,
                reps = null,
                rir = lastRir,
                side = SetSide.LEFT,
                linkedRightSet = rightSet
            )
        } else {
            SetData(
                type = SetType.EFFECTIVE,
                charge = lastCharge,
                reps = null,
                rir = lastRir,
                side = SetSide.BOTH,
                linkedRightSet = null
            )
        }
        sets.add(newSet)


        changeSaveStatus(SaveType.NOTSAVE)
    }

    fun getLastExercicePerfBetweenDate(dateRecent: String, dateBefore: String) {
        val dayDataList = dayDataFlow.value // List<DayDataRequest>?
        val exerciseId = uiState.value.expandedExerciseId ?: return

        val result = getLastExercisePerfBetweenDateUseCase(
             dayDataList,
             exerciseId,
             dateRecent,
             dateBefore
        )
        _uiState.update { current ->
            current.copy(analyseData = result)
        }
    }

    fun swapSet(exerciseId: Long, fromIndex: Int, toIndex: Int) {
        val seance = _uiState.value.seancesParJour[_uiState.value.daySelected] ?: return
        val sets = seance.setsMap[exerciseId] ?: return

        if (fromIndex !in sets.indices || toIndex !in sets.indices) return

        sets[fromIndex] = sets.set(toIndex, sets[fromIndex])
        changeSaveStatus(SaveType.NOTSAVE)
    }

    fun deleteSet(exerciseId: Long, setId: String) {
        val day = _uiState.value.daySelected
        val seance = _uiState.value.seancesParJour[day] ?: return
        val sets = seance.setsMap[exerciseId] ?: return
        seance.setsMap[exerciseId] = sets.filter { it.id != setId }.toMutableStateList()
        changeSaveStatus(SaveType.NOTSAVE)
    }

    fun expandExercise(exerciseId: Long?) {
        _uiState.update { it.copy(expandedExerciseId = exerciseId, showAnalyseData = false) }
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

        changeSaveStatus(SaveType.NOTSAVE,false)
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

        changeSaveStatus(SaveType.NOTSAVE,false)
    }




    private val autoSaveJobs = mutableMapOf<String, Job>()

    fun changeSaveStatus(status: SaveType, exerciseContentEdit : Boolean = true) {
        val day = _uiState.value.daySelected
        val seance = _uiState.value.seancesParJour[day] ?: return
        _uiState.value.seancesParJour[day] = seance.copy(saveAPI = status)
        if (status == SaveType.NOTSAVE||status == SaveType.LocalSAVE) {
            if (status != SaveType.LocalSAVE) {
                viewModelScope.launch {
                    updateDayDataToLocalUseCase(seance.toDayDataRequest(day),if (exerciseContentEdit) TypeOfAction.FORCESEANCEUPDATE else TypeOfAction.UPDATEBOTH)
                    _uiState.value.seancesParJour[day] = seance.copy(saveAPI = SaveType.LocalSAVE)
                }
            }
            autoSaveJobs[day]?.cancel()
            val seanceForAutoSave = seance.copy() // capture immuable pour le Worker
            autoSaveJobs[day] = viewModelScope.launch {
                delay(3_000L)
                autoSaveSeanceIfNeeded(day, seanceForAutoSave)
            }
        }
    }

    fun autoSaveSeanceIfNeeded(day: String, seance: SeanceDuJour) {
        val currentDayData = seance.toDayDataRequest(day)


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

    fun getSeanceSummary(day: String): SeanceSummary {
        val state = uiState.value
        val maps = state.masterDataMaps
            ?: return SeanceSummary(emptyList(), emptySet(),emptySet(), 0, 0)

        val daySeance = state.seancesParJour[day]
            ?: return SeanceSummary(emptyList(), emptySet(),emptySet(), 0, 0)

        val exercisesList = daySeance.exercisesInSceance
            .mapNotNull { maps.exerciseMap[it.exerciseId] }

        val principalMuscle = exercisesList
            .mapNotNull { ex ->
                ex.muscleInfos?.find { it.principal }?.idMuscle
            }
            .mapNotNull { maps.muscleMap[it]?.getName(languageUser) }
            .toSet()

        val mainFocus = exercisesList
            .mapNotNull { it.mainFocusGroup?.let { id -> maps.musclesGroupMap[id] } }
            .toSet()

        val (totalSets, totalVolume) = daySeance.setsMap.values.flatten()
            .fold(0 to 0.0) { (count, sum), set ->
                val setVolume = if (set.linkedRightSet == null) {
                    (set.reps ?: 0) * (set.charge ?: 0.0)
                } else {
                    (((set.reps ?: 0) + (set.linkedRightSet?.reps ?: 0)) * (set.charge ?: 0.0) / 2)
                }
                (count + 1) to (sum + setVolume)
            }

        return SeanceSummary(
            exercisesList = exercisesList,
            mainFocus = mainFocus,
            principalMuscle=principalMuscle,
            totalSets = totalSets,
            sommePond = totalVolume.fastRoundToInt()
        )
    }


}