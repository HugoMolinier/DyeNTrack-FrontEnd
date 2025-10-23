package com.dyentrack.app.domain.usecase

import com.dyentrack.app.data.repository.MasterDataRepository
import com.dyentrack.app.domain.model.MasterDataStatic
import javax.inject.Inject

class GetMasterDataStaticUseCase @Inject constructor(private val repo: MasterDataRepository) {
    suspend operator fun invoke(): MasterDataStatic = repo.getMasterDataStatic()
}