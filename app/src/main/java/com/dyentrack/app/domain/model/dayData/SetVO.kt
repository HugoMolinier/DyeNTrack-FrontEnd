package com.dyentrack.app.domain.model.dayData

import com.dyentrack.app.domain.model.SetSide
import com.dyentrack.app.domain.model.SetType
import kotlinx.serialization.Serializable

@Serializable
data class SetVO(
    val setOrder: Int,
    val repsNumber: Int,
    val rir: Int,
    val charge: Double,
    val typeOfSet: SetType = SetType.EFFECTIVE,
    val side : SetSide = SetSide.BOTH
)
