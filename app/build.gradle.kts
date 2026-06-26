import java.util.Base64
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

val signingFromEnv = !System.getenv("KEYSTORE_BASE64").isNullOrEmpty()

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
    }

    signingConfigs {
        create("release") {
            if (signingFromEnv) {
                val ksFile = File(projectDir, "release-key-env.jks")
                val decoded = Base64.getDecoder().decode(System.getenv("KEYSTORE_BASE64"))
                ksFile.writeBytes(decoded)
                storeFile = ksFile
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEY_PASSWORD")
            } else if (keystorePropertiesFile.exists()) {
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

    splits {
        abi {
            isEnable = true
            reset()
            include("arm64-v8a", "armeabi-v7a")
            isUniversalApk = false
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
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
    implementation(project(":termux:view"))
    implementation(project(":termux:shared"))
    implementation(project(":termux:application")) {
        exclude(group = "com.google.guava", module = "listenablefuture")
    }
    implementation("com.google.guava:listenablefuture:${v.getProperty("listenablefutureVersion")}")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:${v.getProperty("desugarVersion")}")
}
