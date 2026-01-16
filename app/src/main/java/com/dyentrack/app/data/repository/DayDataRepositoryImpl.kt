package com.dyentrack.app.data.repository

import android.util.Log
import com.dyentrack.app.data.local.database.AppDatabase
import com.dyentrack.app.data.local.database.SeanceDao
import com.dyentrack.app.data.network.ApiInterface
import com.dyentrack.app.domain.model.AuthStatus
import com.dyentrack.app.domain.model.enums.TypeOfAction
import com.dyentrack.app.domain.model.dayData.DayDataRequest
import com.dyentrack.app.domain.model.enums.SaveType
import com.dyentrack.app.domain.repository.DayDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DayDataRepositoryImpl @Inject constructor(
    private val api: ApiInterface,
    private val seanceDao: SeanceDao,
    private val db: AppDatabase,
    private val userConnectionRepositoryImpl: UserConnectionRepositoryImpl
) : DayDataRepository {
    private val _dayDataFlow = MutableStateFlow<List<DayDataRequest>?>(null)
    override val dayDataFlow: StateFlow<List<DayDataRequest>?> = _dayDataFlow

    private val _apiFetchStatus = MutableStateFlow(SaveType.NOTSAVE)
    override val apiFetchStatus: StateFlow<SaveType> = _apiFetchStatus

    override suspend fun fetchDayData() {
        // 1. récupère le local

        val localDayData = seanceDao.getAllSeances()
        //1.5 update le flow car api peut delay
        _dayDataFlow.value = localDayData

        _apiFetchStatus.value = SaveType.LocalSAVE
        // 2. récupère l'API
        if (userConnectionRepositoryImpl.isValideToken() == AuthStatus.CONNECTED) {
            try {
                val apiResponse = api.getAllDayDataByDay()
                if (apiResponse.status == 200) {
                    _apiFetchStatus.value = SaveType.APISAVE
                    apiResponse.data
                    //Merge
                    val merged = mergeLocalAndApiByTimestamp(localDayData, apiResponse.data)
                    _dayDataFlow.value = merged

                }

            } catch (e: Exception) {
                Log.e("DayDataRepository", "Erreur fetchDayData: ${e.message}")
                return
            }

        }
    }


    private suspend fun mergeLocalAndApiByTimestamp( // a ajouter verif id telephone pour eviter le cas que sur un autre tel le healthsync date change la date avec seance vide donc api detecte que local>api donc importe seance vide
        local: List<DayDataRequest>,
        api: List<DayDataRequest>
    ): List<DayDataRequest> {
        val result = mutableListOf<DayDataRequest>()
        val apiMap = api.associateBy { it.dayData }

        //verification du plus récent
        local.forEach { localDay ->

            val apiDay = apiMap[localDay.dayData]
            if (apiDay == null) {
                // API n’a pas le jour → push local
                result.add(localDay)
                updateToApi(localDay)
                return@forEach
            }

            val apiTime = Instant.parse(apiDay.lastUpdate)
            val localSeanceTime = Instant.parse(localDay.lastUpdate)
            val localHealthTime = Instant.parse(localDay.lastUpdateHealthLocal)

            val seanceFromLocal = localSeanceTime > apiTime
            val healthFromLocal = localHealthTime > apiTime
            val merged = apiDay.copy(
                seanceTrack = if (seanceFromLocal) localDay.seanceTrack else apiDay.seanceTrack,
                nutritionTrack = if (healthFromLocal) localDay.nutritionTrack else apiDay.nutritionTrack,
                physioTrack = if (healthFromLocal) localDay.physioTrack else apiDay.physioTrack
            )
            result.add(merged)

            // ---- SYNC API ---- si le local a un element plus récent , n'envoie que l'élément plus récent
            if (seanceFromLocal || healthFromLocal) {//null = ingnoré
                updateToApi(
                    apiDay.copy(
                        seanceTrack = if (seanceFromLocal) localDay.seanceTrack else null,
                        nutritionTrack = if (healthFromLocal) localDay.nutritionTrack else null,
                        physioTrack = if (healthFromLocal) localDay.physioTrack else null
                    )
                )
            }
            // ---- SYNC LOCAL ---- si l'api a un élémênt plus récent
            if (localHealthTime < apiTime || localSeanceTime < apiTime) {
                updateToLocal(
                    merged.copy(
                        seanceTrack = merged.seanceTrack?.copy(presetLocalId = localDay.seanceTrack?.presetLocalId)
                    ),
                    apiDay.lastUpdate,
                    TypeOfAction.UPDATEBOTH
                )

            }


        }
        //ajouter au local les jour api pas dans le local
        val localDays = local.map { it.dayData }.toSet()
        api.filter { it.dayData !in localDays }.forEach {
            result.add(it)
            updateToLocal(it, it.lastUpdate, TypeOfAction.UPDATEBOTH)
        }
        return result
    }

    override suspend fun getDayLocal(day: String): DayDataRequest? {
        return seanceDao.getSeanceByDay(day)
    }

    override suspend fun updateToApi(dayData: DayDataRequest): Int {
        return try {
            val response = api.saveDayData(dayData)
            if (response.status in listOf(200, 201)) {
                200
            } else {
                Log.d("Api response Error", "$response")
                100
            }
        } catch (e: Exception) {
            Log.d("Api response Error", "$e")
            100
        }
    }


    override suspend fun updateToLocal(
        dayData: DayDataRequest,
        dateSave: String?,
        typeOfAction: TypeOfAction
    ) {
        seanceDao.saveOrUpdateSeanceWithoutReplaceNull(dayData, dateSave, typeOfAction)
        seanceDao.getSeanceByDay(dayData.dayData)?.let { day ->
            _dayDataFlow.value =
                (_dayDataFlow.value.orEmpty().filter { it.dayData != day.dayData } + day.copy())
        }
    }

    override suspend fun cleanDB() {
        db.clearAllTables()
        _dayDataFlow.value = emptyList()
    }


}