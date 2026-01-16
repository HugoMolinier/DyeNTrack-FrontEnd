package com.dyentrack.app.domain.model.dayData


data class PlannedExerciseVO(
    val id: Long?,
    val exerciseOrder: Int,
    val exerciseId: Long,
    val lateraliteId: Long,
    val equipmentId: Long,
    val sets: List<SetVO>? = emptyList()
)