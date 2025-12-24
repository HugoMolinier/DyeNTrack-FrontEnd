package com.dyentrack.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserLocal(
    @PrimaryKey val id: Long,
    val pseudo: String,
    val dateRegister: String,
    val birthdate: String?,
    val height: Int?,
    val sexe: Boolean?
)
