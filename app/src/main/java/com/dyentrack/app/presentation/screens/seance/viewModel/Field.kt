package com.dyentrack.app.presentation.screens.seance.viewModel

import com.dyentrack.app.domain.model.exercise.Exercise

enum class SaveType {
    NOTSAVE,
    LocalSAVE,
    APISAVE
}

enum class SectionSelectType {
    SEANCE,
    DATA
}

enum class PhysioField {
    WEIGHT,
    STEP,
    HOUR_OF_SLEEP,
    MOOD
}

enum class NutritionField {
    CALORIES,
    PROTEINS,
    LIPIDS,
    CARBOHYDRATES,
    FIBER,
    CAFEINS
}

data class SeanceSummary(
    val exercisesList: List<Exercise>,
    val mainFocus: Set<String>,
    val totalSets: Int,
    val sommePond: Int
)
