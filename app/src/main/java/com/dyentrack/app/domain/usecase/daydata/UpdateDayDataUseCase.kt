package com.dyentrack.app.domain.usecase.daydata

import com.dyentrack.app.domain.model.DayDataRequest
import com.dyentrack.app.domain.repository.DayDataRepository
import javax.inject.Inject

class UpdateDayDataUseCase @Inject constructor(
    private val repository: DayDataRepository
) {
    suspend operator fun invoke(dayData: DayDataRequest): Int {
        return repository.updateDayData(dayData)
    }
}