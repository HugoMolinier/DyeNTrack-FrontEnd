package com.dyentrack.app.domain.model.exercise

data class Exercise(
    val      idExercise : Long,
    val nameFR : String,
 val mainFocusGroup: Long,
val description: String,
    val muscleInfos : List<muscleInfos>
)