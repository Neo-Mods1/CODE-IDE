/**
 *	(っ◔◡◔)っ ♥
 *
 *	Telegram Contact • @NeoModsDev
 *	Telegram Channel • https://t.me/NeoModsChannel
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
            projectDir.mkdirs()

            generateRootBuildGradle()
            generateSettingsGradle()
            generateGradleProperties()
            generateGradleWrapper()
            generateGitignore()

            if (template.hasActivity) {
                generateAppBuildGradle()
                generateMainActivity()
                generateLayouts()
                generateManifest()
            } else {
                generateLibraryBuildGradle()
                generateManifest()
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

    private fun generateAppBuildGradle() {
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

        File(projectDir, "app/build.gradle.$ext").writeText(content)
    }

    private fun generateLibraryBuildGradle() {
        val kotlinPlugin = if (isKotlin) """    id("kotlin-android")""" else ""

        val content = """
plugins {
    id("com.android.library")$kotlinPlugin
}

android {
    namespace = "$packageName"
    compileSdk = $compileSdk

    defaultConfig {
        minSdk = $minSdk

        consumerProguardFiles("consumer-rules.pro")
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
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
}
""".trimIndent()

        File(projectDir, "app/build.gradle.$ext").writeText(content)
    }

    private fun generateMainActivity() {
        val appDir = File(projectDir, "app/src/main/java/$packagePath")
        appDir.mkdirs()

        val actClassName = className()

        when (template.activityType) {
            ProjectTemplate.ActivityType.COMPOSE -> generateComposeActivity(appDir, actClassName)
            ProjectTemplate.ActivityType.EMPTY -> generateSimpleActivity(appDir, actClassName)
            ProjectTemplate.ActivityType.BASIC -> generateBasicActivity(appDir, actClassName)
            ProjectTemplate.ActivityType.NAV_DRAWER -> generateNavDrawerActivity(appDir, actClassName)
            ProjectTemplate.ActivityType.BOTTOM_NAV -> generateBottomNavActivity(appDir, actClassName)
            ProjectTemplate.ActivityType.TABBED -> generateTabbedActivity(appDir, actClassName)
            ProjectTemplate.ActivityType.NONE -> { /* No activity */ }
        }
    }

    private fun generateSimpleActivity(dir: File, className: String) {
        val lang = if (isKotlin) "kotlin" else "java"
        val importViewBinding = if (!isCompose) "\nimport $packageName.databinding.ActivityMainBinding" else ""
        val extendAppCompatActivity = if (isCompose) "ComponentActivity()" else "AppCompatActivity()"

        if (isKotlin) {
            File(dir, "$className.kt").writeText("""
package $packageName

import android.os.Bundle
$importViewBinding
import androidx.activity.ComponentActivity

class $className : $extendAppCompatActivity() {
    ${if (!isCompose) "private lateinit var binding: ActivityMainBinding" else ""}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ${if (!isCompose) """
binding = ActivityMainBinding.inflate(layoutInflater)
setContentView(binding.root)""" else """setContentView(R.layout.activity_main)"""}
    }
}
""".trimIndent())
        } else {
            File(dir, "$className.java").writeText("""
package $packageName;

import android.os.Bundle;
$importViewBinding
import androidx.appcompat.app.AppCompatActivity;

public class $className extends AppCompatActivity {
    ${if (!isCompose) "private ActivityMainBinding binding;" else ""}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ${if (!isCompose) """
binding = ActivityMainBinding.inflate(getLayoutInflater());
setContentView(binding.getRoot());""" else """setContentView(R.layout.activity_main);"""}
    }
}
""".trimIndent())
        }
    }

    private fun generateBasicActivity(dir: File, className: String) {
        generateSimpleActivity(dir, className)
    }

    private fun generateNavDrawerActivity(dir: File, className: String) {
        generateSimpleActivity(dir, className)
    }

    private fun generateBottomNavActivity(dir: File, className: String) {
        generateSimpleActivity(dir, className)
    }

    private fun generateTabbedActivity(dir: File, className: String) {
        generateSimpleActivity(dir, className)
    }

    private fun generateComposeActivity(dir: File, className: String) {
        val content = "package $packageName\n" +
            "\n" +
            "import android.os.Bundle\n" +
            "import androidx.activity.ComponentActivity\n" +
            "import androidx.activity.compose.setContent\n" +
            "import androidx.compose.foundation.layout.fillMaxSize\n" +
            "import androidx.compose.material3.MaterialTheme\n" +
            "import androidx.compose.material3.Surface\n" +
            "import androidx.compose.material3.Text\n" +
            "import androidx.compose.runtime.Composable\n" +
            "import androidx.compose.ui.Modifier\n" +
            "import androidx.compose.ui.tooling.preview.Preview\n" +
            "\n" +
            "class $className : ComponentActivity() {\n" +
            "    override fun onCreate(savedInstanceState: Bundle?) {\n" +
            "        super.onCreate(savedInstanceState)\n" +
            "        setContent {\n" +
            "            MaterialTheme {\n" +
            "                Surface(\n" +
            "                    modifier = Modifier.fillMaxSize(),\n" +
            "                    color = MaterialTheme.colorScheme.background\n" +
            "                ) {\n" +
            "                    Greeting(\"Android\")\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "@Composable\n" +
            "fun Greeting(name: String, modifier: Modifier = Modifier) {\n" +
            "    Text(\n" +
            "        text = \"Hello \"+\"\$\"+\"name!\",\n" +
            "        modifier = modifier\n" +
            "    )\n" +
            "}\n" +
            "\n" +
            "@Preview(showBackground = true)\n" +
            "@Composable\n" +
            "fun GreetingPreview() {\n" +
            "    MaterialTheme {\n" +
            "        Greeting(\"Android\")\n" +
            "    }\n" +
            "}\n"
        File(dir, "$className.kt").writeText(content)
    }

    private fun generateLayouts() {
        val resDir = File(projectDir, "app/src/main/res/layout")
        resDir.mkdirs()

        val layoutName = when (template.activityType) {
            ProjectTemplate.ActivityType.COMPOSE -> return
            ProjectTemplate.ActivityType.NAV_DRAWER -> "activity_main.xml"
            ProjectTemplate.ActivityType.BOTTOM_NAV -> "activity_main.xml"
            ProjectTemplate.ActivityType.TABBED -> "activity_main.xml"
            else -> "activity_main.xml"
        }

        File(resDir, layoutName).writeText("""<?xml version="1.0" encoding="utf-8"?>
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

    private fun generateManifest() {
        val appDir = File(projectDir, "app/src/main")
        appDir.mkdirs()

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

        File(appDir, "AndroidManifest.xml").writeText(content)
    }

    private fun getAgpVersion(): String = "8.2.0"
    private fun getKotlinVersion(): String = "1.9.21"
    private fun getGradleVersion(): String = "8.2"
}
