
####################################
# ANDROID / KOTLIN BASICS
####################################
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-keep class kotlin.Metadata { *; }

####################################
# ROOM Minimal
####################################
-keep @androidx.room.Dao class * { *; }
-keep class * extends androidx.room.RoomDatabase

-keep class com.dyentrack.app.data.local.database.Converters { *; }


####################################
# GSON - ENHANCED FOR GENERICS
####################################

# Keep all data model classes with all their fields
-keep class com.dyentrack.app.data.local.entities.** { *; }
-keep class com.dyentrack.app.domain.model.ApiResponse { *; }
-keep class com.dyentrack.app.domain.model.User { *; }
-keep class com.dyentrack.app.domain.model.ResponseAuth { *; }
-keep class com.dyentrack.app.domain.model.Token { *; }
-keepclassmembers enum com.dyentrack.app.domain.model.AuthError { *; }
-keepclassmembers enum com.dyentrack.app.domain.model.AuthStatus { *; }
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

####################################
# RETROFIT / OKHTTP (si API)
####################################
-keep class retrofit2.** { *; }
-dontwarn okhttp3.**

####################################
# REMOVE WARNINGS SAFE
####################################
-dontwarn java.lang.invoke.**
-dontwarn org.jetbrains.annotations.**




# Google Mobile Ads SDK
-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.android.gms.common.** { *; }

