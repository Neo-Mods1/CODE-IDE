import java.util.Properties

val v = Properties().apply {
    load(rootProject.file("versions.properties").inputStream())
}

plugins {
    id("com.android.library")
}

android {
    namespace = "com.termux.shared"
    compileSdk = v.getProperty("compileSdk").toInt()

    defaultConfig {
        minSdk = v.getProperty("minSdk").toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":termux:view"))
    implementation(project(":termux:emulator"))
    implementation("com.google.android.material:material:${v.getProperty("materialVersion")}")
    implementation("androidx.appcompat:appcompat:${v.getProperty("appcompatVersion")}")
    implementation("androidx.annotation:annotation:${v.getProperty("annotationVersion")}")
}
