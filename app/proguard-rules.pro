# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# -----------------------------
# Retrofit & Gson
# -----------------------------

# Ne pas minifier les modèles (POJO) utilisés par Gson
-keep class com.dyentrack.app.domain.model.** { *; }
-keep class com.dyentrack.app.data.network.dto.** { *; }

# Ne pas minifier ApiResponse et ses génériques
-keep class com.dyentrack.app.domain.model.ApiResponse { *; }

# Garder les annotations Gson
-keepattributes Signature
-keepattributes *Annotation*

# Retrofit interface
-keep interface com.dyentrack.app.data.network.ApiInterface { *; }

# Optional : garder les classes Gson de type paramétré
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
