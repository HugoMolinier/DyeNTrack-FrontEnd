package com.dyentrack.app.data.repository

import com.dyentrack.app.data.network.ApiInterface
import com.dyentrack.app.domain.model.ApiResponse
import com.dyentrack.app.domain.model.DayDataRequest
import com.dyentrack.app.domain.repository.APISeanceRepository
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class APISeanceRepositoryImpl @Inject constructor(
    private val api: ApiInterface
): APISeanceRepository {

    override suspend fun saveDayData(dayDataRequest: DayDataRequest): ApiResponse<DayDataRequest> {
        return api.saveDayData(dayDataRequest)
    }

    override suspend fun getAllDayData(): ApiResponse<List<DayDataRequest>> {
        return api.getAllDayDataByDay()
    }
}
