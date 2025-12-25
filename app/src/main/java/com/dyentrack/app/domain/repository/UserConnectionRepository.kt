package com.dyentrack.app.domain.repository

import com.dyentrack.app.domain.model.AuthStatus
import com.dyentrack.app.domain.model.ResponseAuth
import com.dyentrack.app.domain.model.User

interface UserConnectionRepository {

    suspend fun registerUser(email:String,password:String,pseudo:String): ResponseAuth
    suspend fun loginUser(email:String,password:String): ResponseAuth
    fun isValideToken(): AuthStatus
    fun getUser():User?
    fun getToken(): String
    fun logout():ResponseAuth
}