package com.dyentrack.app.data.repository

import com.dyentrack.app.data.local.database.SeanceDao
import com.dyentrack.app.data.local.entities.SeanceOfDayLocal
import com.dyentrack.app.domain.repository.LocalSeanceRepository
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class LocalSeanceRepositoryImpl @Inject constructor(
    private val seanceDao: SeanceDao
): LocalSeanceRepository {



    override suspend fun getAllSeances(): List<SeanceOfDayLocal> =seanceDao.getAllSeances()


    override suspend fun getSeanceByDay(day: String): SeanceOfDayLocal? =seanceDao.getSeanceByDay(day)


    override suspend fun insertSeance(seance: SeanceOfDayLocal) = seanceDao.insertSeance(seance)
    override suspend fun saveOrUpdateSeance(seance: SeanceOfDayLocal) = seanceDao.saveOrUpdateSeance(seanceDao,seance)

    override suspend fun updateSeance(seance: SeanceOfDayLocal) = seanceDao.updateSeance(seance)


    override suspend fun deleteSeance(seance: SeanceOfDayLocal) = seanceDao.deleteSeance(seance)
}
