package com.dyentrack.app.presentation.screens.parameters.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dyentrack.app.domain.model.AuthStatus
import com.dyentrack.app.domain.model.ResponseAuth
import com.dyentrack.app.domain.usecase.auth.GetUserUseCase
import com.dyentrack.app.domain.usecase.auth.IsValideTokenUseCase
import com.dyentrack.app.domain.usecase.auth.LoginUserUseCase
import com.dyentrack.app.domain.usecase.auth.LogoutUseCase
import com.dyentrack.app.domain.usecase.auth.RegisterUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUserUseCase: LoginUserUseCase,
    private val registerUserUseCase: RegisterUserUseCase,
    private val isValideTokenUseCase: IsValideTokenUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    val authResponse = MutableStateFlow<ResponseAuth?>(null)

    init {
        checkSession()
    }
    fun login(email: String, password: String) {
        viewModelScope.launch {
            authResponse.value = loginUserUseCase(email, password)
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            authResponse.value = registerUserUseCase(email, password)
        }
    }

    fun logout(){
        authResponse.value =logoutUseCase()
    }
    private fun checkSession() {
        viewModelScope.launch {
            val status = isValideTokenUseCase()

            if (status == AuthStatus.CONNECTED) {
                val user = getUserUseCase()
                authResponse.value = ResponseAuth(
                    userDTO = user,
                    token = null,
                    status = AuthStatus.CONNECTED,
                    null
                )
            } else {
                authResponse.value = ResponseAuth(
                    userDTO = null,
                    token = null,
                    status = AuthStatus.NOT_CONNECTED,
                    null
                )
            }
        }
    }
}


