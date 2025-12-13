package com.dyentrack.app.presentation.screens.exercise

import com.dyentrack.app.domain.model.SeanceTrack

data class ExerciseUiState(
    val sectionChose: SectionChose = SectionChose.EXPLAIN
)

enum class SectionChose {
    EXPLAIN,       // Explic
    ANALYSE   // Analyse
}