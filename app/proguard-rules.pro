# R8 optimizations
-allowaccessmodification
-repackageclasses ''
-optimizations !code/simplification/cast,!field/*,!class/merging/*

# JSR305
-keep class javax.annotation.** { *; }

# OkHttp / Okio
-dontwarn okio.**
-dontwarn okhttp3.**

# Lottie
-dontwarn com.airbnb.lottie.**

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Apache Commons Compress / XZ
-dontwarn org.apache.commons.compress.**
-dontwarn org.tukaani.xz.**

# Bootstrap JNI
-keep class com.termux.app.TermuxInstaller { *; }
