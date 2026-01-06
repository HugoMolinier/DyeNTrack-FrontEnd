package com.dyentrack.app.data.local.database

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.TypeConverters
import androidx.room.Update
import com.dyentrack.app.domain.model.dayData.DayDataRequest
import com.dyentrack.app.domain.model.TypeOfAction
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.time.temporal.ChronoUnit

//données métier persistantes
@Dao
@TypeConverters(Converters::class)
interface SeanceDao {

    @Query("SELECT * FROM seanceOfDay")
    suspend fun getAllSeances(): List<DayDataRequest>

    @Query("SELECT * FROM seanceOfDay WHERE dayData = :day")
    suspend fun getSeanceByDay(day: String): DayDataRequest?

    @Query("SELECT * FROM seanceOfDay")
    fun observeAllSeances(): Flow<List<DayDataRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeance(seance: DayDataRequest)



    @Update
    suspend fun updateSeance(seance: DayDataRequest)

    suspend fun saveOrUpdateSeance(seanceDao: SeanceDao, seance: DayDataRequest) {
        val updatedSeance = seance.copy(lastUpdate = Instant.now().toString() , lastUpdateHealthLocal =Instant.now().toString() )
        if (seanceDao.getSeanceByDay(seance.dayData) == null) {
            seanceDao.insertSeance(updatedSeance)
        } else {
            seanceDao.updateSeance(updatedSeance)
        }
    }

    suspend fun saveOrUpdateSeanceWithoutReplaceNull( seance: DayDataRequest,  dateSave: String? = null,typeOfAction: TypeOfAction) {
        val existing = getSeanceByDay(seance.dayData)
        val saveDate = dateSave ?: Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()
        val epoch = Instant.EPOCH.toString()
        Log.d("Type de SaveLocal","${typeOfAction}")
        Log.d("DateSave", saveDate)

        val isSeanceUpdate =
            typeOfAction == TypeOfAction.UPDATESEANCE ||
                    typeOfAction == TypeOfAction.FORCESEANCEUPDATE||
                    typeOfAction == TypeOfAction.UPDATEBOTH

        val isHealthUpdate = typeOfAction == TypeOfAction.UPDATEHEALTH|| typeOfAction == TypeOfAction.UPDATEBOTH


        val mergedSeance = existing?.copy(
            nutritionTrack = seance.nutritionTrack ?: existing.nutritionTrack,
            physioTrack = seance.physioTrack ?: existing.physioTrack,
            seanceTrack = when (typeOfAction) {
                TypeOfAction.FORCESEANCEUPDATE -> seance.seanceTrack
                else -> seance.seanceTrack ?: existing.seanceTrack
            },
            lastUpdate = if (isSeanceUpdate) saveDate else existing.lastUpdate ,
            lastUpdateHealthLocal = if (isHealthUpdate) saveDate else existing.lastUpdateHealthLocal
        )?: seance.copy(
            lastUpdate = if (isSeanceUpdate) saveDate else epoch,
            lastUpdateHealthLocal = if (isHealthUpdate) saveDate else epoch

        )
        Log.d("mergeSeance","${mergedSeance}")

        if (existing == null) {
            insertSeance(mergedSeance)
        } else {
            updateSeance(mergedSeance)
        }
    }



}


