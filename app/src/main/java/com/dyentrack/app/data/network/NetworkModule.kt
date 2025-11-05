package com.dyentrack.app.data.network

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL: String = "http://4.233.56.101:8080/api/"
    private const val TestToken : String ="eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInN1YiI6IjEiLCJpYXQiOjE3NjE1OTEyMzcsImV4cCI6MTc2MjE5NjAzN30.Ipr7gQcA_iAh-y0is8jW8Fyov9lKRNtYS_PJAvqo1qE"
    private const val AccountPersPerfToken: String = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjIsInN1YiI6IjIiLCJpYXQiOjE3NjIyOTU4MDYsImV4cCI6MTc2MjkwMDYwNn0.pUQdTgCP9y5ByeWtdH5ie-U6al9xs0Ak71uCn0362DU"
    private const val Apikey : String ="gK7WOGlkQQxEiDQeYxweqbKluRfulBxkXHzqUP8k3U0="

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $AccountPersPerfToken")
                    .addHeader("X-API-KEY", Apikey)
                    .addHeader("Accept", "application/json")
                val request = requestBuilder.build()
                Log.d("REQUEST API", " : ${request}")
                chain.proceed(request)
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    fun provideApiInterface(client: OkHttpClient, gson: Gson): ApiInterface {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiInterface::class.java)
    }
}
