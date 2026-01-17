package com.dyentrack.app.domain.model.dayData

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.dyentrack.app.data.local.database.Converters
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
){
    @Ignore
    private var _localDate: LocalDate? = null

    val localDate: LocalDate
        get() {
            if (_localDate == null) {
                _localDate = LocalDate.parse(dayData, DateTimeFormatter.ISO_DATE)
            }
            return _localDate!!
        }
}
