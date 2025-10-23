package com.dyentrack.app.domain.usecase

import com.dyentrack.app.data.repository.SeanceRepository
import com.dyentrack.app.domain.model.ApiResponse
import com.dyentrack.app.domain.model.DayDataRequest
import com.dyentrack.app.domain.model.MasterDataStatic
import javax.inject.Inject

class GetDayDataUseCase @Inject constructor(
    private val repository: SeanceRepository
) {
    suspend operator fun invoke(): ApiResponse<List<DayDataRequest>> {
        return repository.getAllDayData()
    }
}