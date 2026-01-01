package com.dyentrack.app.data.repository

import android.util.Log
import com.dyentrack.app.data.local.database.SeanceDao
import com.dyentrack.app.data.local.entities.SeanceOfDayLocal
import com.dyentrack.app.data.network.ApiInterface
import com.dyentrack.app.domain.model.AuthStatus
import com.dyentrack.app.domain.model.DayDataRequest
import com.dyentrack.app.domain.model.NutritionTrack
import com.dyentrack.app.domain.model.PhysioTrack
import com.dyentrack.app.domain.model.SeanceTrack
import com.dyentrack.app.domain.model.TypeOfAction
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
    private val userConnectionRepositoryImpl: UserConnectionRepositoryImpl
) : DayDataRepository {
    private val _dayDataFlow = MutableStateFlow<List<DayDataRequest>?>(null)
    override val dayDataFlow: StateFlow<List<DayDataRequest>?> = _dayDataFlow

    override suspend fun fetchDayData() {
        // 1. récupère le local

        val localDayDataBrut = seanceDao.getAllSeances()
        val localDayData = localDayDataBrut.map { it.toDayDataRequest() }
        //1.5 update le flow car api peut delay
        _dayDataFlow.value = localDayData


        // 2. récupère l'API
        if(userConnectionRepositoryImpl.isValideToken()==AuthStatus.CONNECTED) {
            val apiDayData = try {
                val apiResponse = api.getAllDayDataByDay()
                if (apiResponse.status == 200) apiResponse.data else emptyList()
            } catch (_: Exception) {
                emptyList()
            }
            // 4. merge local et API
            val merged = mergeLocalAndApiByTimestamp(localDayData, apiDayData)
            _dayDataFlow.value = merged
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
            if (apiDay != null) {
                val localTimeSeance = Instant.parse(localDay.lastUpdate)
                val localTimeHealth = Instant.parse(localDay.lastUpdateHealthLocal)
                val apiTime =  Instant.parse(apiDay.lastUpdate)

                val updateSeance = apiTime >= localTimeSeance // api + recent que seance
                val updateHealth = apiTime >= localTimeHealth // api + recent que health

                if (updateHealth || updateSeance ) { //api + recent que seance ou api + recent que health
                    result.add(apiDay)
                    if (apiTime !=localTimeSeance || apiTime !=localTimeHealth ) { //si egal alors donnée importer de l'api et sync en date
                        updateToLocal(
                            apiDay.copy(
                                nutritionTrack = if (updateHealth) apiDay.nutritionTrack else null,
                                physioTrack = if (updateHealth) apiDay.physioTrack else null,
                                seanceTrack = if (updateSeance) apiDay.seanceTrack else null
                            ), apiDay.lastUpdate,
                            TypeOfAction.UPDATEBOTH
                        )
                    }
                }


                else {// sinon garder local et mettre a jour api
                    result.add(localDay)
                     updateToApi(localDay)
                }

            }else {// API n'a pas ce jour → on envoie le local
                result.add(localDay)
                updateToApi(localDay)
            }
        }
        //ajouter au local les jour api pas dans le local
        val localDays = local.map { it.dayData }.toSet()
        api.filter { it.dayData !in localDays }.forEach { apiOnlyDay ->
            result.add(apiOnlyDay)
            updateToLocal(apiOnlyDay,apiOnlyDay.lastUpdate, TypeOfAction.UPDATEBOTH)
        }
        return result
    }

    override suspend fun getDayLocal(day:String): DayDataRequest? {
        return seanceDao
            .getSeanceByDay(day)
            ?.toDayDataRequest()
    }

    override suspend fun updateToApi(dayData: DayDataRequest): Int {
        return try {
            val response = api.saveDayData(dayData)
            Log.d("ResponseApi","${response}")
            if (response.status in listOf(200, 201)) {
                200
            } else {
                Log.d("Api response Error","$response")
                100
            }
        } catch (e: Exception) {
            Log.d("Api response Error","$e")
            100
        }
    }


    override suspend fun updateToLocal(dayData: DayDataRequest, dateSave: String?,typeOfAction: TypeOfAction ) {
        seanceDao.getSeanceByDay(dayData.dayData)
        val seanceLocal = dayData.toSeanceOfDayLocal()
        seanceDao.saveOrUpdateSeanceWithoutReplaceNull(seanceDao, seanceLocal,dateSave,typeOfAction)
        val updatedLocal = seanceDao.getAllSeances().map { it.toDayDataRequest() }
        _dayDataFlow.value = updatedLocal.map { it.copy() }
    }


    fun DayDataRequest.toSeanceOfDayLocal(): SeanceOfDayLocal {
        return SeanceOfDayLocal(
            day = dayData,
            nutritionTrack = nutritionTrack ,
            physioTrack = physioTrack ,
            plannedExercises = seanceTrack?.plannedExercises?:emptyList()

        )
    }

     fun SeanceOfDayLocal.toDayDataRequest(): DayDataRequest {
        return DayDataRequest(
            null,
            day,

            seanceTrack = SeanceTrack(
                startHour = null,
                presetSeanceId = null,
                plannedExercises = plannedExercises
            ),
            nutritionTrack?: NutritionTrack(),
            physioTrack?: PhysioTrack(),
            lastUpdate= lastUpdateSeance,
            lastUpdateHealth
        )
    }
}