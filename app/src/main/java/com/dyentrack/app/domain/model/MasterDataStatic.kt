package com.dyentrack.app.domain.model

import com.dyentrack.app.domain.model.exercise.Exercise
import java.time.Instant

data class MasterDataStatic(
    val musclesGroup: List<ReturnIdAndName>,
    val muscles: List<ReturnIdAndName>,
    val officialExercise: List<Exercise>,
    val lateralites: List<ReturnIdAndName>,
    val equipment: List<ReturnIdAndName>,
    val dateEnregistement: String = Instant.now().toString(),

    )
