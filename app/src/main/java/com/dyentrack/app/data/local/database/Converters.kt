package com.dyentrack.app.data.local.database

import androidx.room.TypeConverter
import com.dyentrack.app.domain.model.PlannedExerciseVO
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromPlannedExerciseList(value: List<PlannedExerciseVO>?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toPlannedExerciseList(value: String?): List<PlannedExerciseVO> {

        val type = object : TypeToken<List<PlannedExerciseVO>>() {}.type
        return gson.fromJson(value, type)
    }

}