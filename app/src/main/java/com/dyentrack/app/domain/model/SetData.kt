package com.dyentrack.app.domain.model

import java.util.UUID

data class SetData(
    val id: String = UUID.randomUUID().toString(),
    val type: SetType = SetType.EFFECTIVE,
    val charge: Double?,
    val rir: Int?,
    val reps: Int?,
    val side: SetSide = SetSide.BOTH,
    val linkedRightSet: SetData? = null
)

sealed class SetField {
    data class Reps(val value: Int?) : SetField()
    data class Rir(val value: Int?) : SetField()
    data class Charge(val value: Double?) : SetField()
}


enum class SetType {
    EFFECTIVE, TEMPO, WARMUP
}

enum class SetSide {
    LEFT, RIGHT, BOTH
}
