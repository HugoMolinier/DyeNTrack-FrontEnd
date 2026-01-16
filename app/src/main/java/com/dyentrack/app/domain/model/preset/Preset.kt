package com.dyentrack.app.domain.model.preset

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.dyentrack.app.data.local.database.Converters
import java.time.Instant

@Entity(tableName = "preset",
        indices = [Index(value = ["idPreset"], unique = true)]
)
@TypeConverters(Converters::class)
data class Preset(
    @PrimaryKey(autoGenerate = true)
    val idLocal:Long?=null,//local only
    val idPreset: Long?=null,
    val name :String,
    val lastUpdate: String= Instant.now().toString(),
    val presetSeanceExerciseVO: List<ExerciceInPreset>?=emptyList(),
    val isDelete: Boolean = false,//Local only

)
