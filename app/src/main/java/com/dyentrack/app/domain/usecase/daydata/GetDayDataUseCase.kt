package com.dyentrack.app.domain.usecase.daydata

import com.dyentrack.app.domain.model.dayData.DayDataRequest
import com.dyentrack.app.domain.model.enum.SaveType
import com.dyentrack.app.domain.repository.DayDataRepository
import javax.inject.Singleton
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@Singleton
class GetDayDataUseCase @Inject constructor(
    private val repository: DayDataRepository
) {
    val dayDataFlow: StateFlow<List<DayDataRequest>?> = repository.dayDataFlow
    val apiFetchStatus: StateFlow<SaveType> = repository.apiFetchStatus

    suspend operator fun invoke() {
        repository.fetchDayData()
    }
}