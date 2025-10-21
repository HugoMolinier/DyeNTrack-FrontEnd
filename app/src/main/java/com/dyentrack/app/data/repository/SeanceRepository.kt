package com.dyentrack.app.data.repository

import com.dyentrack.app.data.network.ApiInterface
import com.dyentrack.app.domain.model.ApiResponse
import com.dyentrack.app.domain.model.DayDataRequest
import com.dyentrack.app.domain.model.MasterDataStatic
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SeanceRepository @Inject constructor(
    private val api: ApiInterface
) {

    suspend fun saveDayData(dayDataRequest: DayDataRequest): ApiResponse<DayDataRequest> {
        return api.saveDayData(dayDataRequest)
    }
}
