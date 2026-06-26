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
    implementation("com.google.android.material:material:${v.getProperty("materialVersion")}")
    implementation("com.google.guava:guava:24.1-jre")
    implementation("io.noties.markwon:core:4.6.2")
    implementation("io.noties.markwon:ext-strikethrough:4.6.2")
    implementation("io.noties.markwon:linkify:4.6.2")
    implementation("io.noties.markwon:recycler:4.6.2")
    implementation("org.lsposed.hiddenapibypass:hiddenapibypass:6.1")
    implementation("commons-io:commons-io:2.5")
    implementation("androidx.window:window:1.1.0")
    implementation("com.termux:termux-am-library:v2.0.0")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")
}
