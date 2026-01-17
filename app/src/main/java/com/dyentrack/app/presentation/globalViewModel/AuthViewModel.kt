package com.dyentrack.app.presentation.globalViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dyentrack.app.domain.model.AuthState
import com.dyentrack.app.domain.model.RequestState
import com.dyentrack.app.domain.repository.UserConnectionRepository
import com.dyentrack.app.domain.usecase.auth.DeleteUserUseCase
import com.dyentrack.app.domain.usecase.auth.LoginUserUseCase
import com.dyentrack.app.domain.usecase.auth.LogoutUseCase
import com.dyentrack.app.domain.usecase.auth.RegisterUserUseCase
import com.dyentrack.app.domain.usecase.auth.UpdateUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUserUseCase: LoginUserUseCase,
    private val registerUserUseCase: RegisterUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    repository: UserConnectionRepository

) : ViewModel() {

    val authState: StateFlow<AuthState> = repository.authState

    // flow loading / error / success
    private val _requestState = MutableStateFlow<RequestState>(RequestState.Idle)
    val requestState: StateFlow<RequestState> = _requestState


    init {
        clearRequestState()
    }

    fun clearRequestState() {
        _requestState.value = RequestState.Idle
    }

    fun login(email: String, password: String) = viewModelScope.launch {
        _requestState.value = RequestState.Loading
        _requestState.value = loginUserUseCase(email, password)
    }

    fun register(email: String, password: String, pseudo: String) = viewModelScope.launch {
        _requestState.value = RequestState.Loading
        _requestState.value = registerUserUseCase(email, password, pseudo)
    }

    fun updateUser(pseudo: String) =
        viewModelScope.launch {
            _requestState.value = RequestState.Loading
            _requestState.value = updateUserUseCase(pseudo, null, null, null)
        }

    fun deleteAll() {
        viewModelScope.launch {
            _requestState.value = RequestState.Loading

            _requestState.value = deleteUserUseCase()


        }
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            logoutUseCase()
        }
    }
}