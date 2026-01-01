package com.dyentrack.app.data.local.database

import androidx.room.TypeConverter
import com.dyentrack.app.domain.model.NutritionTrack
import com.dyentrack.app.domain.model.PhysioTrack
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

    @TypeConverter
    fun fromNutritionTrack(track: NutritionTrack?): String? {
        return gson.toJson(track)
    }

    @TypeConverter
    fun toNutritionTrack(value: String?): NutritionTrack {

            return  gson.fromJson(value, NutritionTrack::class.java)
                ?: NutritionTrack()

    }

    @TypeConverter
    fun fromPhysioTrack(track: PhysioTrack?): String? {
        return gson.toJson(track)
    }

    @TypeConverter
    fun toPhysioTrack(value: String?): PhysioTrack {
        return gson.fromJson(value, PhysioTrack::class.java) ?: PhysioTrack(
            weight = 0.0,
            step = 0,
            hourOfSleep = 0.0,
            mood = ""
        )
    }

}