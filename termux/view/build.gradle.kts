plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    namespace = "com.termux.view"
    compileSdk = project.property("compileSdk").toString().toInt()

    defaultConfig {
        minSdk = project.property("minSdk").toString().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    api(project(":termux:emulator"))
    implementation("androidx.annotation:annotation:${project.property("annotationVersion")}")
}
