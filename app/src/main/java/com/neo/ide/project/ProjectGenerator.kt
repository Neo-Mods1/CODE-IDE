/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║                    CODE-IDE • NeoMods                      ║
 * ║                  Advanced Android IDE Project              ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 *  (っ◔◡◔)っ ♥
 *
 *  Developer         • NeoMods
 *  Telegram Contact  • @NeoModsDev
 *  Telegram Channel  • https://t.me/NeoModsChannel
 *
 * ──────────────────────────────────────────────────────────────
 *  PROJECT NOTICE
 * ──────────────────────────────────────────────────────────────
 *
 *  This source file is part of the CODE-IDE project.
 *
 *  Unauthorized copying, extraction, redistribution,
 *  mirroring, downloading, modification, or reuse of
 *  CODE-IDE source files is NOT permitted without
 *  explicit permission from the developer.
 *
 *  The application may expose certain components in
 *  read-only mode for educational or preview purposes,
 *  however this DOES NOT grant permission to reuse
 *  or redistribute the source code.
 *
 *  If you need access to the original source code,
 *  implementation details, licensing, or collaboration,
 *  please contact the developer directly.
 *
 *  © NeoMods — All Rights Reserved
 * ──────────────────────────────────────────────────────────────
 */

package com.neo.ide.project

import com.neo.ide.templates.ProjectTemplate
import java.io.File

class ProjectGenerator(
    private val projectName: String,
    private val packageName: String,
    private val projectDir: File,
    private val language: ProjectTemplate.TemplateLanguage,
    private val useKts: Boolean,
    private val minSdk: Int,
    private val targetSdk: Int,
    private val compileSdk: Int,
    private val template: ProjectTemplate
) {
    private val packagePath = packageName.replace('.', '/')
    private val ext = if (useKts) "kts" else "gradle"
    private val isKotlin = language == ProjectTemplate.TemplateLanguage.KOTLIN
    private val isCompose = template.activityType == ProjectTemplate.ActivityType.COMPOSE
    private val jvmTarget = "17"

    fun generate(): Result<Unit> {
        return try {
            // Create root directory
            projectDir.mkdirs()

            // Create app module directory structure
            val appDir = File(projectDir, "app")
            val appSrcDir = File(appDir, "src/main")
            val appJavaDir = File(appSrcDir, "java/$packagePath")
            val appResDir = File(appSrcDir, "res")
            val layoutDir = File(appResDir, "layout")
            val valuesDir = File(appResDir, "values")
            val drawableDir = File(appResDir, "drawable")
            val mipmapDir = File(appResDir, "mipmap-hdpi")

            listOf(appDir, appSrcDir, appJavaDir, appResDir, layoutDir, valuesDir, drawableDir, mipmapDir).forEach { it.mkdirs() }

            // Generate root project files
            generateRootBuildGradle()
            generateSettingsGradle()
            generateGradleProperties()
            generateGradleWrapper()
            generateGitignore()

            // Generate app module files
            generateAppBuildGradle(appDir)
            generateProguardRules(appDir)
            generateManifest(appSrcDir)
            generateStringsXml(valuesDir)

            if (template.hasActivity) {
                generateMainActivity(appJavaDir)
                generateLayouts(layoutDir)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun generateRootBuildGradle() {
        val kotlinPlugin = if (isKotlin) """
    id("org.jetbrains.kotlin.android") version "${getKotlinVersion()}" apply false""" else ""

        val content = """
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "${getAgpVersion()}" apply false
    id("com.android.library") version "${getAgpVersion()}" apply false$kotlinPlugin
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
""".trimIndent()

        File(projectDir, "build.gradle.$ext").writeText(content)
    }

    private fun generateSettingsGradle() {
        val content = """
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "$projectName"
include(":app")
""".trimIndent()

        File(projectDir, "settings.gradle.$ext").writeText(content)
    }

    private fun generateGradleProperties() {
        val content = """
# Project-wide Gradle settings.
# IDE (e.g. Android Studio) users:
# Gradle settings configured through the IDE *will override*
# any settings specified in this file.

# For more details on how to configure your build environment visit
# http://www.gradle.org/docs/current/userguide/build_environment.html

# Specifies the JVM arguments used for the daemon process.
# The setting is particularly useful for tweaking memory settings.
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8

# Use AndroidX
android.useAndroidX=true

# Enables namespacing of each library's R class so that its R class includes only the
# resources declared in the library itself and none from the library's dependencies,
# thereby reducing the size of the R class for that library
android.nonTransitiveRClass=true
""".trimIndent()

        File(projectDir, "gradle.properties").writeText(content)
    }

    private fun generateGradleWrapper() {
        val wrapperDir = File(projectDir, "gradle/wrapper")
        wrapperDir.mkdirs()

        File(wrapperDir, "gradle-wrapper.properties").writeText("""
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-${getGradleVersion()}-bin.zip
networkTimeout=10000
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
""".trimIndent())
    }

    private fun generateGitignore() {
        File(projectDir, ".gitignore").writeText("""
*.iml
.gradle
/local.properties
/.idea
.DS_Store
/build
/captures
.externalNativeBuild
.cxx
local.properties
""".trimIndent())
    }

    private fun generateAppBuildGradle(appDir: File) {
        val kotlinPlugin = if (isKotlin) """    id("kotlin-android")""" else ""
        val kotlinComposePlugin = if (isCompose) """    id("org.jetbrains.kotlin.plugin.compose")""" else ""

        val composeConfig = if (isCompose) """
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }""" else """
    buildFeatures {
        viewBinding = true
    }"""

        val kotlinJvmTarget = if (isKotlin) """

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "$jvmTarget"
    }
}""" else ""

        val dependencies = buildString {
            appendLine("dependencies {")
            if (isCompose) {
                appendLine("    implementation(platform(\"androidx.compose:compose-bom:2024.02.00\"))")
                appendLine("    implementation(\"androidx.compose.ui:ui\")")
                appendLine("    implementation(\"androidx.compose.ui:ui-graphics\")")
                appendLine("    implementation(\"androidx.compose.ui:ui-tooling-preview\")")
                appendLine("    implementation(\"androidx.compose.material3:material3\")")
                appendLine("    implementation(\"androidx.activity:activity-compose:1.8.2\")")
                appendLine("    implementation(\"androidx.lifecycle:lifecycle-runtime-ktx:2.7.0\")")
                appendLine("    debugImplementation(\"androidx.compose.ui:ui-tooling\")")
                appendLine("    debugImplementation(\"androidx.compose.ui:ui-test-manifest\")")
            } else {
                appendLine("    implementation(\"androidx.core:core-ktx:1.12.0\")")
                appendLine("    implementation(\"androidx.appcompat:appcompat:1.6.1\")")
                appendLine("    implementation(\"com.google.android.material:material:1.11.0\")")
                appendLine("    implementation(\"androidx.constraintlayout:constraintlayout:2.1.4\")")
            }
            appendLine("}")
        }

        val content = """
plugins {
    id("com.android.application")$kotlinPlugin$kotlinComposePlugin
}

android {
    namespace = "$packageName"
    compileSdk = $compileSdk

    defaultConfig {
        applicationId = "$packageName"
        minSdk = $minSdk
        targetSdk = $targetSdk
        versionCode = 1
        versionName = "1.0"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }$composeConfig
}$kotlinJvmTarget

$dependencies
""".trimIndent()

        File(appDir, "build.gradle.$ext").writeText(content)
    }

    private fun generateProguardRules(appDir: File) {
        File(appDir, "proguard-rules.pro").writeText("""
# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renameSourcefileattribute SourceFile
""".trimIndent())
    }

    private fun generateManifest(appSrcDir: File) {
        val activityName = if (template.hasActivity) ".${className()}" else ""
        val mainIntent = if (template.hasActivity) """
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>""" else ""

        val content = """<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.$projectName">
        <activity
            android:name="$activityName"
            android:exported="true"$mainIntent>
        </activity>
    </application>

</manifest>
"""

        File(appSrcDir, "AndroidManifest.xml").writeText(content)
    }

    private fun generateStringsXml(valuesDir: File) {
        val content = """<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">$projectName</string>
</resources>
"""
        File(valuesDir, "strings.xml").writeText(content)
    }

    private fun generateMainActivity(dir: File) {
        val actClassName = className()

        when (template.activityType) {
            ProjectTemplate.ActivityType.COMPOSE -> generateComposeActivity(dir, actClassName)
            ProjectTemplate.ActivityType.EMPTY -> generateSimpleActivity(dir, actClassName)
            ProjectTemplate.ActivityType.BASIC -> generateSimpleActivity(dir, actClassName)
            ProjectTemplate.ActivityType.NAV_DRAWER -> generateSimpleActivity(dir, actClassName)
            ProjectTemplate.ActivityType.BOTTOM_NAV -> generateSimpleActivity(dir, actClassName)
            ProjectTemplate.ActivityType.TABBED -> generateSimpleActivity(dir, actClassName)
            ProjectTemplate.ActivityType.NONE -> { /* No activity */ }
        }
    }

    private fun generateSimpleActivity(dir: File, className: String) {
        if (isKotlin) {
            File(dir, "$className.kt").writeText("""
package $packageName

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

class $className : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MaterialTheme {
        Greeting("Android")
    }
}
""".trimIndent())
        } else {
            File(dir, "$className.java").writeText("""
package $packageName;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class $className extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
""".trimIndent())
        }
    }

    private fun generateComposeActivity(dir: File, className: String) {
        File(dir, "$className.kt").writeText("""
package $packageName

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

class $className : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MaterialTheme {
        Greeting("Android")
    }
}
""".trimIndent())
    }

    private fun generateLayouts(layoutDir: File) {
        val layoutName = when (template.activityType) {
            ProjectTemplate.ActivityType.COMPOSE -> return
            ProjectTemplate.ActivityType.NAV_DRAWER -> "activity_main.xml"
            ProjectTemplate.ActivityType.BOTTOM_NAV -> "activity_main.xml"
            ProjectTemplate.ActivityType.TABBED -> "activity_main.xml"
            else -> "activity_main.xml"
        }

        File(layoutDir, layoutName).writeText("""<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".${className()}">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Hello World!"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
""")
    }

    private fun className(): String = when (template.activityType) {
        ProjectTemplate.ActivityType.COMPOSE -> "ComposeActivity"
        ProjectTemplate.ActivityType.EMPTY -> "EmptyActivity"
        ProjectTemplate.ActivityType.BASIC -> "BasicActivity"
        ProjectTemplate.ActivityType.NAV_DRAWER -> "NavDrawerActivity"
        ProjectTemplate.ActivityType.BOTTOM_NAV -> "BottomNavActivity"
        ProjectTemplate.ActivityType.TABBED -> "TabbedActivity"
        ProjectTemplate.ActivityType.NONE -> ""
    }

    private fun getAgpVersion(): String = "8.2.0"
    private fun getKotlinVersion(): String = "1.9.21"
    private fun getGradleVersion(): String = "8.2"
}
