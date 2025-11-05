package com.dyentrack.app.domain.repository

import com.dyentrack.app.domain.model.InfoExerciseUser


interface InfoExerciseUserRepository {
    suspend fun saveInfoExerciseUser(infoExerciseUser: InfoExerciseUser): InfoExerciseUser
    suspend fun getInfoExerciseUser(): List<InfoExerciseUser>
}
