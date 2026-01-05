package com.dyentrack.app.domain.usecase.masterdata


import com.dyentrack.app.domain.model.MasterDataStatic
import com.dyentrack.app.domain.model.preset.Preset
import com.dyentrack.app.domain.repository.MasterDataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class GetMasterDataStaticUseCase @Inject constructor(private val repo: MasterDataRepository) {
    val masterDataFlow: Flow<MasterDataStatic?> = repo.masterDataFlow

    suspend operator fun invoke() = repo.refreshMasterDataIfNeeded()
}