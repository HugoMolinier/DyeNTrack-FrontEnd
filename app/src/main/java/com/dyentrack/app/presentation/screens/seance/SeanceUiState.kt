package com.dyentrack.app.presentation.screens.seance

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateListOf

import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.dyentrack.app.domain.model.MasterDataStatic
import com.dyentrack.app.domain.model.SetData
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class SeanceUiState(
    val masterDataStatic: MasterDataStatic = MasterDataStatic(emptyList(), emptyList(), emptyList(), emptyList(), emptyList()),

    val seancesParJour: MutableMap<String, SeanceDuJour> = mutableStateMapOf(),

    val exerciseToAdd: List<Long> = emptyList(),
    val daySelected: String= LocalDate.now().format(DateTimeFormatter.ISO_DATE),

    val expandedExerciseId: Long? = null,
    val isBottomSheetVisible: Boolean = false,
    val searchQuery: String = "",



    // résultats d’analyse
    val showAnalyseData:Boolean = false,
    val analyseData: Map<String, List<SetData>> = emptyMap()


)

data class SeanceDuJour(
    val exerciseIds: SnapshotStateList<Long> = mutableStateListOf(),
    val save: Boolean = false,
    val setsMap: SnapshotStateMap<Long, SnapshotStateList<SetData>> = mutableStateMapOf(),
    val note: String?=null,
)


