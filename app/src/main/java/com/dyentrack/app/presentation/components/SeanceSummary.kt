package com.dyentrack.app.presentation.components

import com.dyentrack.app.domain.model.exercise.Exercise
import java.security.Principal

data class SeanceSummary(
    val exercisesList: List<Exercise>,
    val mainFocus: Set<String>,
    val principalMuscle:Set<String>,
    val totalSets: Int,
    val sommePond: Int
)