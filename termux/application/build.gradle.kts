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
        manifestPlaceholders["TERMUX_PACKAGE_NAME"] = "com.termux"
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
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
    implementation("androidx.preference:preference:${v.getProperty("preferenceVersion")}")
    implementation("androidx.viewpager:viewpager:${v.getProperty("viewpagerVersion")}")
    implementation("com.google.android.material:material:${v.getProperty("materialVersion")}")
    implementation("com.google.guava:guava:${v.getProperty("guavaVersion")}")
    implementation("io.noties.markwon:core:${v.getProperty("markwonVersion")}")
    implementation("io.noties.markwon:ext-strikethrough:${v.getProperty("markwonVersion")}")
    implementation("io.noties.markwon:linkify:${v.getProperty("markwonVersion")}")
    implementation("io.noties.markwon:recycler:${v.getProperty("markwonVersion")}")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:${v.getProperty("desugarVersion")}")
}
