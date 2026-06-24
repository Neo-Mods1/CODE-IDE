plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    namespace = "com.termux.view"
    compileSdk = rootProject.ext.compileSdk

    defaultConfig {
        minSdk = rootProject.ext.minSdk
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    api(project(":termux:emulator"))
    implementation("androidx.annotation:annotation:${rootProject.ext.annotationVersion}")
}
