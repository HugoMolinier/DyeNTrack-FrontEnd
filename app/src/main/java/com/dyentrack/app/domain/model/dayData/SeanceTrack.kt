package com.dyentrack.app.domain.model.dayData

import kotlinx.serialization.Serializable

@Serializable
data class SeanceTrack(
    val startHour: StartHour?,
    val presetSeanceId: Long? ,
    val presetLocalId:Long?,
    val plannedExercises: List<PlannedExerciseVO>
)
