plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    namespace = "com.termux.shared"
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
    implementation("androidx.appcompat:appcompat:${project.property("appcompatVersion")}")
    implementation("androidx.annotation:annotation:${project.property("annotationVersion")}")
    implementation("androidx.core:core-ktx:${project.property("coreKtxVersion")}")
    implementation("com.google.android.material:material:${project.property("materialVersion")}")
    implementation(project(":termux:view"))
}
