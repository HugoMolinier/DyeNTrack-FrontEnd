package com.dyentrack.app.domain.usecase.daydata

import com.dyentrack.app.data.local.entities.SeanceOfDayLocal
import com.dyentrack.app.domain.model.ApiResponse
import com.dyentrack.app.domain.model.DayDataRequest
import com.dyentrack.app.domain.model.SeanceTrack
import com.dyentrack.app.domain.repository.APISeanceRepository
import com.dyentrack.app.domain.repository.LocalSeanceRepository
import javax.inject.Inject

class GetDayDataUseCase @Inject constructor(
    private val repositoryApi: APISeanceRepository,
    private val repositoryLocal: LocalSeanceRepository
) {
    suspend operator fun invoke(): ApiResponse<List<DayDataRequest>> {
        val response = try {

            repositoryApi.getAllDayData()}
        catch (_: Exception) {

            null
        }

        if ( (response?.data as? List<*>)?.isNotEmpty() == true) {
            response.data.forEach { dayDataRequest ->
                val seanceLocal = repositoryLocal.getSeanceByDay(dayDataRequest.dayData)
                val seance = dayDataRequest.toSeanceOfDayLocal(isSaveApi = true)
                if (seanceLocal == null) {
                    repositoryLocal.insertSeance(seance)
                } else {

                    repositoryLocal.updateSeance(seance)
                }
            }
        }else{
                val localData = repositoryLocal.getAllSeances()
                return ApiResponse(data =  localData.map { it.toDayDataRequest() },status = 100, message = "Api data")
            }

        return response
    }


    private fun DayDataRequest.toSeanceOfDayLocal(isSaveApi: Boolean): SeanceOfDayLocal {

        return SeanceOfDayLocal(
            day = dayData,
            saveAPI = isSaveApi,
            plannedExercises = seanceTrack.plannedExercises
        )

    }
    private fun SeanceOfDayLocal.toDayDataRequest(): DayDataRequest {
        return DayDataRequest(null,day, seanceTrack = SeanceTrack(startHour = null, presetSeanceId = null,plannedExercises=plannedExercises),null,null)
    }

}