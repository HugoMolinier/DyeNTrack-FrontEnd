package com.dyentrack.app.domain.usecase.daydata

import com.dyentrack.app.domain.model.dayData.DayDataRequest
import com.dyentrack.app.domain.model.TypeOfAction
import com.dyentrack.app.domain.repository.DayDataRepository
import javax.inject.Singleton
import javax.inject.Inject

@Singleton
class UpdateDayDataToLocalUseCase @Inject constructor(private val repository: DayDataRepository) {
    suspend operator fun invoke(dayData: DayDataRequest,typeOfAction: TypeOfAction) {
        repository.updateToLocal(dayData,null,typeOfAction)
    }
}