plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    namespace = "com.termux.shared"
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
    implementation("androidx.appcompat:appcompat:${rootProject.ext.appcompatVersion}")
    implementation("androidx.annotation:annotation:${rootProject.ext.annotationVersion}")
    implementation("androidx.core:core-ktx:${rootProject.ext.coreKtxVersion}")
    implementation("com.google.android.material:material:${rootProject.ext.materialVersion}")
    implementation(project(":termux:view"))
}
