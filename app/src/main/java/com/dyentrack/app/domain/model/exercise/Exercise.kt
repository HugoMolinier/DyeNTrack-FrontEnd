package com.dyentrack.app.domain.model.exercise

import android.os.Parcelable
import com.dyentrack.app.domain.model.HasLocalizedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Exercise(
    val idExercise : Long,
    override val nameFR : String,
    override val nameEN : String,
    val mainFocusGroup: Long?=null,
    val description: String?="",
    val muscleInfos : List<MuscleInfos>?=listOf(),
    val linkVideo:String?=null,
    val idCreator: Long?=null
): HasLocalizedName,Parcelable