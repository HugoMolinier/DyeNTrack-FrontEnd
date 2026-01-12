package com.dyentrack.app.domain.usecase.preset

import com.dyentrack.app.domain.model.preset.Preset
import com.dyentrack.app.domain.repository.PresetRepository
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SavePresetUseCase @Inject constructor(private val repo: PresetRepository) {
    suspend operator fun invoke(preset: Preset): Preset? = repo.save(preset)

    suspend fun localOnly(preset: Preset): Preset? = repo.saveLocalOnly(preset)
}