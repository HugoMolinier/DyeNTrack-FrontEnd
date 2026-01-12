package com.dyentrack.app.domain.usecase.health

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.NutritionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.dyentrack.app.domain.model.dayData.NutritionTrack
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetDataHealthUseCase @Inject constructor() {

    suspend fun aggregateSteps(
        healthConnectClient: HealthConnectClient, startTime: Instant, endTime: Instant
    ): Long {
        return try {
            val response = healthConnectClient.aggregate(
                AggregateRequest(
                    metrics = setOf(StepsRecord.COUNT_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
            response[StepsRecord.COUNT_TOTAL] ?: 0L
        } catch (_: Exception) {
            0L
        }
    }


    suspend fun getNutritionRecords(
        healthConnectClient: HealthConnectClient, startTime: Instant, endTime: Instant
    ): List<NutritionRecord> {
        val records: List<NutritionRecord> = try {
            val request = ReadRecordsRequest(
                recordType = NutritionRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
            val response = healthConnectClient.readRecords(request)
            response.records
        } catch (_: Exception) {
            emptyList()
        }

        return records
    }


    suspend fun nutritionData(
        healthConnectClient: HealthConnectClient, startTime: Instant, endTime: Instant
    ): NutritionTrack {
        val records = getNutritionRecords(healthConnectClient, startTime, endTime)

        var calories = 0.0
        var proteins = 0.0
        var lipids = 0.0
        var carbs = 0.0
        var fiber = 0.0
        var cafeins = 0.0

        for (record in records) {
            // Certaines unités peuvent être null
            calories += record.energy?.inKilocalories ?: 0.0
            proteins += record.protein?.inGrams ?: 0.0
            lipids += record.totalFat?.inGrams ?: 0.0
            carbs += record.totalCarbohydrate?.inGrams ?: 0.0
            fiber += record.dietaryFiber?.inGrams ?: 0.0
            cafeins += record.caffeine?.inMilligrams ?: 0.0
        }

        return NutritionTrack(
            calories = calories.toInt(),
            proteins = proteins.toInt(),
            lipids = lipids.toInt(),
            carbohydrates = carbs.toInt(),
            fiber = fiber.toInt(),
            cafeins = cafeins.toInt()
        )
    }

}
