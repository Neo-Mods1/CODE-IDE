# JSR305
-keep class javax.annotation.** { *; }

# OkHttp / Okio
-dontwarn okio.**
-dontwarn okhttp3.**
-keep class okio.** { *; }
-keep class okhttp3.** { *; }

# Lottie
-dontwarn com.airbnb.lottie.**
-keep class com.airbnb.lottie.** { *; }

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# Fetch2
-keep class com.tonyodev.fetch2.** { *; }
-keep class com.tonyodev.fetch2core.** { *; }
-dontwarn com.tonyodev.fetch2.**
-dontwarn com.tonyodev.fetch2core.**
