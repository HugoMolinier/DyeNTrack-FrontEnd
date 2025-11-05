package com.dyentrack.app.domain.usecase.daydata

import com.dyentrack.app.data.local.entities.SeanceOfDayLocal
import com.dyentrack.app.domain.model.ApiResponse
import com.dyentrack.app.domain.model.DayDataRequest
import com.dyentrack.app.domain.repository.APISeanceRepository
import com.dyentrack.app.domain.repository.LocalSeanceRepository
import javax.inject.Inject

class SaveDayDataUseCase @Inject constructor(
    private val repositoryApi: APISeanceRepository,
    private val repositoryLocal: LocalSeanceRepository
) {
    suspend operator fun invoke(dayDataRequest: DayDataRequest): ApiResponse<DayDataRequest>? {
        val seance = dayDataRequest.toSeanceOfDayLocal()
        repositoryLocal.saveOrUpdateSeance(seance)
        val response = try {
            val apiResponse = repositoryApi.saveDayData(dayDataRequest)

            apiResponse
        } catch (e: Exception) {
            ApiResponse(data = dayDataRequest, message = "timeout $e", status = 404)
        }

        return response
    }

    private fun DayDataRequest.toSeanceOfDayLocal(): SeanceOfDayLocal {
        return SeanceOfDayLocal(
            day = dayData,
            plannedExercises = seanceTrack.plannedExercises
        )
    }
}