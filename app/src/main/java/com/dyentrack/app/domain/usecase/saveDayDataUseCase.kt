package com.dyentrack.app.domain.usecase

import com.dyentrack.app.data.repository.SeanceRepository
import com.dyentrack.app.domain.model.ApiResponse
import com.dyentrack.app.domain.model.DayDataRequest
import com.dyentrack.app.domain.model.MasterDataStatic
import javax.inject.Inject

class SaveDayDataUseCase @Inject constructor(
    private val repository: SeanceRepository
) {
    suspend operator fun invoke(dayDataRequest: DayDataRequest): ApiResponse<DayDataRequest> {
        return repository.saveDayData(dayDataRequest)
    }
}