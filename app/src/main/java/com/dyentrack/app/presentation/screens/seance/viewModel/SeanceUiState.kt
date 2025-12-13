package com.dyentrack.app.presentation.screens.seance.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.dyentrack.app.domain.model.NutritionTrack
import com.dyentrack.app.domain.model.PhysioTrack
import com.dyentrack.app.domain.model.SetData
import com.dyentrack.app.domain.model.exercise.ExerciseInSeance
import java.time.LocalDate
import java.time.format.DateTimeFormatter

enum class ExerciseMode { NORMAL, LOCK, CHANGE_ORDER }
data class SeanceUiState(
    val seancesParJour: SnapshotStateMap<String, SeanceDuJour> = mutableStateMapOf(),
    val healthConnectPermissionsNeeded: Set<String>? = null,
    val exerciseToAdd: List<Long> = emptyList(),
    val daySelected: String = LocalDate.now().format(DateTimeFormatter.ISO_DATE),

    val expandedExerciseId: Long? = null,
    val isBottomSheetVisible: Boolean = false,
    val searchQuery: String = "",

    val sectionSelect:SectionSelectType?=SectionSelectType.SEANCE,


    val exerciseMode: ExerciseMode = ExerciseMode.NORMAL,
    // résultats d’analyse
    val showAnalyseData: Boolean = false,
    val isLoadingRequest: Boolean = false,
    val analyseData: Map<String, List<SetData>> = emptyMap()


)

data class SeanceDuJour(
    //séparé pouur éviter les recomposition
    val nutritionTrack: NutritionTrack =NutritionTrack(),
    val physioTrack: PhysioTrack= PhysioTrack(),

    val exercisesInSceance: SnapshotStateList<ExerciseInSeance> = mutableStateListOf(),
    val saveAPI: SaveType = SaveType.NOTSAVE,
    val setsMap: SnapshotStateMap<Long, SnapshotStateList<SetData>> = mutableStateMapOf(),
    val note: String? = null,
)





