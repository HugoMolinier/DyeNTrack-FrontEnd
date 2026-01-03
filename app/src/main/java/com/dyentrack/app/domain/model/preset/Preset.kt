package com.dyentrack.app.domain.model.preset

data class Preset(
    val id_preset_seance: Long?,
    val name :String,
    val id_creator:Long?, //futur voir les preset Public
    val presetSeanceExerciseVOs: List<ExerciceInPreset>
)
