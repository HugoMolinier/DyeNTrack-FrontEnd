package com.dyentrack.app.presentation.screens.exercise

import com.dyentrack.app.domain.model.MasterDataMaps
import com.dyentrack.app.domain.model.MuscleRate
import com.dyentrack.app.domain.model.SeanceTrack
import com.dyentrack.app.domain.model.exercise.Exercise

data class ExerciseUiState(
    val sectionChose: SectionChose = SectionChose.EXPLAIN,
    val allDataExercise: Map<String, MuscleRate> =emptyMap(),

    val masterDataMaps: MasterDataMaps? = MasterDataMaps(),

)

enum class SectionChose {
    EXPLAIN,       // Explic
    ANALYSE   // Analyse
}