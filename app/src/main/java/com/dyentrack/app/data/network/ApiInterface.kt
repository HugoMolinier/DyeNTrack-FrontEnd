package com.dyentrack.app.data.network

import com.dyentrack.app.data.network.dto.IdentificationRequest
import com.dyentrack.app.data.network.dto.InfoExerciseUserContentRequest
import com.dyentrack.app.data.network.dto.UserUpdate
import com.dyentrack.app.domain.model.ApiResponse
import com.dyentrack.app.domain.model.InfoExerciseUser
import com.dyentrack.app.domain.model.MasterDataStatic
import com.dyentrack.app.domain.model.ResponseAuth
import com.dyentrack.app.domain.model.User
import com.dyentrack.app.domain.model.dayData.DayDataRequest
import com.dyentrack.app.domain.model.preset.Preset
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiInterface {


    @GET("master/static")
    suspend fun getMasterDataStatic(): ApiResponse<MasterDataStatic>


    @POST("daydata/save")
    suspend fun saveDayData(@Body dayDataRequest: DayDataRequest): ApiResponse<DayDataRequest>

    @GET("daydata/getAll")
    suspend fun getAllDayDataByDay(): ApiResponse<List<DayDataRequest>>

    @POST("info-exercise-user/update/{id}")
    suspend fun updateInfoExerciseUser(
        @Path("id") id: Long, @Body request: InfoExerciseUserContentRequest
    ): ApiResponse<InfoExerciseUser>


    @GET("info-exercise-user/getAll")
    suspend fun getAllinfoExerciseUser(): ApiResponse<List<InfoExerciseUser>>

    @POST("user/register")
    suspend fun registerUser(
        @Body request: IdentificationRequest
    ): ApiResponse<ResponseAuth>

    @POST("user/login")
    suspend fun loginUser(
        @Body request: IdentificationRequest
    ): ApiResponse<ResponseAuth>

    @PUT("user/update")
    suspend fun updateUser(
        @Body request: UserUpdate
    ): ApiResponse<User>


    @DELETE("preset-seances/delete/{id}")
    suspend fun deletePreset(
        @Path("id") id: Long,
    ): ApiResponse<Unit>


    @POST("preset-seances/save")
    suspend fun savePreset(
        @Body preset: Preset,
    ): ApiResponse<Preset>


    @GET("preset-seances/getAll")
    suspend fun getAllPreset(): ApiResponse<List<Preset>>


}
