package com.dyentrack.app.domain.model

data class SetData(
    var type: SetType = SetType.EFFECTIVE,
    var charge: Double ?,
    var rir: Int?,
    var reps: Int?
)

enum class SetType {
    EFFECTIVE,
    TEMPO,
    WARMUP
}
