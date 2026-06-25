import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

val compileSdk = project.property("compileSdk").toString().toInt()
val minSdk = project.property("minSdk").toString().toInt()
val targetSdk = project.property("targetSdk").toString().toInt()
val appVersionCode = project.property("appVersionCode").toString().toInt()
val appVersionName = project.property("appVersionName").toString()

val keystorePropertiesFile = rootProject.file("app/release-key.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(keystorePropertiesFile.inputStream())
}

android {
    namespace = "com.neo.ide"
    this.compileSdk = compileSdk

    defaultConfig {
        applicationId = "com.neo.ide"
        this.minSdk = minSdk
        this.targetSdk = targetSdk
        versionCode = appVersionCode
        versionName = appVersionName

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
    implementation("com.google.code.findbugs:jsr305:${project.property("jsr305Version")}")
    implementation("androidx.core:core-ktx:${project.property("coreKtxVersion")}")
    implementation("androidx.appcompat:appcompat:${project.property("appcompatVersion")}")
    implementation("com.google.android.material:material:${project.property("materialVersion")}")
    implementation("androidx.constraintlayout:constraintlayout:${project.property("constraintLayoutVersion")}")
    implementation("androidx.coordinatorlayout:coordinatorlayout:${project.property("coordinatorLayoutVersion")}")
    implementation("androidx.drawerlayout:drawerlayout:${project.property("drawerLayoutVersion")}")
    implementation("androidx.recyclerview:recyclerview:${project.property("recyclerviewVersion")}")
    implementation("androidx.viewpager2:viewpager2:${project.property("viewpager2Version")}")
    implementation("androidx.fragment:fragment-ktx:${project.property("fragmentKtxVersion")}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${project.property("lifecycleVersion")}")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:${project.property("lifecycleVersion")}")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${project.property("lifecycleVersion")}")
    implementation("androidx.annotation:annotation:${project.property("annotationVersion")}")
    implementation("androidx.dynamicanimation:dynamicanimation:${project.property("dynamicAnimationVersion")}")
    implementation("androidx.activity:activity-ktx:${project.property("activityKtxVersion")}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${project.property("coroutinesVersion")}")
    implementation("androidx.transition:transition:${project.property("transitionVersion")}")
    implementation("com.airbnb.android:lottie:${project.property("lottieVersion")}")
    implementation("com.squareup.okhttp3:okhttp:${project.property("okhttpVersion")}")
    implementation("org.json:json:20231013")
    implementation(project(":termux:application"))
}
