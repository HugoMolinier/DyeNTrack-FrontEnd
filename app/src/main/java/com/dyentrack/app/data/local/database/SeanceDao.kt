package com.dyentrack.app.data.local.database

import androidx.room.*
import com.dyentrack.app.data.local.entities.SeanceOfDayLocal
import kotlinx.coroutines.flow.Flow
import java.time.Instant

@Dao
@TypeConverters(Converters::class)
interface SeanceDao {

    @Query("SELECT * FROM seanceOfDay")
    suspend fun getAllSeances(): List<SeanceOfDayLocal>

    @Query("SELECT * FROM seanceOfDay WHERE day = :day")
    suspend fun getSeanceByDay(day: String): SeanceOfDayLocal?

    @Query("SELECT * FROM seanceOfDay")
    fun observeAllSeances(): Flow<List<SeanceOfDayLocal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeance(seance: SeanceOfDayLocal)



    @Update
    suspend fun updateSeance(seance: SeanceOfDayLocal)

    suspend fun saveOrUpdateSeance(seanceDao: SeanceDao, seance: SeanceOfDayLocal) {
        val updatedSeance = seance.copy(lastUpdate = Instant.now().toString())
        if (seanceDao.getSeanceByDay(seance.day) == null) {
            seanceDao.insertSeance(updatedSeance)
        } else {
            seanceDao.updateSeance(updatedSeance)
        }
    }

    suspend fun saveOrUpdateSeanceWithoutReplaceNull(seanceDao: SeanceDao, seance: SeanceOfDayLocal,  dateSave: String? = null) {
        val existing = seanceDao.getSeanceByDay(seance.day)
        val saveDate = dateSave ?: Instant.now().toString()

        val mergedSeance = existing?.copy(
            nutritionTrack = seance.nutritionTrack ?: existing.nutritionTrack,
            physioTrack = seance.physioTrack ?: existing.physioTrack,
            plannedExercises = seance.plannedExercises.ifEmpty { existing.plannedExercises },
            lastUpdate = saveDate
        ) ?: seance.copy(lastUpdate = saveDate)

        if (existing == null) {
            seanceDao.insertSeance(mergedSeance)
        } else {
            seanceDao.updateSeance(mergedSeance)
        }
    }


}
