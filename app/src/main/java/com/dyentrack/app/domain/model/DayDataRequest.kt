package com.dyentrack.app.domain.model


data class DayDataRequest(
    val idDayData:Long?,
    val dayData: String,
    val seanceTrack: SeanceTrack,
    val nutritionTrack: NutritionTrack?,
    val physioTrack: PhysioTrack?
)

data class NutritionTrack(
    val calories: Int?=0,
    val proteins: Int?=0,
    val lipids: Int?=0,
    val carbohydrates: Int?=0,
    val fiber: Int?=0,
    val cafeins: Int?=0
)

data class PhysioTrack(
    val weight: Double?=70.0,
    val step: Int?=0,
    val hourOfSleep: Double?=8.0,
    val mood: String?=""
)
data class SeanceTrack(
    val startHour: StartHour?,
    val presetSeanceId: Long? ,
    val plannedExercises: List<PlannedExerciseVO>
)

data class StartHour(
    val hour: Int,
    val minute: Int,
    val second: Int?,
    val nano: Int?
)

data class PlannedExerciseVO(
    val id : Long?,
    val exerciseOrder: Int,
    val exerciseId: Long,
    val lateraliteId: Long,
    val equipmentId: Long,
    val sets: List<SetVO>? = emptyList()
)

data class SetVO(
    val setOrder: Int,
    val repsNumber: Int,
    val rir: Int,
    val charge: Double,
    val typeOfSet: SetType = SetType.EFFECTIVE,
    val side : SetSide= SetSide.BOTH
)
