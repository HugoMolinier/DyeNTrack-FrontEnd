package com.dyentrack.app.domain.usecase.preset

import com.dyentrack.app.domain.model.preset.Preset
import com.dyentrack.app.domain.repository.PresetRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetPresetUseCase @Inject constructor(
    private val repository: PresetRepository
) {
    val presetFlow: Flow<List<Preset>> = repository.presetFlow

    suspend operator fun invoke() {
        repository.syncBetweenLocalApi()
    }
}