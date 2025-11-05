package com.dyentrack.app.domain.model

data class SetData(
    var type: SetType = SetType.EFFECTIVE,
    var charge: Double ?,
    var rir: Int?,
    var reps: Int?,
    var side:SetSide= SetSide.BOTH,
    var linkedRightSet: SetData? = null
)

enum class SetType {
    EFFECTIVE,
    TEMPO,
    WARMUP
}

enum class SetSide {
    LEFT, RIGHT, BOTH
}
