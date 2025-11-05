package com.dyentrack.app.data.repository

import android.util.Log
import com.dyentrack.app.data.network.ApiInterface
import com.dyentrack.app.data.network.dto.InfoExerciseUserContentRequest
import com.dyentrack.app.domain.model.InfoExerciseUser
import com.dyentrack.app.domain.repository.InfoExerciseUserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InfoExerciseUserRepositoryImpl @Inject constructor(
    private val api: ApiInterface
) : InfoExerciseUserRepository {

    override suspend fun saveInfoExerciseUser(infoExerciseUser: InfoExerciseUser): InfoExerciseUser {

        val response =  api.updateInfoExerciseUser(infoExerciseUser.idExercise,
            InfoExerciseUserContentRequest(infoExerciseUser.favorite,infoExerciseUser.note))
        if (response.status == 200 || response.status ==201) {
            return response.data
        }else{
            throw Exception("Erreur ${response.status}: ${response.message}  ")
        }
    }


    override suspend fun getInfoExerciseUser(): List<InfoExerciseUser> {
        return api.getAllinfoExerciseUser().data
    }
}