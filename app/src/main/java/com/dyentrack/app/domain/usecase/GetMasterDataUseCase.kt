package com.dyentrack.app.domain.usecase


import com.dyentrack.app.domain.model.MasterDataStatic
import com.dyentrack.app.domain.repository.MasterDataRepository
import javax.inject.Inject

class GetMasterDataStaticUseCase @Inject constructor(private val repo: MasterDataRepository) {
    suspend operator fun invoke(): MasterDataStatic = repo.getMasterDataStatic()
}