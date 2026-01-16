package com.dyentrack.app.presentation.globalViewModel

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import androidx.health.connect.client.HealthConnectClient
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dyentrack.app.domain.model.HealthConnectStatus
import com.dyentrack.app.domain.model.UserPreference
import com.dyentrack.app.domain.usecase.health.CheckHealthConnectUseCase
import com.dyentrack.app.domain.usecase.health.RequestHealthConnectPermissionsUseCase
import com.dyentrack.app.domain.usecase.health.SyncHealthDataUseCase
import com.dyentrack.app.domain.usecase.preference.FetchUserPreferenceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

enum class HealthConnectEtat {
    VERIFICATION, INSTALLER, AUTORISER, OPERATIONNEL, METTRE_A_JOUR, NON_DISPONIBLE
}

@HiltViewModel
class HealthConnectViewModel @Inject constructor(
    private val healthConnectUseCase: CheckHealthConnectUseCase,
    private val requestHealthConnectPermissionsUseCase: RequestHealthConnectPermissionsUseCase,
    private val syncHealthDataUseCase: SyncHealthDataUseCase,
    fetchUserPreferenceUseCase: FetchUserPreferenceUseCase
) : ViewModel() {

    private val _etat = MutableStateFlow(HealthConnectEtat.VERIFICATION)
    val etat: StateFlow<HealthConnectEtat> = _etat

    //seulement au démarage pour le sync donc 0 collect
    val userPreference: StateFlow<UserPreference?> =
        fetchUserPreferenceUseCase.userPreferenceFlow.stateIn(
            scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = null
        )


    private val syncGranded: Boolean = userPreference.value?.autoSync ?: true


    private val _autorisationNeed = MutableStateFlow<Set<String>?>(null)
    val autorisationNeed: StateFlow<Set<String>?> = _autorisationNeed
    private var client: HealthConnectClient? = null

    init {
        viewModelScope.launch {
            when (val status = healthConnectUseCase()) {
                is HealthConnectStatus.NotAvailable -> _etat.value = HealthConnectEtat.INSTALLER
                is HealthConnectStatus.Available -> {
                    client = status.client
                    val granted =
                        requestHealthConnectPermissionsUseCase.arePermissionsGranted(status.client)
                    if (granted) {
                        _etat.value = HealthConnectEtat.OPERATIONNEL
                        if (syncGranded) {
                            syncHealthData()
                            syncHealthData(LocalDate.now().minusDays(1L))
                            syncHealthData(LocalDate.now().minusDays(2L))


                        }
                    } else {
                        _etat.value = HealthConnectEtat.AUTORISER
                    }
                }

                is HealthConnectStatus.NeedsUpdate -> _etat.value = HealthConnectEtat.METTRE_A_JOUR
                else -> _etat.value = HealthConnectEtat.NON_DISPONIBLE
            }
        }
    }

    fun onButtonClick(context: Context) {
        when (_etat.value) {
            HealthConnectEtat.INSTALLER, HealthConnectEtat.METTRE_A_JOUR -> openPlayStore(context)
            HealthConnectEtat.AUTORISER -> requestPermissions()
            HealthConnectEtat.OPERATIONNEL, HealthConnectEtat.VERIFICATION, HealthConnectEtat.NON_DISPONIBLE -> {} // rien à faire
        }
    }

    private fun openPlayStore(context: Context) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            "https://play.google.com/store/apps/details?id=com.google.android.apps.healthdata".toUri()
        )
        context.startActivity(intent)
    }

    private fun requestPermissions() {
        client?.let {
            viewModelScope.launch {
                _autorisationNeed.value =
                    requestHealthConnectPermissionsUseCase.getRequiredPermissions()
            }
        }
    }


    fun syncHealthData(daySelected: LocalDate = LocalDate.now()) {
        client?.let { client ->
            viewModelScope.launch {
                val permissionsGranted =
                    requestHealthConnectPermissionsUseCase.arePermissionsGranted(client)
                if (permissionsGranted) {
                    syncHealthDataUseCase(client, daySelected)
                }
            }
        }
    }

    fun onPermissionsResult(allGranted: Boolean) {
        if (allGranted) {
            _etat.value = HealthConnectEtat.OPERATIONNEL
            if (syncGranded) syncHealthData()
        } else {
            _etat.value = HealthConnectEtat.AUTORISER
        }
        _autorisationNeed.value = null
    }
}

