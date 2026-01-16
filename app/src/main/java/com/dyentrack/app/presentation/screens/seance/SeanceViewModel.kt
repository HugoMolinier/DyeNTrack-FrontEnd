package com.dyentrack.app.presentation.screens.seance

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
import com.dyentrack.app.core.utils.GsonProvider
import com.dyentrack.app.data.worker.SaveSeanceWorker
import com.dyentrack.app.domain.model.DayInfo
import com.dyentrack.app.domain.model.PresetId
import com.dyentrack.app.domain.model.SetData
import com.dyentrack.app.domain.model.SetField
import com.dyentrack.app.domain.model.SetSide
import com.dyentrack.app.domain.model.SetType
import com.dyentrack.app.domain.model.UserPreference
import com.dyentrack.app.domain.model.dayData.DayDataRequest
import com.dyentrack.app.domain.model.enums.BottomSheetVisibleType
import com.dyentrack.app.domain.model.enums.ExerciseMode
import com.dyentrack.app.domain.model.enums.NutritionField
import com.dyentrack.app.domain.model.enums.PhysioField
import com.dyentrack.app.domain.model.enums.SaveType
import com.dyentrack.app.domain.model.enums.SectionSelectType
import com.dyentrack.app.domain.model.enums.SyncPerformanceMode
import com.dyentrack.app.domain.model.enums.TypeOfAction
import com.dyentrack.app.domain.model.exercise.ExerciseInSeance
import com.dyentrack.app.domain.model.getName
import com.dyentrack.app.domain.model.preset.Preset
import com.dyentrack.app.domain.usecase.GetDaysRangeUseCase
import com.dyentrack.app.domain.usecase.GetLastExercisePerfBetweenDateUseCase
import com.dyentrack.app.domain.usecase.daydata.GetDayDataUseCase
import com.dyentrack.app.domain.usecase.daydata.UpdateDayDataToLocalUseCase
import com.dyentrack.app.domain.usecase.masterdata.BuildMasterDataMapsUseCase
import com.dyentrack.app.domain.usecase.masterdata.GetMasterDataStaticUseCase
import com.dyentrack.app.domain.usecase.preference.FetchUserPreferenceUseCase
import com.dyentrack.app.presentation.components.SeanceSummary
import com.dyentrack.app.presentation.mapper.toDayDataRequest
import com.dyentrack.app.presentation.mapper.toSeanceDuJour
import com.dyentrack.app.presentation.utils.toIsoDate
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
    fetchUserPreferenceUseCase: FetchUserPreferenceUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SeanceUiState())
    val uiState: StateFlow<SeanceUiState> = _uiState


    val days: SnapshotStateSet<DayInfo> = mutableStateSetOf()
    val masterData = getMasterDataStaticUseCase.masterDataFlow.stateIn(
        viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    val languageUser = LocaleManager.getCurrentLanguage()
    private val dayDataFlow: StateFlow<List<DayDataRequest>?> = getDayDataUseCase.dayDataFlow
    val fetchStatus: StateFlow<SaveType> = getDayDataUseCase.apiFetchStatus
    val userPreference: StateFlow<UserPreference?> =
        fetchUserPreferenceUseCase.userPreferenceFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )


    private var _lastExerciseSetMapAll: Map<Long, SetData> = emptyMap()
    private var _lastExerciseSetMapPreset: Map<Long, Map<Long, SetData>> = emptyMap()

    init {
        days.addAll(getDaysRangeUseCase.getLast30Days(languageUser))
        val last15DaysSet = (0L until 15L).map { LocalDate.now().minusDays(it).toIsoDate() }.toSet()

        viewModelScope.launch {
            val maps = buildMasterDataMapsUseCase.execute(languageUser)
            dayDataFlow.collect { dayDataList ->
                if (dayDataList == null) return@collect

                val minDayDataDate = dayDataList.minOfOrNull { it.localDate }
                if (minDayDataDate != null) {
                    val additionalDays =
                        getDaysRangeUseCase.extendDaysIfNeeded(days, minDayDataDate, languageUser)
                    if (additionalDays.isNotEmpty()) {
                        days.addAll(additionalDays)
                    }
                }

                val sceanceJourUi = mutableStateMapOf<String, SeanceDuJour>()
                dayDataList.forEach { dayData ->
                    sceanceJourUi[dayData.dayData] = dayData.toSeanceDuJour()

                }
                _uiState.update { current ->
                    current.copy(seancesParJour = sceanceJourUi, masterDataMaps = maps)
                }

                val lastExerciseSetMapPreset = mutableMapOf<Long, MutableMap<Long, SetData>>()
                val lastExerciseSetMapAll = mutableMapOf<Long, SetData>()

                sceanceJourUi.filter { it.key in last15DaysSet }
                    .toSortedMap()
                    .forEach { (_, seance) ->

                        // Parcours de tous les sets pour mémoriser la dernière charge et RIR par exercice
                        userPreference.value?.syncPerformanceMode?.let { mode ->
                            when (mode) {
                                SyncPerformanceMode.ALL -> {
                                    seance.setsMap.forEach { (exerciseId, setsList) ->
                                        setsList.firstOrNull()?.let { firstSet ->
                                            lastExerciseSetMapAll[exerciseId] = firstSet
                                        }
                                    }
                                }

                                SyncPerformanceMode.PRESET_ONLY -> {
                                    seance.idPreset?.let { presetId ->
                                        seance.setsMap.forEach { (exerciseId, setsList) ->
                                            setsList.firstOrNull()?.let { firstSet ->
                                                val presetMap =
                                                    lastExerciseSetMapPreset.getOrPut(exerciseId) { mutableMapOf() }
                                                presetId.localId?.let { presetMap[it] = firstSet }
                                                presetId.apiId?.let { presetMap[it] = firstSet }
                                            }
                                        }
                                    }
                                }

                                SyncPerformanceMode.NONE -> {
                                }
                            }
                        }

                    }
                _lastExerciseSetMapAll = lastExerciseSetMapAll
                _lastExerciseSetMapPreset = lastExerciseSetMapPreset


            }
        }

    }

    fun setDaySelected(daySelected: String) {
        _uiState.update { current -> current.copy(daySelected = daySelected) }
    }

    fun setSectionSelected(section: String) {
        if (section == "data") {
            _uiState.update { current -> current.copy(sectionSelect = SectionSelectType.DATA) }
        }
    }

    fun toggleMode(newMode: ExerciseMode) {
        _uiState.update { current ->
            when {
                current.exerciseMode == newMode ->
                    current.copy(exerciseMode = ExerciseMode.NORMAL)

                newMode == ExerciseMode.CHANGE_ORDER ->
                    current.copy(
                        exerciseMode = newMode,
                        expandedExerciseId = null
                    )

                else ->
                    current.copy(exerciseMode = newMode)
            }
        }
    }

    fun swapExercise(fromIndex: Int, toIndex: Int) = withCurrentSeance { day, seance ->
        val exercises = seance.exercisesInSceance
        if (fromIndex !in exercises.indices || toIndex !in exercises.indices) return
        val temp = exercises[fromIndex]
        exercises[fromIndex] = exercises[toIndex]
        exercises[toIndex] = temp


    }

    fun updateSet(
        exerciseId: Long,
        index: Int,
        newValue: SetField,
        rightSet: Boolean,
        unilateral: Boolean
    ) {
        updateExercise(
            exerciseId,
            propertyUpdater = { it },
            setsUpdater = { sets ->
                if (index !in sets.indices) return@updateExercise
                val left = sets[index]
                val right = left.linkedRightSet

                sets[index] = when (newValue) {
                    // ---------- REPS ----------
                    is SetField.Reps -> if (unilateral && rightSet && right != null) {
                        left.copy(linkedRightSet = right.copy(reps = newValue.value))
                    } else left.copy(reps = newValue.value)
                    // ---------- CHARGE (sync L + R) ----------
                    is SetField.Charge -> if (unilateral) {
                        left.copy(
                            charge = newValue.value,
                            linkedRightSet = right?.copy(charge = newValue.value)
                        )
                    } else left.copy(charge = newValue.value)
                    // ---------- RIR (sync L + R) ----------
                    is SetField.Rir -> if (unilateral) {
                        left.copy(
                            rir = newValue.value,
                            linkedRightSet = right?.copy(rir = newValue.value)
                        )
                    } else left.copy(rir = newValue.value)
                }
            }
        )
    }


    fun updateLaterite(exerciseId: Long, lateriteId: Long) {
        updateExercise(
            exerciseId,
            propertyUpdater = { it.copy(laterite = lateriteId) },
            setsUpdater = { sets ->
                if (lateriteId == 2L) { // unilatéral
                    for (i in sets.indices) {
                        val set = sets[i]
                        val updatedRightSet = set.linkedRightSet?.copy(
                            reps = set.linkedRightSet.reps ?: set.reps,
                            charge = set.linkedRightSet.charge ?: set.charge,
                            rir = set.linkedRightSet.rir ?: set.rir,
                            type = set.linkedRightSet.type
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
            }
        )
    }

    fun updateEquipment(exerciseId: Long, equipmentId: Long) {
        updateExercise(
            exerciseId,
            propertyUpdater = { it.copy(equipment = equipmentId) }
        )
    }

    fun changeDaySelected(dateFormat: String) {
        _uiState.update {
            it.copy(
                daySelected = dateFormat,
                showExpand = ExpandSection.NONE,
                expandedExerciseId = null
            )
        }
    }

    fun toggleBottomSheet(show: BottomSheetVisibleType) {
        _uiState.update { it.copy(isBottomSheetVisible = show) }
    }

    fun toggleShowAnalyseData(show: ExpandSection) {
        _uiState.update { currentState ->
            currentState.copy(
                showExpand = if (show == currentState.showExpand) ExpandSection.NONE else show
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

    fun addExerciseToSeance() = withCurrentSeance { _, seance ->
        val newExercises = _uiState.value.exerciseToAdd.map { exerciseId ->
            val exercise = _uiState.value.masterDataMaps!!.exerciseMap[exerciseId]
            val exerciseAlreadyHave = seance.exercisesInSceance.find { it.exerciseId == exerciseId }
            ExerciseInSeance(
                exerciseId = exerciseId,
                exerciseAlreadyHave?.laterite ?: 1L,
                exerciseAlreadyHave?.equipment ?: exercise?.defaultEquipment ?: 1L
            )
        }
        seance.exercisesInSceance.clear()
        seance.exercisesInSceance.addAll(newExercises)

        _uiState.update {
            it.copy(
                exerciseToAdd = emptyList(),
                isBottomSheetVisible = BottomSheetVisibleType.NO
            )
        }
        changeSaveStatus(SaveType.NOTSAVE)
    }


    fun setPreset(idPresetLocal: Long?, idPresetApi: Long?) {
        updateSeance(
            save = true,
            exerciseSave = false
        ) { seance ->
            seance.copy(
                idPreset = PresetId(
                    apiId = idPresetApi,
                    localId = idPresetLocal
                )
            )
        }
    }

    fun addExerciseFromPresetForDaySelected(presets: List<Preset>) =
        withCurrentSeance { _, seance ->
            val presetId = seance.idPreset ?: return
            val preset = presets.find { preset ->
                (presetId.localId != null && preset.idLocal == presetId.localId) ||
                        (presetId.apiId != null && preset.idPreset == presetId.apiId)
            } ?: return
            val syncMode = userPreference.value?.syncPerformanceMode ?: SyncPerformanceMode.NONE
            val exercisesToAdd = preset.presetSeanceExerciseVO
                ?.filter { presetEx ->
                    seance.exercisesInSceance.none { it.exerciseId == presetEx.idExercise }
                }
                ?.map { presetEx ->
                    val newExercise = ExerciseInSeance(
                        exerciseId = presetEx.idExercise,
                        laterite = presetEx.idLateralite,
                        equipment = presetEx.idEquipment
                    )

                    // Créer les sets par défaut pour cet exercice
                    val lastSet = when (syncMode) {
                        SyncPerformanceMode.ALL -> _lastExerciseSetMapAll[presetEx.idExercise]
                        SyncPerformanceMode.PRESET_ONLY -> {
                            val presetMap = _lastExerciseSetMapPreset[presetEx.idExercise]
                            presetId.localId?.let { presetMap?.get(it) }
                                ?: presetId.apiId?.let { presetMap?.get(it) }
                        }

                        SyncPerformanceMode.NONE -> null
                    }

                    val sets = mutableListOf<SetData>()
                    repeat(presetEx.nbSet) {
                        val set = if (presetEx.idLateralite == 2L) { // unilatéral
                            val rightSet = SetData(
                                type = SetType.EFFECTIVE,
                                charge = lastSet?.charge,
                                reps = null,
                                rir = lastSet?.rir,
                                side = SetSide.RIGHT
                            )
                            SetData(
                                type = SetType.EFFECTIVE,
                                charge = lastSet?.charge,
                                reps = null,
                                rir = lastSet?.rir,
                                side = SetSide.LEFT,
                                linkedRightSet = rightSet
                            )
                        } else { // bilatéral ou autre
                            SetData(
                                type = SetType.EFFECTIVE,
                                charge = lastSet?.charge,
                                reps = null,
                                rir = lastSet?.rir,
                                side = SetSide.BOTH
                            )
                        }
                        sets.add(set)
                    }

                    // Ajouter les sets dans la map de la séance
                    seance.setsMap[newExercise.exerciseId] = sets.toMutableStateList()
                    newExercise
                } ?: emptyList()

            seance.exercisesInSceance.addAll(exercisesToAdd)
            changeSaveStatus(SaveType.NOTSAVE)
        }

    fun setExerciseToAdd(exercises: List<Long>) {
        _uiState.update { currentState ->
            currentState.copy(exerciseToAdd = exercises)
        }
    }

    fun deleteExercice(exerciseId: Long) = withCurrentSeance { _, seance ->
        seance.exercisesInSceance.removeAll { it.exerciseId == exerciseId }
        seance.setsMap.remove(exerciseId)
        changeSaveStatus(SaveType.NOTSAVE)
    }


    fun addSetToExercise(exerciseId: Long, lateriteId: Long) = withCurrentSeance { _, seance ->
        val syncMode = userPreference.value?.syncPerformanceMode ?: SyncPerformanceMode.NONE
        val presetId = seance.idPreset
        updateExercise(
            exerciseId,
            propertyUpdater = { it },
            setsUpdater = { sets ->
                val referenceSet: SetData? = when (syncMode) {
                    SyncPerformanceMode.ALL -> sets.lastOrNull()
                        ?: _lastExerciseSetMapAll[exerciseId]

                    SyncPerformanceMode.PRESET_ONLY -> {
                        val presetMap = _lastExerciseSetMapPreset[exerciseId]
                        val lastSetForPreset = presetId?.let { id ->
                            presetMap?.get(id.localId) ?: presetMap?.get(id.apiId)
                        }
                        sets.lastOrNull() ?: lastSetForPreset
                    }

                    SyncPerformanceMode.NONE -> sets.lastOrNull()
                }

                val lastCharge = referenceSet?.charge
                val lastRir = referenceSet?.rir ?: 1
                val newSet = if (lateriteId == 2L) {
                    val rightSet = SetData(
                        type = SetType.EFFECTIVE,
                        charge = lastCharge,
                        reps = null,
                        rir = null,
                        side = SetSide.RIGHT
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
                        side = SetSide.BOTH
                    )
                }
                sets.add(newSet)
            }
        )
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
        updateExercise(
            exerciseId,
            propertyUpdater = { it },
            setsUpdater = { sets ->
                if (fromIndex in sets.indices && toIndex in sets.indices) {
                    sets[fromIndex] = sets.set(toIndex, sets[fromIndex])
                }
            }
        )
    }

    fun deleteSet(exerciseId: Long, setId: String) {
        updateExercise(
            exerciseId,
            propertyUpdater = { it },
            setsUpdater = { sets ->
                sets.removeAll { it.id == setId }
            }
        )
    }

    fun expandExercise(exerciseId: Long?) {
        _uiState.update {
            it.copy(
                expandedExerciseId = exerciseId,
                showExpand = ExpandSection.NONE
            )
        }
    }

    fun changeSection(sectionSelectType: SectionSelectType) {
        _uiState.update { it.copy(sectionSelect = sectionSelectType) }
    }


    // +++++++++++ Nutrition
    fun updateNutrition(field: NutritionField, value: Int) {
        updateSeance(save = true, exerciseSave = false) { seance ->
            val newTrack = when (field) {
                NutritionField.CALORIES -> seance.nutritionTrack.copy(calories = value)
                NutritionField.PROTEINS -> seance.nutritionTrack.copy(proteins = value)
                NutritionField.LIPIDS -> seance.nutritionTrack.copy(lipids = value)
                NutritionField.CARBOHYDRATES -> seance.nutritionTrack.copy(carbohydrates = value)
                NutritionField.FIBER -> seance.nutritionTrack.copy(fiber = value)
                NutritionField.CAFEINS -> seance.nutritionTrack.copy(cafeins = value)
            }
            seance.copy(nutritionTrack = newTrack)
        }

    }
    // +++++++++++ Physio

    fun updatePhysio(
        field: PhysioField,
        value: Any
    ) {
        updateSeance(save = true, exerciseSave = false) { seance ->
            val newTrack = when (field) {
                PhysioField.WEIGHT -> seance.physioTrack.copy(
                    weight = value as? Double ?: seance.physioTrack.weight
                )

                PhysioField.STEP -> seance.physioTrack.copy(
                    step = value as? Int ?: seance.physioTrack.step
                )

                PhysioField.HOUR_OF_SLEEP -> seance.physioTrack.copy(
                    hourOfSleep = value as? Double ?: seance.physioTrack.hourOfSleep
                )

                PhysioField.MOOD -> seance.physioTrack.copy(
                    mood = value as? String ?: seance.physioTrack.mood
                )
            }
            seance.copy(physioTrack = newTrack)
        }
    }


    private val autoSaveJobs = mutableMapOf<String, Job>()

    fun changeSaveStatus(status: SaveType, exerciseContentEdit: Boolean = true) =
        withCurrentSeance { day, seance ->
            _uiState.value.seancesParJour[day] = seance.copy(saveAPI = status)
            if (status == SaveType.NOTSAVE || status == SaveType.LocalSAVE) {
                if (status != SaveType.LocalSAVE) {
                    viewModelScope.launch {
                        updateDayDataToLocalUseCase(
                            seance.toDayDataRequest(day),
                            if (exerciseContentEdit) TypeOfAction.FORCESEANCEUPDATE else TypeOfAction.UPDATEBOTH
                        )
                        _uiState.value.seancesParJour[day] =
                            seance.copy(saveAPI = SaveType.LocalSAVE)
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
        val json = GsonProvider.gson.toJson(currentDayData)

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
            if (workInfo != null && workInfo.state == WorkInfo.State.SUCCEEDED) {

                _uiState.value.seancesParJour[day] = seance.copy(saveAPI = SaveType.APISAVE)

            }
        }

    }


    fun getSeanceSummary(day: String): SeanceSummary {
        val state = uiState.value
        val maps = state.masterDataMaps
            ?: return SeanceSummary(emptyList(), emptySet(), emptySet(), 0, 0)

        val daySeance = state.seancesParJour[day]
            ?: return SeanceSummary(emptyList(), emptySet(), emptySet(), 0, 0)

        val exercisesList = daySeance.exercisesInSceance
            .mapNotNull { maps.exerciseMap[it.exerciseId] }

        val principalMuscle = exercisesList
            .mapNotNull { maps.muscleMap[it.mainMuscle]?.getName(languageUser) }.toSet()


        val mainFocus = exercisesList
            .mapNotNull { it.mainFocusGroup?.let { id -> maps.musclesGroupMap[id] } }
            .toSet()

        val (totalSets, totalVolume) = daySeance.setsMap.values.flatten()
            .fold(0 to 0.0) { (count, sum), set ->
                val setVolume = if (set.linkedRightSet == null) {
                    (set.reps ?: 0) * (set.charge ?: 0.0)
                } else {
                    (((set.reps ?: 0) + (set.linkedRightSet.reps ?: 0)) * (set.charge ?: 0.0) / 2)
                }
                (count + 1) to (sum + setVolume)
            }

        return SeanceSummary(
            exercisesList = exercisesList,
            mainFocus = mainFocus,
            principalMuscle = principalMuscle,
            totalSets = totalSets,
            sommePond = totalVolume.fastRoundToInt()
        )


    }

    private fun updateSeance(
        day: String = _uiState.value.daySelected,
        save: Boolean = true,
        exerciseSave: Boolean = true,
        updater: (SeanceDuJour) -> SeanceDuJour
    ) {
        val seance = _uiState.value.seancesParJour.getOrPut(day) { SeanceDuJour() }
        _uiState.value.seancesParJour[day] = updater(seance)
        if (save) changeSaveStatus(SaveType.NOTSAVE, exerciseSave)
    }

    private fun updateExercise(
        exerciseId: Long,
        propertyUpdater: (ExerciseInSeance) -> ExerciseInSeance,
        setsUpdater: ((MutableList<SetData>) -> Unit)? = null
    ) = withCurrentSeance { _, seance ->
        val index = seance.exercisesInSceance.indexOfFirst { it.exerciseId == exerciseId }
        if (index != -1) {
            seance.exercisesInSceance[index] = propertyUpdater(seance.exercisesInSceance[index])
        }
        setsUpdater?.invoke(seance.setsMap.getOrPut(exerciseId) { mutableStateListOf() })
        changeSaveStatus(SaveType.NOTSAVE)
    }

    private inline fun withCurrentSeance(
        block: (day: String, seance: SeanceDuJour) -> Unit
    ) {
        val day = _uiState.value.daySelected
        val seance = _uiState.value.seancesParJour.getOrPut(day) { SeanceDuJour() }
        block(day, seance)
    }
}

