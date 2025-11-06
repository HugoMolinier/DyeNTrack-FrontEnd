package com.dyentrack.app.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dyentrack.app.domain.model.DayDataRequest
import com.dyentrack.app.domain.usecase.daydata.SaveDayDataUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.serialization.json.Json

@HiltWorker
class SaveSeanceWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val saveDayDataUseCase: SaveDayDataUseCase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val json = inputData.getString("payload") ?: return Result.failure()
        return try {
            val request = Json .decodeFromString<DayDataRequest>(json)
            val response = saveDayDataUseCase(request)
            if (response?.status in 200..201) Result.success() else Result.retry()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

