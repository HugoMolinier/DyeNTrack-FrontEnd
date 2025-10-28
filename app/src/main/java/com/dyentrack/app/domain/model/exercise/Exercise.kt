package com.dyentrack.app.domain.model.exercise

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Exercise(
    val idExercise : Long,
    val nameFR : String,
    val mainFocusGroup: Long?=null,
    val description: String?="",
    val muscleInfos : List<MuscleInfos>?=listOf(),
    val linkVideo:String?=null,
    val idCreator: String?=null
): Parcelable