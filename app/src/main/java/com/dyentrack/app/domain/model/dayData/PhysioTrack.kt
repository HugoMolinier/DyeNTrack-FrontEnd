package com.dyentrack.app.domain.model.dayData


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
