package com.dyentrack.app.data.network.dto

data class UserUpdate(
    val pseudo:String?=null,
    val birthdate:String?=null,
    val height: Int?=null,
    val sexeMale: Boolean?=null
)

