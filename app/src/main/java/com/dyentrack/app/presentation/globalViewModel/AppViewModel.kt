package com.dyentrack.app.presentation.globalViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dyentrack.app.domain.usecase.masterdata.GetMasterDataStaticUseCase
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



    fun syncOnStart(isConnected:Boolean){

        viewModelScope.launch {

            if (isConnected)getDayDataUseCase()
            getMasterDataUseCase()
            if (isConnected)getInfoExerciseUser()


        }

    }

    fun syncUserData(){
        viewModelScope.launch {
            try {
                getDayDataUseCase()
                getInfoExerciseUser()
            } catch (e: Exception) {
                println("❌ Erreur : $e")
            }
        }
    }
}