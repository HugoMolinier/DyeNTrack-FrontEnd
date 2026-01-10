package com.dyentrack.app.domain.repository

import com.dyentrack.app.domain.model.dayData.DayDataRequest
import com.dyentrack.app.domain.model.TypeOfAction
import com.dyentrack.app.domain.model.enum.SaveType
import kotlinx.coroutines.flow.StateFlow

interface DayDataRepository {
    val dayDataFlow: StateFlow<List<DayDataRequest>?>
    val apiFetchStatus: StateFlow<SaveType>
    suspend fun updateToLocal(dayData: DayDataRequest, dateSave: String?,typeOfAction: TypeOfAction)
    suspend fun fetchDayData()
    suspend fun updateToApi(dayData: DayDataRequest): Int
    suspend fun getDayLocal(day:String): DayDataRequest?
    suspend fun cleanDB()

}