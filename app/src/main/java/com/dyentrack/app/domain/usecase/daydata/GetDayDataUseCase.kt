package com.dyentrack.app.domain.usecase.daydata

import com.dyentrack.app.domain.model.dayData.DayDataRequest
import com.dyentrack.app.domain.model.enums.SaveType
import com.dyentrack.app.domain.repository.DayDataRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetDayDataUseCase @Inject constructor(
    private val repository: DayDataRepository
) {
    val dayDataFlow: StateFlow<List<DayDataRequest>?> = repository.dayDataFlow
    val apiFetchStatus: StateFlow<SaveType> = repository.apiFetchStatus
    val dayDataReady: StateFlow<Boolean> = repository.dayDataReady

    suspend operator fun invoke() {
        repository.fetchDayData()
    }
}