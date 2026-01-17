package com.dyentrack.app.data.local.database

import androidx.room.TypeConverter
import com.dyentrack.app.core.utils.GsonProvider
import com.dyentrack.app.domain.model.dayData.NutritionTrack
import com.dyentrack.app.domain.model.dayData.PhysioTrack
import com.dyentrack.app.domain.model.dayData.SeanceTrack
import com.dyentrack.app.domain.model.preset.ExerciceInPreset
import com.google.gson.reflect.TypeToken

class Converters {


    @TypeConverter
    fun fromNutritionTrack(track: NutritionTrack?): String? {
        return GsonProvider.gson.toJson(track)
    }

    @TypeConverter
    fun toNutritionTrack(value: String?): NutritionTrack {

        return GsonProvider.gson.fromJson(value, NutritionTrack::class.java) ?: NutritionTrack()

    }

    @TypeConverter
    fun fromPhysioTrack(track: PhysioTrack?): String? {
        return GsonProvider.gson.toJson(track)
    }

    @TypeConverter
    fun toPhysioTrack(value: String?): PhysioTrack {
        return GsonProvider.gson.fromJson(value, PhysioTrack::class.java) ?: PhysioTrack()
    }

    @TypeConverter
    fun fromSeanceTrack(track: SeanceTrack?): String? {
        return GsonProvider.gson.toJson(track)
    }

    @TypeConverter
    fun toSeanceTrack(value: String?): SeanceTrack? {
        return value?.let { GsonProvider.gson.fromJson(it, SeanceTrack::class.java) }
    }

    @TypeConverter
    fun fromExerciceList(value: List<ExerciceInPreset>?): String {
        return GsonProvider.gson.toJson(value)
    }

    @TypeConverter
    fun toExerciceList(value: String): List<ExerciceInPreset>? {
        val type = TypeToken.getParameterized(List::class.java, ExerciceInPreset::class.java).type
        return GsonProvider.gson.fromJson(value, type)
    }

}