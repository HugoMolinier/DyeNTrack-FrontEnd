package com.dyentrack.app.domain.model


data class DayDataRequest(
    val dayData: String,
    val seanceTrack: SeanceTrack
)

data class SeanceTrack(
    val startHour: StartHour?,
    val presetSeanceId: Long? ,
    val plannedExerciseVOs: List<PlannedExerciseVO>
)

data class StartHour(
    val hour: Int,
    val minute: Int,
    val second: Int?,
    val nano: Int?
)

data class PlannedExerciseVO(
    val exerciseOrder: Int,
    val exerciseId: Long,
    val lateraliteId: Long,
    val equipmentId: Long,
    val sets: List<SetVO>
)

data class SetVO(
    val setOrder: Int,
    val repsNumber: Int,
    val rir: Int,
    val charge: Double,
    val typeOfSet: SetType = SetType.EFFECTIVE
)
