package com.dyentrack.app.presentation.components

import com.dyentrack.app.domain.model.exercise.Exercise

data class SeanceSummary(
    val exercisesList: List<Exercise>,
    val mainFocus: Set<String>,
    val totalSets: Int,
    val sommePond: Int
)