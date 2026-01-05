package com.dyentrack.app.domain.repository

import com.dyentrack.app.domain.model.preset.Preset
import kotlinx.coroutines.flow.Flow

interface PresetRepository {
    val presetFlow: Flow<List<Preset>>
    suspend fun save(preset: Preset): Preset?

    suspend fun saveLocalOnly(preset: Preset): Preset?

    suspend fun syncBetweenLocalApi()
    suspend fun delete(preset: Preset)
}