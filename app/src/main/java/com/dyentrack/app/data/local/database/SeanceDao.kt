package com.dyentrack.app.data.local.database

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.TypeConverters
import androidx.room.Update
import com.dyentrack.app.data.local.entities.SeanceOfDayLocal
import com.dyentrack.app.domain.model.TypeOfAction
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
        val updatedSeance = seance.copy(lastUpdateSeance = Instant.now().toString() , lastUpdateHealth =Instant.now().toString() )
        if (seanceDao.getSeanceByDay(seance.day) == null) {
            seanceDao.insertSeance(updatedSeance)
        } else {
            seanceDao.updateSeance(updatedSeance)
        }
    }

    suspend fun saveOrUpdateSeanceWithoutReplaceNull(seanceDao: SeanceDao, seance: SeanceOfDayLocal,  dateSave: String? = null,typeOfAction: TypeOfAction) {
        val existing = seanceDao.getSeanceByDay(seance.day)
        val saveDate = dateSave ?: Instant.now().toString()
        val epoch = Instant.EPOCH.toString()
        Log.d("Type de SaveLocal","${typeOfAction}")


        val isSeanceUpdate =
            typeOfAction == TypeOfAction.UPDATESEANCE ||
                    typeOfAction == TypeOfAction.FORCESEANCEUPDATE||
                    typeOfAction == TypeOfAction.UPDATEBOTH

        val isHealthUpdate = typeOfAction == TypeOfAction.UPDATEHEALTH|| typeOfAction == TypeOfAction.UPDATEBOTH


        val mergedSeance = existing?.copy(
            nutritionTrack = seance.nutritionTrack ?: existing.nutritionTrack,
            physioTrack = seance.physioTrack ?: existing.physioTrack,
            plannedExercises = if (typeOfAction == TypeOfAction.FORCESEANCEUPDATE) seance.plannedExercises else seance.plannedExercises.ifEmpty { existing.plannedExercises } ,
            lastUpdateSeance = if (isSeanceUpdate) saveDate else existing.lastUpdateSeance ,
            lastUpdateHealth = if (isHealthUpdate) saveDate else existing.lastUpdateHealth
        )?: seance.copy(
            lastUpdateSeance =  if (isSeanceUpdate) saveDate else epoch,
            lastUpdateHealth = if (isHealthUpdate) saveDate else epoch

        )

        if (existing == null) {
            seanceDao.insertSeance(mergedSeance)
        } else {
            seanceDao.updateSeance(mergedSeance)
        }
    }

}


