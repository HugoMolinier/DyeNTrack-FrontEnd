package com.dyentrack.app.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dyentrack.app.domain.usecase.GetMasterDataStaticUseCase
import com.dyentrack.app.domain.usecase.daydata.GetDayDataUseCase
import com.dyentrack.app.domain.usecase.infoExerciseUser.GetInfoExerciseUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val getMasterDataUseCase: GetMasterDataStaticUseCase,
    private val getInfoExerciseUser: GetInfoExerciseUserUseCase,
    private val getDayDataUseCase: GetDayDataUseCase,
) : ViewModel() {

    init {
        viewModelScope.launch {
            try {
                getMasterDataUseCase()
                getDayDataUseCase()
                getInfoExerciseUser()

            } catch (e: Exception) {
                println("❌ Erreur : ${e.message}")
            }
        }
    }
}