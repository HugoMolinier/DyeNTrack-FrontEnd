
####################################
# ANDROID / KOTLIN BASICS
####################################
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-keep class kotlin.Metadata { *; }


####################################
# ROOM
####################################
-keep class androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Dao class * { *; }
-keepclassmembers class * {
    @androidx.room.* <methods>;
}

####################################
# WORK MANAGER
####################################
-keep class androidx.work.** { *; }
-dontwarn androidx.work.**

####################################
# GSON - ENHANCED FOR GENERICS
####################################
-keep class com.google.gson.** { *; }

# Keep all data model classes with all their fields
-keep class com.dyentrack.app.data.local.entities.** { *; }
-keep class com.dyentrack.app.domain.model.ApiResponse { *; }
-keep class com.dyentrack.app.domain.model.User { *; }
-keep class com.dyentrack.app.domain.model.ResponseAuth { *; }
-keep class com.dyentrack.app.domain.model.MasterDataStatic { *; }
-keep class com.dyentrack.app.domain.model.InfoExerciseUser { *; }
-keep class com.dyentrack.app.domain.model.preset.Preset { *; }
-keep class com.dyentrack.app.domain.model.dayData.DayDataRequest { *; }
-keep class com.dyentrack.app.data.network.dto.** { *; }

# IMPORTANT: Keep ApiResponse with its generic type parameter
-keepclassmembers class com.dyentrack.app.data.network.dto.** {
    <fields>;
    <init>();
}
# Keep all fields in model classes
-keepclassmembers class com.dyentrack.app.data.local.entities.** {
    <fields>;
    <init>();
}
-keepclassmembers class com.dyentrack.app.domain.model.** {
    <fields>;
    <init>();
}

# Keep SerializedName annotations
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Prevent stripping of generic type information
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep generic type information for classes with TypeToken
-keep class * extends com.google.gson.reflect.TypeToken { *; }


####################################
# COROUTINES
####################################
-dontwarn kotlinx.coroutines.**
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
-keep class kotlinx.coroutines.** { *; }

####################################
# HILT / DAGGER (si présent ou futur)
####################################
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-dontwarn dagger.hilt.**

####################################
# RETROFIT / OKHTTP (si API)
####################################
-keep class retrofit2.** { *; }
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

-dontwarn retrofit2.**
-dontwarn okhttp3.**

####################################
# REMOVE WARNINGS SAFE
####################################
-dontwarn java.lang.invoke.**
-dontwarn org.jetbrains.annotations.**




# Google Mobile Ads SDK
-keep class com.google.android.gms.ads.** { *; }
-keep interface com.google.android.gms.ads.** { *; }

-keep class com.google.android.gms.common.** { *; }
-keep interface com.google.android.gms.common.** { *; }

-keep class com.google.android.gms.internal.ads.** { *; }
-keep interface com.google.android.gms.internal.ads.** { *; }