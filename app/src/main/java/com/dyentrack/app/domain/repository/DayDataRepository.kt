package com.dyentrack.app.domain.repository

import com.dyentrack.app.domain.model.DayDataRequest
import kotlinx.coroutines.flow.StateFlow

interface DayDataRepository {
    val dayDataFlow: StateFlow<List<DayDataRequest>>
    suspend fun fetchDayData()
    suspend fun updateDayData(dayData: DayDataRequest):Int
}