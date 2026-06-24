plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    namespace = "com.termux"
    compileSdk = rootProject.ext.compileSdk

    defaultConfig {
        minSdk = rootProject.ext.minSdk
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
    implementation("androidx.annotation:annotation:${rootProject.ext.annotationVersion}")
    implementation("androidx.core:core-ktx:${rootProject.ext.coreKtxVersion}")
    implementation("androidx.drawerlayout:drawerlayout:${rootProject.ext.drawerLayoutVersion}")
    implementation("com.google.android.material:material:${rootProject.ext.materialVersion}")
    implementation(project(":termux:view"))
    implementation(project(":termux:shared"))
}
