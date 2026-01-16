package com.dyentrack.app.domain.usecase.health

import androidx.health.connect.client.HealthConnectClient
import com.dyentrack.app.domain.model.enums.TypeOfAction
import com.dyentrack.app.domain.model.dayData.DayDataRequest
import com.dyentrack.app.domain.model.dayData.NutritionTrack
import com.dyentrack.app.domain.model.dayData.PhysioTrack
import com.dyentrack.app.domain.usecase.daydata.GetDayDataDayLocalUseCase
import com.dyentrack.app.domain.usecase.daydata.UpdateDayDataToApiUseCase
import com.dyentrack.app.domain.usecase.daydata.UpdateDayDataToLocalUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

data class SyncedHealthData(
    val steps: Int, val nutritionTrack: NutritionTrack
)

@Singleton
class SyncHealthDataUseCase @Inject constructor(
    private val getDataHealthUseCase: GetDataHealthUseCase,
    private val updateDayDataToLocalUseCase: UpdateDayDataToLocalUseCase,
    private val updateDayDataToApiUseCase: UpdateDayDataToApiUseCase,
    private val getDayDataDayLocalUseCase: GetDayDataDayLocalUseCase
) {

    suspend operator fun invoke(
        client: HealthConnectClient,
        daySelected: LocalDate
    ): SyncedHealthData {
        return withContext(Dispatchers.IO) {
            val start = daySelected.atStartOfDay(ZoneOffset.UTC).toInstant()
            val end = daySelected.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant()

            // Récupération des données Health Connect
            val steps = getDataHealthUseCase.aggregateSteps(client, start, end).toInt()
            val nutrition = getDataHealthUseCase.nutritionData(client, start, end)
            val currentSave = getDayDataDayLocalUseCase.invoke(daySelected.toString())

            if (steps != 0 || nutrition.isNotNull) {
                val newOccuDayPhysio = if (currentSave?.physioTrack?.step == steps) null
                else PhysioTrack(step = steps)

                val newOccuDayNutrition =
                    if (currentSave?.nutritionTrack?.equalsIgnoreNull(nutrition) == true) null else nutrition
                if (newOccuDayPhysio != null || newOccuDayNutrition != null) {
                    val dayData = DayDataRequest(
                        idDayData = null,
                        dayData = daySelected.toString(),
                        physioTrack = newOccuDayPhysio,
                        seanceTrack = null,
                        nutritionTrack = newOccuDayNutrition
                    )

                    updateDayDataToLocalUseCase(dayData, TypeOfAction.UPDATEHEALTH)
                    updateDayDataToApiUseCase(dayData)
                }
            }
            SyncedHealthData(
                steps = steps, nutritionTrack = nutrition
            )
        }
    }
}
