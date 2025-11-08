package com.dyentrack.app.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dyentrack.app.domain.model.DayDataRequest
import com.dyentrack.app.domain.model.MasterDataStatic
import com.dyentrack.app.domain.usecase.GetMasterDataStaticUseCase
import com.dyentrack.app.domain.usecase.daydata.GetDayDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getDayDataUseCase: GetDayDataUseCase,
    getMasterDataStaticUseCase: GetMasterDataStaticUseCase,
) : ViewModel() {

    val dayDataFlow: StateFlow<List<DayDataRequest>> = getDayDataUseCase.dayDataFlow
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState
    var masterData: MasterDataStatic? = null

    init {
        viewModelScope.launch {
            masterData = getMasterDataStaticUseCase()
            dayDataFlow.collect { list ->
                val newMap = list.takeLast(15).associate { dayData ->
                    dayData.dayData to dayData.seanceTrack
                }
                _uiState.update { current -> current.copy(mapSeance = newMap)  }
            }
        }
    }





}