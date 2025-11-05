package com.dyentrack.app.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dyentrack.app.domain.model.InfoExerciseUser
import com.dyentrack.app.domain.model.MasterDataStatic
import com.dyentrack.app.domain.usecase.GetMasterDataStaticUseCase
import com.dyentrack.app.domain.usecase.infoExerciseUser.GetAllInfoExerciseUserUseCase
import com.dyentrack.app.domain.usecase.infoExerciseUser.SaveInfoExerciseUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val getMasterDataUseCase: GetMasterDataStaticUseCase,
    private val getInfoExerciseUser: GetAllInfoExerciseUserUseCase,
    private val saveInfoExerciseUserUseCase: SaveInfoExerciseUserUseCase
) : ViewModel() {

    var masterData: MasterDataStatic? = null

    //dans le futur il y aura aussi ses exercises crée et preset
    private val _infoAllUser = MutableStateFlow<Map<Long, InfoExerciseUser>>(emptyMap())
    val infoAllUser: StateFlow<Map<Long, InfoExerciseUser>> = _infoAllUser

    init {
        viewModelScope.launch {
            try {
                masterData = getMasterDataUseCase()
                Log.d("AppViewModel", "✅ $masterData")
                if (_infoAllUser.value.isEmpty()) {
                    _infoAllUser.value = mapperOfInfoExerciseUser(getInfoExerciseUser())
                    Log.d("AppViewModel", "✅ Données utilisateur chargées depuis l'API")
                } else {
                    Log.d("AppViewModel", "ℹ️ Données déjà en mémoire, pas d'appel API")
                }
            } catch (e: Exception) {
                println("❌ Erreur : ${e.message}")
            }
        }
    }

    fun updateInfoExerciseUser(
        idExercise: Long,
        favorite: Boolean? = null,
        note: String? = "null"
    ) {
        viewModelScope.launch {
            val current = _infoAllUser.value[idExercise] ?: InfoExerciseUser(
                favorite ?: false,
                note ?: "",
                idExercise
            )

            try {
                val currentMap = _infoAllUser.value.toMutableMap()
                val response = saveInfoExerciseUserUseCase(
                    InfoExerciseUser(
                        favorite = favorite ?: current.favorite,
                        note = note ?: current.note,
                        idExercise = idExercise
                    )
                )
                Log.d("ici la response", "${response}response")
                currentMap[idExercise] = saveInfoExerciseUserUseCase(
                    InfoExerciseUser(
                        favorite ?: current.favorite,
                        note ?: current.note,
                        idExercise
                    )
                )
                _infoAllUser.value = currentMap
            } catch (e: Exception) {
                Log.d("AppViewModel", "❌ Erreur updateInfoExerciseUser : $e")
            }
        }


    }

    private fun mapperOfInfoExerciseUser(
        list: List<InfoExerciseUser>
    ): Map<Long, InfoExerciseUser> {
        return list.associateBy { it.idExercise }
    }

}
