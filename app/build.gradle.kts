import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

val keystorePropertiesFile = rootProject.file("app/release-key.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(keystorePropertiesFile.inputStream())
}

android {
    namespace = "com.neo.ide"
    compileSdk = rootProject.ext.compileSdk

    defaultConfig {
        applicationId = "com.neo.ide"
        minSdk = rootProject.ext.minSdk
        targetSdk = rootProject.ext.targetSdk
        versionCode = rootProject.ext.appVersionCode
        versionName = rootProject.ext.appVersionName

        vectorDrawables {
            useSupportLibrary = true
        }
    }
    
    signingConfigs {
        create("release") {
            if (keystorePropertiesFile.exists()) {
                storeFile = file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.findByName("release")
        }
        debug {
            isMinifyEnabled = true
            isShrinkResources = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            signingConfig = signingConfigs.findByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("com.google.code.findbugs:jsr305:${rootProject.ext.jsr305Version}")
    implementation("androidx.core:core-ktx:${rootProject.ext.coreKtxVersion}")
    implementation("androidx.appcompat:appcompat:${rootProject.ext.appcompatVersion}")
    implementation("com.google.android.material:material:${rootProject.ext.materialVersion}")
    implementation("androidx.constraintlayout:constraintlayout:${rootProject.ext.constraintLayoutVersion}")
    implementation("androidx.coordinatorlayout:coordinatorlayout:${rootProject.ext.coordinatorLayoutVersion}")
    implementation("androidx.drawerlayout:drawerlayout:${rootProject.ext.drawerLayoutVersion}")
    implementation("androidx.recyclerview:recyclerview:${rootProject.ext.recyclerviewVersion}")
    implementation("androidx.viewpager2:viewpager2:${rootProject.ext.viewpager2Version}")
    implementation("androidx.fragment:fragment-ktx:${rootProject.ext.fragmentKtxVersion}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${rootProject.ext.lifecycleVersion}")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:${rootProject.ext.lifecycleVersion}")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${rootProject.ext.lifecycleVersion}")
    implementation("androidx.annotation:annotation:${rootProject.ext.annotationVersion}")
    implementation("androidx.dynamicanimation:dynamicanimation:${rootProject.ext.dynamicAnimationVersion}")
    implementation("androidx.activity:activity-ktx:${rootProject.ext.activityKtxVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${rootProject.ext.coroutinesVersion}")
    implementation("androidx.transition:transition:${rootProject.ext.transitionVersion}")
    implementation("com.airbnb.android:lottie:${rootProject.ext.lottieVersion}")
    implementation("com.squareup.okhttp3:okhttp:${rootProject.ext.okhttpVersion}")
    implementation("org.json:json:20231013")
    implementation(project(":termux:application"))
}
