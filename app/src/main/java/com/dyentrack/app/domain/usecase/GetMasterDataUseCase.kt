package com.dyentrack.app.domain.usecase

import com.dyentrack.app.data.repository.MasterDataRepository
import com.dyentrack.app.domain.model.MasterDataStatic
import javax.inject.Inject

class GetMasterDataUseCase @Inject constructor(
    private val masterDataRepository: MasterDataRepository){
    private var masterDataStatic : MasterDataStatic ?= null

    suspend operator fun invoke(): MasterDataStatic?{
        if(masterDataStatic ==null) {
            masterDataStatic =masterDataRepository.getMasterDataStatic()
        }
        return masterDataStatic
    }

}