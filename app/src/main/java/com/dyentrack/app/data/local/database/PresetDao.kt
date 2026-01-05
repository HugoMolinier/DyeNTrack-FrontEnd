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
    suspend fun insertPreset(preset: Preset):Long

    @Update
    suspend fun updatePreset(preset: Preset):Int


    @Query("DELETE FROM preset WHERE idPreset = :idApi")
    suspend fun hardDeleteByApiId(idApi: Long)

    @Query("DELETE FROM preset WHERE idLocal = :idLocal")
    suspend fun hardDeleteByLocalId(idLocal: Long)


    @Query("SELECT * FROM preset WHERE idLocal = :idLocal")
    suspend fun getPresetById(idLocal: Long): Preset?
    suspend fun saveOrUpdatePreset(preset: Preset,dateSave: String? = null):Preset? {

        val saveDate = dateSave ?: Instant.now().toString()
        if (preset.idLocal == 0L) {
            // Nouveau preset → insert
            return getPresetById(insertPreset(preset.copy(lastUpdate=saveDate)))
        } else {
            // Preset existant → merge et update
            val existing = getPresetById(  preset.idLocal )

            val merged = existing?.copy(
                name = preset.name.ifEmpty { existing.name },
                presetSeanceExerciseVO = preset.presetSeanceExerciseVO?.ifEmpty { existing.presetSeanceExerciseVO },
                idPreset = preset.idPreset ?: existing.idPreset,
                lastUpdate = preset.lastUpdate

            ) ?: preset.copy(lastUpdate = saveDate)
            updatePreset(merged)
            return merged
        }
    }

}

