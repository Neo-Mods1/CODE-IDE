plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    namespace = "com.termux"
    compileSdk = project.property("compileSdk").toString().toInt()

    defaultConfig {
        minSdk = project.property("minSdk").toString().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

dependencies {
    implementation("androidx.annotation:annotation:${project.property("annotationVersion")}")
    implementation("androidx.core:core-ktx:${project.property("coreKtxVersion")}")
    implementation("androidx.drawerlayout:drawerlayout:${project.property("drawerLayoutVersion")}")
    implementation("com.google.android.material:material:${project.property("materialVersion")}")
    implementation(project(":termux:view"))
    implementation(project(":termux:shared"))
}
