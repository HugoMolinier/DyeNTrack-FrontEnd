package com.dyentrack.app.domain.model.dayData

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