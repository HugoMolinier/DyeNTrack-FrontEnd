package com.dyentrack.app.presentation.screens.seance

import com.dyentrack.app.domain.model.MasterDataStatic
import com.dyentrack.app.domain.model.SetData

data class SeanceUiState(
    val masterDataStatic: MasterDataStatic = MasterDataStatic(emptyList(), emptyList(), emptyList(), emptyList(), emptyList()),
    val exerciseOfTheSeance: List<Long> = emptyList(),
    val exerciseToAdd: List<Long> = emptyList(),
    val expandedExerciseId: Long? = null,
    val setsMap: Map<Long, List<SetData>> = emptyMap(),
    val isBottomSheetVisible: Boolean = false,
    val searchQuery: String = ""
)