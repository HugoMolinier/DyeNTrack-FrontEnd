package com.dyentrack.app.domain.repository

import com.dyentrack.app.data.local.entities.SeanceOfDayLocal

interface LocalSeanceRepository {
    suspend fun getAllSeances(): List<SeanceOfDayLocal>


    suspend fun getSeanceByDay(day: String): SeanceOfDayLocal?

    suspend fun saveOrUpdateSeance(seance: SeanceOfDayLocal)

    suspend fun insertSeance(seance: SeanceOfDayLocal)


    suspend fun updateSeance(seance: SeanceOfDayLocal)


    suspend fun deleteSeance(seance: SeanceOfDayLocal)

}