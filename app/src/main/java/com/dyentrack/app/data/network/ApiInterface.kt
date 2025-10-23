package com.dyentrack.app.data.network

import com.dyentrack.app.domain.model.ApiResponse
import com.dyentrack.app.domain.model.DayDataRequest
import com.dyentrack.app.domain.model.MasterDataStatic
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface ApiInterface  {




    @GET("master/static")
    suspend fun getMasterDataStatic(): ApiResponse<MasterDataStatic>


    @POST("daydata/save")
    suspend fun saveDayData(@Body dayDataRequest: DayDataRequest): ApiResponse<DayDataRequest>

    @GET("daydata/getAll")
    suspend fun getAllDayDataByDay(): ApiResponse<List<DayDataRequest>>

}
