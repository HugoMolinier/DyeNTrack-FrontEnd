package com.dyentrack.app.presentation.globalViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dyentrack.app.domain.model.preset.Preset
import com.dyentrack.app.domain.usecase.preset.DeletePresetUseCase
import com.dyentrack.app.domain.usecase.preset.GetPresetUseCase
import com.dyentrack.app.domain.usecase.preset.SavePresetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PresetViewModel @Inject constructor(
    getPresetUseCase: GetPresetUseCase,
    private val savePresetUseCase: SavePresetUseCase,
    private val deletePresetUseCase: DeletePresetUseCase
) : ViewModel() {
    //
    val presetFlow = getPresetUseCase.presetFlow.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _navigateToPreset = MutableSharedFlow<Preset?>()
    val navigateToPreset = _navigateToPreset.asSharedFlow()


    fun deletePreset(preset: Preset) = viewModelScope.launch { deletePresetUseCase(preset) }

    fun savePreset(preset: Preset) {
        viewModelScope.launch {
            savePresetUseCase(preset)
        }
    }
    fun createPreset(name: String) {
        viewModelScope.launch {
            val preset = savePresetUseCase.localOnly(
                Preset(name = name, idPreset = null)
            )
            _navigateToPreset.emit(preset)
        }
    }

}