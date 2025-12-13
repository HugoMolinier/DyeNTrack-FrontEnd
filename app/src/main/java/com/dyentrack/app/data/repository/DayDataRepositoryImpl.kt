package com.dyentrack.app.data.repository

import android.util.Log
import com.dyentrack.app.data.local.database.SeanceDao
import com.dyentrack.app.data.local.entities.SeanceOfDayLocal
import com.dyentrack.app.data.network.ApiInterface
import com.dyentrack.app.domain.model.DayDataRequest
import com.dyentrack.app.domain.model.NutritionTrack
import com.dyentrack.app.domain.model.PhysioTrack
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
        // 1. récupère le local
        val localDayDataBrut = seanceDao.getAllSeances()
        val localDayData = localDayDataBrut.map { it.toDayDataRequest() }
        _dayDataFlow.value = localDayData

        // 2. push local modifié offline vers l'API
        localDayDataBrut.forEach { localDay ->
            if (!localDay.saveAPI) {
                val dayDataRequest = localDayData.find { it.dayData == localDay.day }
                dayDataRequest?.let {
                    updateToApi(it)
                }
            }
        }
        // 3. récupère l'API
        val apiDayData = try {
            val apiResponse = api.getAllDayDataByDay()
            if (apiResponse.status == 200) apiResponse.data else emptyList()
        } catch (_: Exception) {
            emptyList()
        }
        Log.d("d,lakda","$apiDayData")
        // 4. merge local et API
        val merged = mergeLocalAndApi(localDayData, apiDayData)
        _dayDataFlow.value = merged

        merged.forEach { dayData ->
            val isFromApi = apiDayData.any { it.dayData == dayData.dayData }
            seanceDao.saveOrUpdateSeance(seanceDao, dayData.toSeanceOfDayLocal(isFromApi))
        }

    }


    private fun mergeLocalAndApi(
        local: List<DayDataRequest>,
        api: List<DayDataRequest>
    ): List<DayDataRequest> {
        val result = mutableListOf<DayDataRequest>()
        val apiMap = api.associateBy { it.dayData }

        local.forEach { localDay ->
            Log.d("localDay","$localDay")
            val apiDay = apiMap[localDay.dayData]
            result.add(
                if (apiDay != null) {
                    if (!localDay.toSeanceOfDayLocal(false).saveAPI) localDay else apiDay
                } else localDay
            )
        }

        // jours uniquement API
        val localDays = local.map { it.dayData }.toSet()
        api.filter { it.dayData !in localDays }.forEach { result.add(it) }

        return result
    }



    override suspend fun updateToApi(dayData: DayDataRequest): Int {
        return try {
            val response = api.saveDayData(dayData)
            if (response.status in listOf(200, 201)) {
                seanceDao.updateSaveApi(dayData.dayData, true)
                200
            } else {
                Log.d("Api response Error","$response")
                100
            }
        } catch (e: Exception) {
            Log.d("Api response Error","$e")
            100
        }
    }


    override suspend fun updateToLocal(dayData: DayDataRequest) {
        val existing = seanceDao.getSeanceByDay(dayData.dayData)
        val seanceLocal = dayData.toSeanceOfDayLocal(false, existing)
        seanceDao.saveOrUpdateSeanceWithoutReplaceNull(seanceDao, seanceLocal)

        val updatedLocal = seanceDao.getAllSeances().map { it.toDayDataRequest() }
        _dayDataFlow.value = updatedLocal.map { it.copy() }
    }


    fun DayDataRequest.toSeanceOfDayLocal(
        isSaveApi: Boolean,
        existing: SeanceOfDayLocal? = null
    ): SeanceOfDayLocal {
        return SeanceOfDayLocal(
            day = dayData,
            saveAPI = isSaveApi,
            nutritionTrack = nutritionTrack ?: existing?.nutritionTrack,
            physioTrack = physioTrack ?: existing?.physioTrack,
            plannedExercises = if (seanceTrack?.plannedExercises.isNullOrEmpty()) {
                existing?.plannedExercises ?: emptyList()
            } else {
                seanceTrack.plannedExercises
            }
        )
    }


     fun SeanceOfDayLocal.toDayDataRequest(): DayDataRequest {
        return DayDataRequest(
            null,
            day,
            seanceTrack = SeanceTrack(
                startHour = null,
                presetSeanceId = null,
                plannedExercises = plannedExercises
            ),
            nutritionTrack?: NutritionTrack(),
            physioTrack?: PhysioTrack()
        )
    }
}