package com.dyentrack.app.domain.model.exercise

import com.dyentrack.app.domain.model.HasLocalizedDescription
import com.dyentrack.app.domain.model.HasLocalizedName

data class Exercise(
    val idExercise : Long,
    override val nameFR : String,
    override val nameEN : String,
    val mainFocusGroup: Long?=null,
    val mainMuscle:Long?=null,
    override val descriptionFR: String?="",
    override val descriptionEN: String?="",
    val muscleInfos : List<MuscleInfos>?=listOf(),
    val defaultEquipment: Long?=null,
    val equipmentInfo:List<EquipmentInfo>?=listOf(),
    val linkVideo:String?=null,
    val idCreator: Long?=null
): HasLocalizedName, HasLocalizedDescription