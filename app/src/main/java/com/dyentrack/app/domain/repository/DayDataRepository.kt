package com.dyentrack.app.domain.repository

import com.dyentrack.app.domain.model.DayDataRequest
import kotlinx.coroutines.flow.StateFlow

interface DayDataRepository {
    val dayDataFlow: StateFlow<List<DayDataRequest>?>
    suspend fun updateToLocal(dayData: DayDataRequest, dateSave: String?,takeExistingExercise: Boolean? )
    suspend fun fetchDayData()
    suspend fun updateToApi(dayData: DayDataRequest): Int
}