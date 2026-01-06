package com.dyentrack.app.presentation.screens.preset

import com.dyentrack.app.domain.model.MasterDataMaps
import com.dyentrack.app.domain.model.enum.BottomSheetVisibleType
import com.dyentrack.app.domain.model.enum.ExerciseMode
import com.dyentrack.app.domain.model.preset.Preset

data class PresetUiState (
    val expandedExerciseId: Long? = null,
    val exerciseToAdd: List<Long> = emptyList(),
    val originalPreset: Preset? = null,
    val currentPreset: Preset? = null,
    val isBottomSheetVisible: BottomSheetVisibleType = BottomSheetVisibleType.NO,
    val exerciseMode: ExerciseMode = ExerciseMode.LOCK, //Lock Par Défaut
    val masterDataMaps: MasterDataMaps? = MasterDataMaps(),
    ){
    val isModified: Boolean
        get() = originalPreset != currentPreset
}
