import java.util.Properties

val v = Properties().apply {
    load(rootProject.file("versions.properties").inputStream())
}

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.neo.ide.lsp"
    compileSdk = v.getProperty("compileSdk").toInt()

    defaultConfig {
        minSdk = v.getProperty("minSdk").toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("org.eclipse.lsp4j:org.eclipse.lsp4j:${v.getProperty("lsp4jVersion")}")
    implementation("com.google.code.gson:gson:${v.getProperty("gsonVersion")}")
    implementation("org.greenrobot:eventbus:${v.getProperty("eventBusVersion")}")
    implementation("org.slf4j:slf4j-api:${v.getProperty("slf4jVersion")}")
    implementation("com.squareup:javapoet:${v.getProperty("javaPoetVersion")}")
    implementation("com.itsaky.androidide.treesitter:android-tree-sitter:${v.getProperty("treeSitterVersion")}")
    implementation("com.itsaky.androidide.treesitter:tree-sitter-java:${v.getProperty("treeSitterVersion")}")
    implementation("com.itsaky.androidide.treesitter:tree-sitter-xml:${v.getProperty("treeSitterVersion")}")

    implementation("androidx.core:core-ktx:${v.getProperty("coreKtxVersion")}")
    implementation("androidx.appcompat:appcompat:${v.getProperty("appcompatVersion")}")
    implementation("androidx.annotation:annotation:${v.getProperty("annotationVersion")}")
}
