plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    namespace = "com.termux.emulator"
    compileSdk = project.property("compileSdk").toString().toInt()

    defaultConfig {
        minSdk = project.property("minSdk").toString().toInt()

        externalNativeBuild {
            ndkBuild {
                cFlags += arrayOf("-std=c11", "-Wall", "-Wextra", "-Os", "-fno-stack-protector", "-Wl,--gc-sections")
            }
        }
    }

    externalNativeBuild {
        ndkBuild {
            path = file("src/main/jni/Android.mk")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation("androidx.annotation:annotation:${project.property("annotationVersion")}")
}
