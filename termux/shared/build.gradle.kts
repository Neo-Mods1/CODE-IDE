import java.util.Properties

val v = Properties().apply {
    load(rootProject.file("versions.properties").inputStream())
}

plugins {
    id("com.android.library")
}

android {
    namespace = "com.termux.shared"
    compileSdk = v.getProperty("compileSdk").toInt()
    ndkVersion = v.getProperty("ndkVersion")

    defaultConfig {
        minSdk = v.getProperty("minSdk").toInt()

        externalNativeBuild {
            ndkBuild {
                cppFlags += ""
            }
        }
        
        ndk {
            abiFilters += listOf("arm64-v8a", "armeabi-v7a")
        }
    }

    externalNativeBuild {
        ndkBuild {
            path = file("src/main/cpp/Android.mk")
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    api(project(":termux:view"))
    implementation("androidx.appcompat:appcompat:${v.getProperty("appcompatVersion")}")
    implementation("androidx.annotation:annotation:${v.getProperty("annotationVersion")}")
    implementation("androidx.core:core:${v.getProperty("coreKtxVersion")}")
    implementation("androidx.window:window:${v.getProperty("windowVersion")}")
    implementation("com.google.android.material:material:${v.getProperty("materialVersion")}")
    implementation("com.google.guava:guava:${v.getProperty("guavaVersion")}")
    implementation("io.noties.markwon:core:${v.getProperty("markwonVersion")}")
    implementation("io.noties.markwon:ext-strikethrough:${v.getProperty("markwonVersion")}")
    implementation("io.noties.markwon:linkify:${v.getProperty("markwonVersion")}")
    implementation("io.noties.markwon:recycler:${v.getProperty("markwonVersion")}")
    implementation("org.lsposed.hiddenapibypass:hiddenapibypass:${v.getProperty("hiddenApiBypassVersion")}")
    implementation("commons-io:commons-io:${v.getProperty("commonsIoVersion")}")
    implementation("com.termux:termux-am-library:${v.getProperty("termuxAmLibraryVersion")}")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:${v.getProperty("desugarVersion")}")
}
