package com.dyentrack.app.domain.repository

import com.dyentrack.app.domain.model.InfoExerciseUser
import kotlinx.coroutines.flow.StateFlow


interface InfoExerciseUserRepository {
    val infoExerciseUserFlow: StateFlow<Map<Long, InfoExerciseUser>>
    suspend fun fetchInfoExerciseUser()
    suspend fun updateInfoExerciseUser(infoExerciseUser: InfoExerciseUser): Int
}
