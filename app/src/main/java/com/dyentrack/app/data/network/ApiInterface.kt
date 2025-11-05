package com.dyentrack.app.data.network

import com.dyentrack.app.data.network.dto.InfoExerciseUserContentRequest
import com.dyentrack.app.domain.model.ApiResponse
import com.dyentrack.app.domain.model.DayDataRequest
import com.dyentrack.app.domain.model.MasterDataStatic
import com.dyentrack.app.domain.model.InfoExerciseUser
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiInterface  {




    @GET("master/static")
    suspend fun getMasterDataStatic(): ApiResponse<MasterDataStatic>


    @POST("daydata/save")
    suspend fun saveDayData(@Body dayDataRequest: DayDataRequest): ApiResponse<DayDataRequest>

    @GET("daydata/getAll")
    suspend fun getAllDayDataByDay(): ApiResponse<List<DayDataRequest>>

    @POST("info-exercise-user/update/{id}")
    suspend fun updateInfoExerciseUser(
        @Path("id") id: Long,
        @Body request: InfoExerciseUserContentRequest
    ): ApiResponse<InfoExerciseUser>


    @GET("info-exercise-user/getAll")
    suspend fun getAllinfoExerciseUser(): ApiResponse<List<InfoExerciseUser>>


}
