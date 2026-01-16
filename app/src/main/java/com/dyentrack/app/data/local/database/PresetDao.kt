package com.dyentrack.app.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.TypeConverters
import androidx.room.Update
import com.dyentrack.app.domain.model.preset.Preset
import kotlinx.coroutines.flow.Flow
import java.time.Instant

@Dao
@TypeConverters(Converters::class)
interface PresetDao {


    @Query("SELECT * FROM preset WHERE isDelete = 0")
    fun getAllActivePresetsFlow(): Flow<List<Preset>>


    @Query("SELECT * FROM preset WHERE isDelete = 1")
    suspend fun getDeletedPresets(): List<Preset>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreset(preset: Preset): Long

    @Update
    suspend fun updatePreset(preset: Preset): Int


    @Query("DELETE FROM preset WHERE idPreset = :idApi")
    suspend fun hardDeleteByApiId(idApi: Long)

    @Query("DELETE FROM preset WHERE idLocal = :idLocal")
    suspend fun hardDeleteByLocalId(idLocal: Long)


    @Query("SELECT * FROM preset WHERE idLocal = :idLocal")
    suspend fun getPresetById(idLocal: Long?): Preset?

    @Query("SELECT * FROM preset WHERE idPreset = :idApi")
    suspend fun getPresetByIdApi(idApi: Long): Preset?
    suspend fun saveOrUpdatePreset(preset: Preset, dateSave: String? = null): Preset? {


        val saveDate = dateSave ?: Instant.now().toString()
        val existing = when {
            preset.idLocal != 0L -> getPresetById(preset.idLocal)
            preset.idPreset != null -> getPresetByIdApi(preset.idPreset)
            else -> null
        }

        return if (existing == null) {
            // Nouveau preset → insert
            val newId = insertPreset(preset.copy(lastUpdate = saveDate))
            getPresetById(newId)
        } else {
            // Preset existant → merge et update
            val merged = existing.copy(
                name = preset.name.ifEmpty { existing.name },
                presetSeanceExerciseVO = preset.presetSeanceExerciseVO?.ifEmpty { existing.presetSeanceExerciseVO },
                idPreset = preset.idPreset ?: existing.idPreset,
                lastUpdate = preset.lastUpdate
            )
            updatePreset(merged)
            merged
        }
    }

}

