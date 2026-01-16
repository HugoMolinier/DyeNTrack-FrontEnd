package com.dyentrack.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "session")
data class SessionLocal(
    @PrimaryKey val id: Int = 0,
    val token: String,
    val tokenType: String,
    val expirationTime: Long
)
