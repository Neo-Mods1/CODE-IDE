plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.termux.app"
    compileSdk = Integer.parseInt(rootProject.findProperty("compileSdk").toString())

    defaultConfig {
        minSdk = Integer.parseInt(rootProject.findProperty("minSdk").toString())
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":termux:emulator"))
    implementation(project(":termux:view"))
    implementation(project(":termux:shared"))
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.drawerlayout:drawerlayout:1.2.0")
}
