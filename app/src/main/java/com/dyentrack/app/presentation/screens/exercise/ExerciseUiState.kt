package com.dyentrack.app.presentation.screens.exercise

import com.dyentrack.app.domain.model.MasterDataMaps
import com.dyentrack.app.domain.model.MuscleRate

data class ExerciseUiState(
    val sectionChose: SectionChose = SectionChose.EXPLAIN,
    val allDataExercise: Map<String, MuscleRate> = emptyMap(),

    val masterDataMaps: MasterDataMaps? = MasterDataMaps(),

    )

enum class SectionChose {
    EXPLAIN,       // Explic
    ANALYSE   // Analyse
}