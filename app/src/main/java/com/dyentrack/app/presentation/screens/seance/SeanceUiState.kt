package com.dyentrack.app.presentation.screens.seance

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.dyentrack.app.domain.model.MasterDataMaps
import com.dyentrack.app.domain.model.PresetId
import com.dyentrack.app.domain.model.SetData
import com.dyentrack.app.domain.model.dayData.NutritionTrack
import com.dyentrack.app.domain.model.dayData.PhysioTrack
import com.dyentrack.app.domain.model.enums.BottomSheetVisibleType
import com.dyentrack.app.domain.model.enums.ExerciseMode
import com.dyentrack.app.domain.model.enums.SaveType
import com.dyentrack.app.domain.model.enums.SectionSelectType
import com.dyentrack.app.domain.model.exercise.ExerciseInSeance
import com.dyentrack.app.presentation.utils.toIsoDate
import java.time.LocalDate

data class SeanceUiState(
    val seancesParJour: SnapshotStateMap<String, SeanceDuJour> = mutableStateMapOf(),
    val exerciseToAdd: List<Long> = emptyList(),
    val daySelected: String = LocalDate.now().toIsoDate(),

    val expandedExerciseId: Long? = null,
    val isBottomSheetVisible: BottomSheetVisibleType = BottomSheetVisibleType.NO,

    val sectionSelect: SectionSelectType? = SectionSelectType.SEANCE,

    val masterDataMaps: MasterDataMaps? = MasterDataMaps(),

    val exerciseMode: ExerciseMode = ExerciseMode.NORMAL,
    // résultats d’analyse
    val showExpand: ExpandSection = ExpandSection.NONE,
    val isLoadingRequest: Boolean = false,
    val analyseData: Map<String, List<SetData>> = emptyMap()


)

enum class ExpandSection() {
    ANALYSE, NONE, COMMENT
}

data class SeanceDuJour(
    //séparé pouur éviter les recomposition
    val nutritionTrack: NutritionTrack = NutritionTrack(),
    val physioTrack: PhysioTrack = PhysioTrack(),

    val exercisesInSceance: SnapshotStateList<ExerciseInSeance> = mutableStateListOf(),
    val saveAPI: SaveType = SaveType.NOTSAVE,
    val setsMap: SnapshotStateMap<Long, SnapshotStateList<SetData>> = mutableStateMapOf(),
    val idPreset: PresetId? = PresetId(null, null), //API, Local

)