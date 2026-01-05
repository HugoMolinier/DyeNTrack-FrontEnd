package com.dyentrack.app.presentation.screens.preset

import com.dyentrack.app.domain.model.MasterDataMaps
import com.dyentrack.app.domain.model.enum.BottomSheetVisibleType
import com.dyentrack.app.domain.model.preset.Preset

data class PresetUiState (
    val expandedExerciseId: Long? = null,
    val exerciseToAdd: List<Long> = emptyList(),
    val currentPreset: Preset?=null,
    val isBottomSheetVisible: BottomSheetVisibleType = BottomSheetVisibleType.NO,

    val masterDataMaps: MasterDataMaps? = MasterDataMaps(),
    )
