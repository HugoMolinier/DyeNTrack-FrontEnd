package com.dyentrack.app.data.repository

import android.util.Log
import com.dyentrack.app.data.network.ApiInterface
import com.dyentrack.app.data.network.dto.InfoExerciseUserContentRequest
import com.dyentrack.app.domain.model.InfoExerciseUser
import com.dyentrack.app.domain.repository.InfoExerciseUserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InfoExerciseUserRepositoryImpl @Inject constructor(
    private val api: ApiInterface
) : InfoExerciseUserRepository {

    private val _infoExerciseUserFlow = MutableStateFlow<Map<Long, InfoExerciseUser>>(emptyMap())
    override val infoExerciseUserFlow: StateFlow<Map<Long, InfoExerciseUser>> =
        _infoExerciseUserFlow

    override suspend fun updateInfoExerciseUser(infoExerciseUser: InfoExerciseUser): Int {
        try {
            val response = api.updateInfoExerciseUser(
                infoExerciseUser.idExercise,
                InfoExerciseUserContentRequest(infoExerciseUser.favorite, infoExerciseUser.note)
            )
            if (response.status == 200 || response.status == 201) {
                updateMap(infoExerciseUser.idExercise, response.data)
            }
            return response.status

        } catch (_: Exception) {

            return 999
        }
    }

    override suspend fun fetchInfoExerciseUser() {
        return try {
            val response = api.getAllinfoExerciseUser()
            if (response.status == 200) {
                _infoExerciseUserFlow.value = response.data.associateBy { it.idExercise }
            } else {
                throw Exception("Erreur API ${response.status}: ${response.message}")
            }
        } catch (_: Exception) {

        }
    }


    private fun updateMap(id: Long, value: InfoExerciseUser) {
        _infoExerciseUserFlow.value =
            _infoExerciseUserFlow.value.toMutableMap().apply {
                this[id] = value
            }
    }
}