package com.dyentrack.app.domain.usecase

import com.dyentrack.app.domain.model.MuscleRate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetRadarDataUseCase @Inject constructor() {


    operator fun invoke(
        data: Map<String, Map<Long, MuscleRate>>, labelsMap: Map<Long, String>
    ): Triple<List<String>, List<Float>, List<Float>> {
        val totalSets = mutableMapOf<Long, Float>()
        val totalExercises = mutableMapOf<Long, Float>()

        data.values.forEach { dayStat ->
            dayStat.forEach { (id, mr) ->
                totalSets[id] = (totalSets[id] ?: 0f) + mr.numberSet.toFloat()
                totalExercises[id] = (totalExercises[id] ?: 0f) + mr.numberExercise.toFloat()
            }
        }

        val labels = labelsMap.values.toList()
        val sets = labelsMap.keys.map { totalSets[it] ?: 0f }
        val exercises = labelsMap.keys.map { totalExercises[it] ?: 0f }

        return Triple(labels, exercises, sets)
    }
}
