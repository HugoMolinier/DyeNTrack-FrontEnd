package com.dyentrack.app.data.local.database

import androidx.room.TypeConverter
import com.dyentrack.app.domain.model.dayData.NutritionTrack
import com.dyentrack.app.domain.model.dayData.PhysioTrack
import com.dyentrack.app.domain.model.dayData.PlannedExerciseVO
import com.dyentrack.app.domain.model.dayData.SeanceTrack
import com.dyentrack.app.domain.model.preset.ExerciceInPreset
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

        return gson.fromJson(value, NutritionTrack::class.java) ?: NutritionTrack()

    }

    @TypeConverter
    fun fromPhysioTrack(track: PhysioTrack?): String? {
        return gson.toJson(track)
    }

    @TypeConverter
    fun toPhysioTrack(value: String?): PhysioTrack {
        return gson.fromJson(value, PhysioTrack::class.java) ?: PhysioTrack()
    }

    @TypeConverter
    fun fromSeanceTrack(track: SeanceTrack?): String? {
        return gson.toJson(track)
    }

    @TypeConverter
    fun toSeanceTrack(value: String?): SeanceTrack? {
        return value?.let { gson.fromJson(it, SeanceTrack::class.java) }
    }

    @TypeConverter
    fun fromExerciceList(value: List<ExerciceInPreset>?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toExerciceList(value: String): List<ExerciceInPreset>? {
        val listType = object : TypeToken<List<ExerciceInPreset>>() {}.type
        return gson.fromJson(value, listType)
    }

}