package com.dyentrack.app.data.local.database

import androidx.room.*
import com.dyentrack.app.data.local.entities.SeanceOfDayLocal

@Dao
@TypeConverters(Converters::class)
interface SeanceDao {

    @Query("SELECT * FROM seanceOfDay")
    suspend fun getAllSeances(): List<SeanceOfDayLocal>

    @Query("SELECT * FROM seanceOfDay WHERE day = :day")
    suspend fun getSeanceByDay(day: String): SeanceOfDayLocal?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeance(seance: SeanceOfDayLocal)

    @Query("UPDATE seanceOfDay SET saveAPI = :status WHERE day = :day")
    suspend fun updateSaveApi(day: String, status: Boolean)


    @Update
    suspend fun updateSeance(seance: SeanceOfDayLocal)

    suspend fun saveOrUpdateSeance(seanceDao: SeanceDao, seance: SeanceOfDayLocal) {
        if (seanceDao.getSeanceByDay(seance.day) == null) {
            seanceDao.insertSeance(seance)
        } else {
            seanceDao.updateSeance(seance)
        }
    }

    suspend fun saveOrUpdateSeanceWithoutReplaceNull(seanceDao: SeanceDao, seance: SeanceOfDayLocal) {
        val existing = seanceDao.getSeanceByDay(seance.day)

        val mergedSeance = existing?.copy(
            nutritionTrack = seance.nutritionTrack ?: existing.nutritionTrack,
            physioTrack = seance.physioTrack ?: existing.physioTrack,
            plannedExercises = seance.plannedExercises.ifEmpty { existing.plannedExercises },
            saveAPI = seance.saveAPI
        ) ?: seance

        if (existing == null) {
            seanceDao.insertSeance(mergedSeance)
        } else {
            seanceDao.updateSeance(mergedSeance)
        }
    }


    @Delete
    suspend fun deleteSeance(seance: SeanceOfDayLocal)
}
