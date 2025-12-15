package com.dyentrack.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class DayDataRequest(
    val idDayData:Long?,
    val dayData: String,
    val seanceTrack: SeanceTrack?=null,
    val nutritionTrack: NutritionTrack?=null,
    val physioTrack: PhysioTrack?=null,
    val lastUpdate: String?=null
)

@Serializable
data class NutritionTrack(
    val calories: Int?=0,
    val proteins: Int?=0,
    val lipids: Int?=0,
    val carbohydrates: Int?=0,
    val fiber: Int?=0,
    val cafeins: Int?=0
) {
    val isNotNull: Boolean
        get() = listOf(calories, proteins, lipids, carbohydrates, fiber, cafeins).any { it != null && it != 0 }
}

@Serializable
data class PhysioTrack(
    val weight: Double?=0.0,
    val step: Int?=0,
    val hourOfSleep: Double?=.0,
    val mood: String?=""
)


@Serializable
data class SeanceTrack(
    val startHour: StartHour?,
    val presetSeanceId: Long? ,
    val plannedExercises: List<PlannedExerciseVO>
)

@Serializable
data class StartHour(
    val hour: Int,
    val minute: Int,
    val second: Int?,
    val nano: Int?
)

@Serializable
data class PlannedExerciseVO(
    val id: Long?,
    val exerciseOrder: Int,
    val exerciseId: Long,
    val lateraliteId: Long,
    val equipmentId: Long,
    val sets: List<SetVO>? = emptyList()
)

@Serializable
data class SetVO(
    val setOrder: Int,
    val repsNumber: Int,
    val rir: Int,
    val charge: Double,
    val typeOfSet: SetType = SetType.EFFECTIVE,
    val side : SetSide= SetSide.BOTH
)
