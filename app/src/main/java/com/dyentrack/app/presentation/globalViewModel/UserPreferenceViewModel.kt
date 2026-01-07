package com.dyentrack.app.presentation.globalViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dyentrack.app.domain.model.UserPreference
import com.dyentrack.app.domain.usecase.preference.FetchUserPreferenceUseCase
import com.dyentrack.app.domain.usecase.preference.SaveUserPreferenceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class UserPreferenceViewModel @Inject constructor(
    private val fetchUserPreferenceUseCase: FetchUserPreferenceUseCase,
    private val saveUserPreferenceUseCase: SaveUserPreferenceUseCase
) : ViewModel() {


    val userPreference: StateFlow<UserPreference?> = fetchUserPreferenceUseCase.userPreferenceFlow.stateIn(
                scope = viewModelScope,
                started = kotlinx.coroutines.flow.SharingStarted.Eagerly,
                initialValue = null
            )

    fun fetchUserPreference() {
        fetchUserPreferenceUseCase.invoke()
    }


    fun updateUserPreference(newPref: UserPreference) {
        saveUserPreferenceUseCase(newPref)
        fetchUserPreferenceUseCase()
    }
}
