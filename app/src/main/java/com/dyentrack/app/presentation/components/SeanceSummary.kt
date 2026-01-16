package com.dyentrack.app.presentation.components

import com.dyentrack.app.domain.model.exercise.Exercise

data class SeanceSummary(
    val exercisesList: List<Exercise>?=emptyList(),
    val mainFocus: Set<String>,
    val principalMuscle:Set<String>,
    val totalSets: Int?=0,
    val sommePond: Int?=0
)