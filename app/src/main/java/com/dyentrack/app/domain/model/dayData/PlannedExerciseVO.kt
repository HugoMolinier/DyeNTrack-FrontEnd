package com.dyentrack.app.domain.model.dayData

import kotlinx.serialization.Serializable

@Serializable
data class PlannedExerciseVO(
    val id: Long?,
    val exerciseOrder: Int,
    val exerciseId: Long,
    val lateraliteId: Long,
    val equipmentId: Long,
    val sets: List<SetVO>? = emptyList()
)