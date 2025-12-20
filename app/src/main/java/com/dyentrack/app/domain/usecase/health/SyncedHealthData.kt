package com.dyentrack.app.domain.usecase.health

import androidx.health.connect.client.HealthConnectClient
import com.dyentrack.app.domain.model.DayDataRequest
import com.dyentrack.app.domain.model.NutritionTrack
import com.dyentrack.app.domain.model.PhysioTrack
import com.dyentrack.app.domain.model.TypeOfAction
import com.dyentrack.app.domain.usecase.daydata.UpdateDayDataToApiUseCase
import com.dyentrack.app.domain.usecase.daydata.UpdateDayDataToLocalUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneOffset
import javax.inject.Inject

data class SyncedHealthData(
    val steps: Int,
    val nutritionTrack: NutritionTrack
)

class SyncHealthDataUseCase @Inject constructor(
    private val getDataHealthUseCase: GetDataHealthUseCase,
    private val updateDayDataToLocalUseCase: UpdateDayDataToLocalUseCase,
    private val updateDayDataToApiUseCase: UpdateDayDataToApiUseCase
) {

    suspend operator fun invoke(client: HealthConnectClient): SyncedHealthData {
        return withContext(Dispatchers.IO) {
            val today = LocalDate.now()
            val start = today.atStartOfDay(ZoneOffset.UTC).toInstant()
            val end = today.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant()

            // Récupération des données Health Connect
            val steps = getDataHealthUseCase.aggregateSteps(client, start, end).toInt()
            val nutrition = getDataHealthUseCase.nutritionData(client, start, end)

            if(steps !=0 ||nutrition.isNotNull) {
                // Création ou mise à jour du DayDataRequest local
                val dayData = DayDataRequest(
                    idDayData = null,
                    dayData = today.toString(),
                    physioTrack = PhysioTrack(step = steps),
                    seanceTrack = null,
                    nutritionTrack = nutrition
                )

                updateDayDataToLocalUseCase(dayData, TypeOfAction.UPDATEHEALTH)
                updateDayDataToApiUseCase(dayData)
            }
            SyncedHealthData(
                steps = steps,
                nutritionTrack = nutrition
            )
        }
    }
}
