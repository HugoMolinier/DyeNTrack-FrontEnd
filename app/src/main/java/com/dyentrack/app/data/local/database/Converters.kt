package com.dyentrack.app.data.local.database

import android.util.Log
import androidx.room.TypeConverter
import com.dyentrack.app.domain.model.PlannedExerciseVO
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromPlannedExerciseList(value: List<PlannedExerciseVO>?): String {
        val json = gson.toJson(value)
        Log.d("DEBUG", "dlkdlz${json}")
        return json
    }

    @TypeConverter
    fun toPlannedExerciseList(value: String?): List<PlannedExerciseVO> {
        Log.d("DEBUG", "rr${value}")
        if (value.isNullOrEmpty()) return emptyList()
        val type = object : TypeToken<List<PlannedExerciseVO>>() {}.type
        return gson.fromJson(value, type)
    }

}