package com.dyentrack.app.domain.model.dayData

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.dyentrack.app.data.local.database.Converters
import com.dyentrack.app.domain.model.SetSide
import com.dyentrack.app.domain.model.SetType
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "seanceOfDay")
@TypeConverters(Converters::class)
data class DayDataRequest(
    val idDayData:Long?,//idAPI
    @PrimaryKey val dayData: String,
    val seanceTrack: SeanceTrack?=null,
    val nutritionTrack: NutritionTrack?=null,
    val physioTrack: PhysioTrack?=null,
    val lastUpdate: String?=null,
    val lastUpdateHealthLocal: String ?=null  //local only seulement pour le merge API / local
)
