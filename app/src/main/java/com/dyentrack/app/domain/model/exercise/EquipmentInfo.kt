package com.dyentrack.app.domain.model.exercise

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EquipmentInfo(
    val idEquipment : Long,
    //val principal: Boolean? //pour futur création
): Parcelable
