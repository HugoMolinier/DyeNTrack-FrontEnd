package com.dyentrack.app.data.network

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

    private const val BASE_URL: String = "http://10.0.2.2:8080/api/"
    private const val TestToken : String ="eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInN1YiI6IjEiLCJpYXQiOjE3NjA5ODA4MTUsImV4cCI6MTc2MTU4NTYxNX0.YJqJyHUFLsJnuYSxRxlnHyf9offaO3cmXB7xikkTH3o"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $TestToken")
                    .addHeader("Accept", "application/json")
                val request = requestBuilder.build()
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
