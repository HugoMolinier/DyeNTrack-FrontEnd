package com.dyentrack.app.domain.usecase.daydata

import com.dyentrack.app.domain.model.DayDataRequest
import com.dyentrack.app.domain.repository.DayDataRepository
import javax.inject.Singleton
import javax.inject.Inject

@Singleton
class GetDayDataDayLocalUseCase @Inject constructor(
    private val repository: DayDataRepository
) {

    suspend operator fun invoke(day:String):DayDataRequest? {
        return repository.getDayLocal(day)
    }
}