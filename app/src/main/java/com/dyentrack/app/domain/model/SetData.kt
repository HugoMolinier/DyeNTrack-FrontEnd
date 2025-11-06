package com.dyentrack.app.domain.model

import kotlinx.serialization.Serializable

data class SetData(
    var type: SetType = SetType.EFFECTIVE,
    var charge: Double ?,
    var rir: Int?,
    var reps: Int?,
    var side:SetSide= SetSide.BOTH,
    var linkedRightSet: SetData? = null
)

@Serializable
enum class SetType {
    EFFECTIVE,
    TEMPO,
    WARMUP
}

@Serializable
enum class SetSide {
    LEFT, RIGHT, BOTH
}
