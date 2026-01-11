package com.dyentrack.app.data.repository

import com.dyentrack.app.data.local.datasource.LocalDataSource
import com.dyentrack.app.data.network.ApiInterface
import com.dyentrack.app.domain.model.MasterDataStatic
import com.dyentrack.app.domain.repository.MasterDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.Duration
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MasterDataRepositoryImpl @Inject constructor(
    private val api: ApiInterface,
    private val localDataSource: LocalDataSource
) : MasterDataRepository {
    private var cachedMasterData: MasterDataStatic? = null
    private val _masterDataFlow = MutableStateFlow<MasterDataStatic?>(null)
    override val masterDataFlow: StateFlow<MasterDataStatic?> = _masterDataFlow

    override suspend fun refreshMasterDataIfNeeded() {
        //cache
        cachedMasterData?.let {
            _masterDataFlow.value = it
            //return
        }

        //local
        localDataSource.getMasterData()?.let {
            cachedMasterData = it
            _masterDataFlow.value = it

            val diffDays = Duration.between(Instant.parse(it.dateEnregistement), Instant.now()).toDays()
            //if (diffDays < 2) return
        }
        //api

        try {
            val response = api.getMasterDataStatic()

            if (response.status == 200) {
                val masterData = response.data.copy(
                    dateEnregistement = response.timestamp.toString()
                )

                if (
                    response.data.officialExercise.isNotEmpty() &&
                    response.data.muscles.isNotEmpty() &&
                    response.data.musclesGroup.isNotEmpty()
                ) {
                    localDataSource.saveMasterData(masterData)
                }

                cachedMasterData = masterData
                _masterDataFlow.value = masterData
            }
        } catch (_: Exception) {

        }
    }


}
