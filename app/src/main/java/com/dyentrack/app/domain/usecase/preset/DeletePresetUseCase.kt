package com.dyentrack.app.domain.usecase.preset

import com.dyentrack.app.domain.model.preset.Preset
import com.dyentrack.app.domain.repository.PresetRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeletePresetUseCase @Inject constructor(
    private val repository: PresetRepository
) {
    suspend operator fun invoke(preset: Preset) { repository.delete(preset) }
}