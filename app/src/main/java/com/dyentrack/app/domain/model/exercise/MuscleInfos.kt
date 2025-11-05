package com.dyentrack.app.domain.model.exercise

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MuscleInfos(
    val idMuscle : Long,
    val principal: Boolean

): Parcelable
