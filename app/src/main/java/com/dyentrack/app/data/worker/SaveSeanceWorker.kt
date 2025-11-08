package com.dyentrack.app.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dyentrack.app.domain.model.DayDataRequest
import com.dyentrack.app.domain.usecase.daydata.UpdateDayDataUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.serialization.json.Json

@HiltWorker
class SaveSeanceWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val updateDayDataUseCase: UpdateDayDataUseCase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val json = inputData.getString("payload") ?: return Result.failure()
        return try {
            val request = Json .decodeFromString<DayDataRequest>(json)
            val response = updateDayDataUseCase(request)
            if (response in 200..201) Result.success() else Result.retry()
        } catch (_: Exception) {
            Result.retry()
        }
    }
}

