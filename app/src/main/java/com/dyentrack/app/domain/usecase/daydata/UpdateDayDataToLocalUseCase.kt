package com.dyentrack.app.domain.usecase.daydata

import com.dyentrack.app.domain.model.enums.TypeOfAction
import com.dyentrack.app.domain.model.dayData.DayDataRequest
import com.dyentrack.app.domain.repository.DayDataRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateDayDataToLocalUseCase @Inject constructor(private val repository: DayDataRepository) {
    suspend operator fun invoke(dayData: DayDataRequest, typeOfAction: TypeOfAction) {
        repository.updateToLocal(dayData, null, typeOfAction)
    }
}