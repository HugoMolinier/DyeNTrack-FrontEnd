package com.dyentrack.app.presentation.globalViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dyentrack.app.domain.usecase.masterdata.GetMasterDataStaticUseCase
import com.dyentrack.app.domain.usecase.daydata.GetDayDataUseCase
import com.dyentrack.app.domain.usecase.infoExerciseUser.GetInfoExerciseUserUseCase
import com.dyentrack.app.domain.usecase.masterdata.BuildMasterDataMapsUseCase
import com.dyentrack.app.domain.usecase.preset.GetPresetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val getMasterDataUseCase: GetMasterDataStaticUseCase,
    private val getInfoExerciseUser: GetInfoExerciseUserUseCase,
    private val getDayDataUseCase: GetDayDataUseCase,
private val getPresetUseCase: GetPresetUseCase
    ) : ViewModel() {



    fun syncOnStart(isConnected:Boolean){

        viewModelScope.launch((Dispatchers.IO)) {

            if (isConnected)getDayDataUseCase()
            getMasterDataUseCase()
            if (isConnected)getInfoExerciseUser()
            if (isConnected)getPresetUseCase()

        }

    }

    fun syncUserData(){
        viewModelScope.launch((Dispatchers.IO)) {
            try {
                getDayDataUseCase()
                getInfoExerciseUser()
                getPresetUseCase()
            } catch (e: Exception) {
                println("❌ Erreur : $e")
            }
        }
    }
}