package com.dyentrack.app.domain.repository

import com.dyentrack.app.domain.model.ApiResponse
import com.dyentrack.app.domain.model.DayDataRequest

interface APISeanceRepository {
    suspend fun saveDayData(dayDataRequest: DayDataRequest): ApiResponse<DayDataRequest>
    suspend fun getAllDayData(): ApiResponse<List<DayDataRequest>>
}