package com.dyentrack.app.domain.model.exercise

data class ExerciseInSeance(
    val exerciseId: Long,
    var laterite: Long ?= null,
    var equipment: Long?= null
)
