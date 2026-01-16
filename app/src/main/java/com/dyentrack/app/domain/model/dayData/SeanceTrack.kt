package com.dyentrack.app.domain.model.dayData



data class SeanceTrack(
    val startHour: StartHour?,
    val presetSeanceId: Long? ,
    val presetLocalId:Long?,
    val plannedExercises: List<PlannedExerciseVO>
)
