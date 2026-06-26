import java.util.Properties

val v = Properties().apply {
    load(rootProject.file("versions.properties").inputStream())
}

plugins {
    id("com.android.library")
}

android {
    namespace = "com.termux"
    compileSdk = v.getProperty("compileSdk").toInt()
    ndkVersion = v.getProperty("ndkVersion")

    defaultConfig {
        minSdk = v.getProperty("minSdk").toInt()

        buildConfigField("String", "TERMUX_PACKAGE_VARIANT", "\"apt-android-7\"")
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    lint {
        disable += "ProtectedPermissions"
    }
}

dependencies {
    api(project(":termux:view"))
    api(project(":termux:shared"))
    implementation("androidx.annotation:annotation:${v.getProperty("annotationVersion")}")
    implementation("androidx.core:core:${v.getProperty("coreKtxVersion")}")
    implementation("androidx.drawerlayout:drawerlayout:${v.getProperty("drawerLayoutVersion")}")
    implementation("com.google.android.material:material:${v.getProperty("materialVersion")}")
    implementation("com.google.guava:guava:24.1-jre")
    implementation("io.noties.markwon:core:4.6.2")
    implementation("io.noties.markwon:ext-strikethrough:4.6.2")
    implementation("io.noties.markwon:linkify:4.6.2")
    implementation("io.noties.markwon:recycler:4.6.2")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")
}

android {
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }
}
