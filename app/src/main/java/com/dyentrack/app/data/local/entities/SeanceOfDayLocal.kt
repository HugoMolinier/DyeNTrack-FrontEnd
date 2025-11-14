package com.dyentrack.app.data.local.entities



import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.dyentrack.app.data.local.database.Converters
import com.dyentrack.app.domain.model.NutritionTrack
import com.dyentrack.app.domain.model.PhysioTrack
import com.dyentrack.app.domain.model.PlannedExerciseVO


@Entity(tableName = "seanceOfDay")
@TypeConverters(Converters::class)
data class SeanceOfDayLocal(
    @PrimaryKey()
    val day: String,
    val plannedExercises: List<PlannedExerciseVO> = emptyList(),
    //val note: String? = null,
    val nutritionTrack: NutritionTrack?,
    val physioTrack: PhysioTrack?,
    val saveAPI: Boolean = false
)


