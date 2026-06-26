import java.util.Properties

val v = Properties().apply {
    load(rootProject.file("versions.properties").inputStream())
}

plugins {
    id("com.android.library")
}

android {
    namespace = "com.termux.view"
    compileSdk = v.getProperty("compileSdk").toInt()

    defaultConfig {
        minSdk = v.getProperty("minSdk").toInt()
        
        ndk {
            abiFilters += listOf("arm64-v8a", "armeabi-v7a")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    api(project(":termux:emulator"))
    implementation("androidx.annotation:annotation:${v.getProperty("annotationVersion")}")
}
