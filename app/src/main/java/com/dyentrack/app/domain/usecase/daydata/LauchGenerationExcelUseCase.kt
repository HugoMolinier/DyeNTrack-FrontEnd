package com.dyentrack.app.domain.usecase.daydata

import com.dyentrack.app.domain.model.RequestState
import com.dyentrack.app.domain.repository.DayDataRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LauchGenerationExcelUseCase @Inject constructor(
    private val repository: DayDataRepository
) {

    suspend operator fun invoke(startDate:String?,endDate:String?):RequestState {
        return repository.launchGenerationExcel(startDate,endDate)
    }
}