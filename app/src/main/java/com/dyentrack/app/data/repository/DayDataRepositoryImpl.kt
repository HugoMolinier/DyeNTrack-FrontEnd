package com.dyentrack.app.data.repository

import android.util.Log
import com.dyentrack.app.data.local.database.SeanceDao
import com.dyentrack.app.data.local.entities.SeanceOfDayLocal
import com.dyentrack.app.data.network.ApiInterface
import com.dyentrack.app.domain.model.DayDataRequest
import com.dyentrack.app.domain.model.SeanceTrack
import com.dyentrack.app.domain.repository.DayDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DayDataRepositoryImpl @Inject constructor(
    private val api: ApiInterface,
    private val seanceDao: SeanceDao
) : DayDataRepository {
    private val _dayDataFlow = MutableStateFlow<List<DayDataRequest>?>(null)
    override val dayDataFlow: StateFlow<List<DayDataRequest>?> = _dayDataFlow

    override suspend fun fetchDayData() {
        val apiDayData = try {
            val apiResponse = api.getAllDayDataByDay()
            if (apiResponse.status == 200) apiResponse.data else emptyList()
        } catch (_: Exception) {
            emptyList()
        }
        //save Api into local
        apiDayData.forEach { apiDayData ->
            seanceDao.saveOrUpdateSeance(
                seanceDao,
                apiDayData.toSeanceOfDayLocal(true)
            )
        }

        //!ApiData -> localData
        val localDayData = if (apiDayData.isEmpty()) {
            seanceDao.getAllSeances().map {
                it.toDayDataRequest()
            }
        } else emptyList()

        _dayDataFlow.value = apiDayData + localDayData

    }

    override suspend fun updateDayData(dayData: DayDataRequest): Int {
        var isSaveApi = false
        try {

            val response = api.saveDayData(dayData)
            isSaveApi = response.status in listOf(200, 201)
        } catch (e: Exception) {
            Log.d("Fail API", "$e.message")
        }
        seanceDao.saveOrUpdateSeance(seanceDao, dayData.toSeanceOfDayLocal(isSaveApi))

        val currentList = _dayDataFlow.value?.toMutableList() ?: mutableListOf()
        val index = currentList.indexOfFirst { it.dayData == dayData.dayData }
        if (index != -1) {
            currentList[index] = dayData
        } else {
            currentList.add(dayData)
        }
        _dayDataFlow.value = currentList
        return if (isSaveApi) 200 else 100


    }

    private fun DayDataRequest.toSeanceOfDayLocal(isSaveApi: Boolean): SeanceOfDayLocal {

        return SeanceOfDayLocal(
            day = dayData,
            saveAPI = isSaveApi,
            plannedExercises = seanceTrack.plannedExercises
        )

    }

    private fun SeanceOfDayLocal.toDayDataRequest(): DayDataRequest {
        return DayDataRequest(
            null,
            day,
            seanceTrack = SeanceTrack(
                startHour = null,
                presetSeanceId = null,
                plannedExercises = plannedExercises
            ),
            null,
            null
        )
    }
}