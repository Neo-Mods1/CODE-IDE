import java.util.Properties

val v = Properties().apply {
    load(rootProject.file("versions.properties").inputStream())
}

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.neo.ide.templates"
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
    implementation("com.squareup:javapoet:${v.getProperty("javaPoetVersion")}")
    implementation("org.greenrobot:eventbus:${v.getProperty("eventBusVersion")}")
    implementation("com.google.auto.service:auto-service:${v.getProperty("autoServiceVersion", "1.1.1")}")
    annotationProcessor("com.google.auto.service:auto-service:${v.getProperty("autoServiceVersion", "1.1.1")}")

    implementation("androidx.core:core-ktx:${v.getProperty("coreKtxVersion")}")
    implementation("androidx.appcompat:appcompat:${v.getProperty("appcompatVersion")}")
    implementation("com.google.android.material:material:${v.getProperty("materialVersion")}")
    implementation("androidx.annotation:annotation:${v.getProperty("annotationVersion")}")
    implementation("androidx.constraintlayout:constraintlayout:${v.getProperty("constraintLayoutVersion")}")
    
    implementation(project(":tooling"))
    implementation("com.itsaky.androidide.build:java-compiler")
}
