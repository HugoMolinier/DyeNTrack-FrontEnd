package com.dyentrack.app.presentation.globalViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dyentrack.app.domain.model.InfoExerciseUser
import com.dyentrack.app.domain.usecase.infoExerciseUser.GetInfoExerciseUserUseCase
import com.dyentrack.app.domain.usecase.infoExerciseUser.UpdateInfoExerciseUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InfoUserViewModel @Inject constructor(
    getInfoExerciseUserUseCase: GetInfoExerciseUserUseCase,
    private val updateInfoExerciseUserUseCase: UpdateInfoExerciseUserUseCase,
)  : ViewModel(){

    //Flow à la vue
    val infoExerciseUserFlow =getInfoExerciseUserUseCase.infoExerciseUserFlow

    fun updateInfoExerciseUser(
        idExercise: Long,
        favorite: Boolean?,
        note: String?
    ) {
        viewModelScope.launch {
            val info = InfoExerciseUser(idExercise = idExercise, favorite = favorite, note = note)
            updateInfoExerciseUserUseCase(info)
        }
    }
}