package com.dyentrack.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class DayDataRequest(
    val idDayData:Long?,
    val dayData: String,
    val seanceTrack: SeanceTrack?=null,
    val nutritionTrack: NutritionTrack?=null,
    val physioTrack: PhysioTrack?=null,
    val lastUpdate: String?=null,
    val lastUpdateHealthLocal: String ?=null  //seulement pour le merge API / local
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

    fun equalsIgnoreNull(other: NutritionTrack?): Boolean {
        if (other == null) return false

        fun Int?.orZero() = this ?: 0

        return calories.orZero() == other.calories.orZero() &&
                proteins.orZero() == other.proteins.orZero() &&
                lipids.orZero() == other.lipids.orZero() &&
                carbohydrates.orZero() == other.carbohydrates.orZero() &&
                fiber.orZero() == other.fiber.orZero() &&
                cafeins.orZero() == other.cafeins.orZero()
    }
}

@Serializable
data class PhysioTrack(
    val weight: Double?=0.0,
    val step: Int?=0,
    val hourOfSleep: Double?=.0,
    val mood: String?=""

){
    val isNotNull: Boolean
        get() = listOf(weight, step, hourOfSleep).any { it != null && it != 0.0 && it != 0 } || !mood.isNullOrEmpty()

    fun equalsIgnoreNullOrDefault(other: PhysioTrack?): Boolean {
        if (other == null) return false

        fun Double?.orZero() = this ?: 0.0
        fun Int?.orZero() = this ?: 0
        fun String?.orEmpty() = this ?: ""

        return weight.orZero() == other.weight.orZero() &&
                step.orZero() == other.step.orZero() &&
                hourOfSleep.orZero() == other.hourOfSleep.orZero() &&
                mood.orEmpty() == other.mood.orEmpty()
    }
}


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
