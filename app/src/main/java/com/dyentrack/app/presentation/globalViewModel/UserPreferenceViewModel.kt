package com.dyentrack.app.presentation.globalViewModel

import androidx.lifecycle.ViewModel
import com.dyentrack.app.domain.model.UserPreference
import com.dyentrack.app.domain.usecase.preference.GetUserPreferenceUseCase
import com.dyentrack.app.domain.usecase.preference.SaveUserPreferenceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class UserPreferenceViewModel @Inject constructor(
    private val getUserPreferenceUseCase: GetUserPreferenceUseCase,
    private val saveUserPreferenceUseCase: SaveUserPreferenceUseCase
) : ViewModel() {




    private val _userPreference = MutableStateFlow<UserPreference?>(null)
    val userPreference: StateFlow<UserPreference?> get() = _userPreference

    init {
        fetchUserPreference()
    }


    private fun fetchUserPreference() {
        _userPreference.value = getUserPreferenceUseCase()
    }

    fun updateUserPreference(newPref: UserPreference) {
        saveUserPreferenceUseCase(newPref)
        _userPreference.value = getUserPreferenceUseCase()
    }
}
