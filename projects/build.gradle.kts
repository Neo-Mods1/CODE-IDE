import java.util.Properties

val v = Properties().apply {
    load(rootProject.file("versions.properties").inputStream())
}

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.neo.ide.projects"
    compileSdk = v.getProperty("compileSdk").toInt()

    defaultConfig {
        minSdk = v.getProperty("minSdk").toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("org.greenrobot:eventbus:${v.getProperty("eventBusVersion")}")
    implementation("com.google.code.gson:gson:${v.getProperty("gsonVersion")}")
    implementation("org.slf4j:slf4j-api:${v.getProperty("slf4jVersion")}")

    implementation("androidx.core:core-ktx:${v.getProperty("coreKtxVersion")}")
    implementation("androidx.appcompat:appcompat:${v.getProperty("appcompatVersion")}")
    implementation("androidx.annotation:annotation:${v.getProperty("annotationVersion")}")
}
