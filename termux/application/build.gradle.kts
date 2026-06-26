import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.util.Properties

val v = Properties().apply {
    load(rootProject.file("versions.properties").inputStream())
}

plugins {
    id("com.android.library")
}

// --- Bootstrap download & assembly generation (runs at configuration time like AndroidIDE's plugin) ---
val bootstrapVersion = "16.12.2023"
val bootstrapDir = layout.buildDirectory.dir("bootstrap-packages").get().asFile
val assemblyFile = file("src/main/cpp/termux-bootstrap-zip.S")

val bootstrapChecksums = mapOf(
    "aarch64" to "68da03ed270d59cafcd37981b00583c713b42cb440adf03d1bf980f39a55181d",
    "arm" to "f3d9f2da7338bd00b02a8df192bdc22ad431a5eef413cecf4cd78d7a54ffffbf",
    "x86_64" to "6e4e50a206c3384c36f141b2496c1a7c69d30429e4e20268c51a84143530af67"
)

bootstrapDir.mkdirs()

val bootstrapFiles = bootstrapChecksums.map { (arch, expectedSha256) ->
    val file = File(bootstrapDir, "bootstrap-${arch}.zip")
    val url = "https://github.com/AndroidIDEOfficial/terminal-packages/releases/download/bootstrap-${bootstrapVersion}/bootstrap-${arch}.zip"

    if (file.exists()) {
        val actualSha256 = sha256(file)
        if (actualSha256 == expectedSha256) {
            println("Bootstrap $arch already downloaded and verified.")
            return@map arch to file
        }
        println("Bootstrap $arch checksum mismatch, re-downloading...")
        file.delete()
    }

    println("Downloading bootstrap-$arch from $url ...")
    downloadFile(url, file)

    val actualSha256 = sha256(file)
    if (actualSha256 != expectedSha256) {
        throw RuntimeException(
            "Bootstrap $arch checksum mismatch! Expected: $expectedSha256, Got: $actualSha256"
        )
    }
    println("Bootstrap $arch verified. SHA256: $actualSha256")
    arch to file
}.toMap()

// Generate assembly file
assemblyFile.writeText(buildString {
    appendLine(".global blob")
    appendLine(".global blob_size")
    appendLine(".section .rodata")
    appendLine("blob:")
    appendLine("#if defined __aarch64__")
    appendLine("    .incbin \"${bootstrapFiles["aarch64"]!!.absolutePath.replace("\\", "\\\\")}\"")
    appendLine("#elif defined __arm__")
    appendLine("    .incbin \"${bootstrapFiles["arm"]!!.absolutePath.replace("\\", "\\\\")}\"")
    appendLine("#elif defined __x86_64__")
    appendLine("    .incbin \"${bootstrapFiles["x86_64"]!!.absolutePath.replace("\\", "\\\\")}\"")
    appendLine("#else")
    appendLine("# error Unsupported arch")
    appendLine("#endif")
    appendLine("1:")
    appendLine("blob_size:")
    appendLine("    .int 1b - blob")
    appendLine()
})
println("Generated ${assemblyFile.absolutePath}")
// --- End bootstrap ---

android {
    namespace = "com.termux"
    compileSdk = v.getProperty("compileSdk").toInt()
    ndkVersion = v.getProperty("ndkVersion")

    defaultConfig {
        minSdk = v.getProperty("minSdk").toInt()

        buildConfigField("String", "TERMUX_PACKAGE_VARIANT", "\"apt-android-7\"")
        manifestPlaceholders["TERMUX_PACKAGE_NAME"] = "com.neo.ide"

        ndk {
            abiFilters += listOf("arm64-v8a", "armeabi-v7a")
        }

        externalNativeBuild {
            ndkBuild {
                cFlags("-std=c11", "-Wall", "-Wextra", "-Os", "-fno-stack-protector")
            }
        }
    }

    externalNativeBuild {
        ndkBuild {
            path = file("src/main/cpp/Android.mk")
        }
    }

    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
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

fun downloadFile(url: String, dest: File) {
    val conn = URL(url).openConnection() as HttpURLConnection
    conn.connectTimeout = 60_000
    conn.readTimeout = 120_000
    conn.setRequestProperty("User-Agent", "CODE-IDE-Builder/1.0")
    conn.instanceFollowRedirects = true
    conn.inputStream.use { input ->
        dest.outputStream().use { output ->
            input.copyTo(output, bufferSize = 8192)
        }
    }
}

fun sha256(file: File): String {
    val digest = MessageDigest.getInstance("SHA-256")
    file.inputStream().use { input ->
        val buffer = ByteArray(8192)
        var read: Int
        while (input.read(buffer).also { read = it } != -1) {
            digest.update(buffer, 0, read)
        }
    }
    return digest.digest().joinToString("") { "%02x".format(it) }
}
