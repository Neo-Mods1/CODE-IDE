import java.io.File
import java.util.Properties

val v = Properties().apply {
    load(rootProject.file("versions.properties").inputStream())
}

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
    compileSdk = v.getProperty("compileSdk").toInt()

    defaultConfig {
        applicationId = "com.neo.ide"
        minSdk = v.getProperty("minSdk").toInt()
        targetSdk = v.getProperty("targetSdk").toInt()
        versionCode = v.getProperty("appVersionCode").toInt()
        versionName = v.getProperty("appVersionName")

        vectorDrawables {
            useSupportLibrary = true
        }

        ndk {
            abiFilters += listOf("arm64-v8a", "armeabi-v7a")
        }
    }

    val isRelease = gradle.startParameter.taskNames.any { it.contains("Release", ignoreCase = true) }
    if (isRelease) {
        splits {
            abi {
                isEnable = true
                reset()
                include("arm64-v8a", "armeabi-v7a")
                isUniversalApk = false
            }
        }
    }

    signingConfigs {
        create("appSigning") {
            if (keystorePropertiesFile.exists()) {
                storeFile = file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
            }
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
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

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isJniDebuggable = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.findByName("appSigning")
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            isJniDebuggable = true
            signingConfig = signingConfigs.findByName("appSigning")
        }
    }
}

dependencies {
    // Sub-modules
    implementation(project(":editor"))
    implementation(project(":lsp"))
    implementation(project(":tooling"))
    implementation(project(":projects"))
    implementation(project(":templates"))

    // Core Android
    implementation("com.google.code.findbugs:jsr305:${v.getProperty("jsr305Version")}")
    implementation("androidx.core:core-ktx:${v.getProperty("coreKtxVersion")}")
    implementation("androidx.appcompat:appcompat:${v.getProperty("appcompatVersion")}")
    implementation("com.google.android.material:material:${v.getProperty("materialVersion")}")
    implementation("androidx.constraintlayout:constraintlayout:${v.getProperty("constraintLayoutVersion")}")
    implementation("androidx.coordinatorlayout:coordinatorlayout:${v.getProperty("coordinatorLayoutVersion")}")
    implementation("androidx.drawerlayout:drawerlayout:${v.getProperty("drawerLayoutVersion")}")
    implementation("androidx.recyclerview:recyclerview:${v.getProperty("recyclerviewVersion")}")
    implementation("androidx.viewpager2:viewpager2:${v.getProperty("viewpager2Version")}")
    implementation("androidx.fragment:fragment-ktx:${v.getProperty("fragmentKtxVersion")}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${v.getProperty("lifecycleVersion")}")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:${v.getProperty("lifecycleVersion")}")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${v.getProperty("lifecycleVersion")}")
    implementation("androidx.annotation:annotation:${v.getProperty("annotationVersion")}")
    implementation("androidx.dynamicanimation:dynamicanimation:${v.getProperty("dynamicAnimationVersion")}")
    implementation("androidx.activity:activity-ktx:${v.getProperty("activityKtxVersion")}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${v.getProperty("coroutinesVersion")}")
    implementation("androidx.transition:transition:${v.getProperty("transitionVersion")}")
    implementation("com.airbnb.android:lottie:${v.getProperty("lottieVersion")}")
    implementation("com.squareup.okhttp3:okhttp:${v.getProperty("okhttpVersion")}")
    implementation("org.apache.commons:commons-compress:${v.getProperty("commonsCompressVersion")}")
    implementation("org.tukaani:xz:${v.getProperty("xzVersion")}")
    implementation("org.json:json:${v.getProperty("jsonVersion")}")
    implementation("org.greenrobot:eventbus:${v.getProperty("eventBusVersion")}")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:${v.getProperty("desugarVersion")}")
    implementation("com.google.android.flexbox:flexbox:${v.getProperty("flexboxVersion")}")
}
