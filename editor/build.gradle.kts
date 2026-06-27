import java.util.Properties

val v = Properties().apply {
    load(rootProject.file("versions.properties").inputStream())
}

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.neo.ide.editor"
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

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Sora Editor
    implementation("io.github.Rosemoe.sora-editor:editor:${v.getProperty("soraEditorVersion")}")
    implementation("io.github.Rosemoe.sora-editor:language-textmate:${v.getProperty("soraEditorVersion")}")

    // Tree-sitter
    implementation("com.itsaky.androidide.treesitter:android-tree-sitter:${v.getProperty("treeSitterVersion")}")
    implementation("com.itsaky.androidide.treesitter:tree-sitter-java:${v.getProperty("treeSitterVersion")}")
    implementation("com.itsaky.androidide.treesitter:tree-sitter-kotlin:${v.getProperty("treeSitterVersion")}")
    implementation("com.itsaky.androidide.treesitter:tree-sitter-json:${v.getProperty("treeSitterVersion")}")
    implementation("com.itsaky.androidide.treesitter:tree-sitter-xml:${v.getProperty("treeSitterVersion")}")
    implementation("com.itsaky.androidide.treesitter:tree-sitter-log:${v.getProperty("treeSitterVersion")}")

    // Other
    implementation("org.greenrobot:eventbus:${v.getProperty("eventBusVersion")}")
    implementation("org.slf4j:slf4j-api:${v.getProperty("slf4jVersion")}")
    implementation("com.google.code.gson:gson:${v.getProperty("gsonVersion")}")

    // AndroidX
    implementation("androidx.core:core-ktx:${v.getProperty("coreKtxVersion")}")
    implementation("androidx.appcompat:appcompat:${v.getProperty("appcompatVersion")}")
    implementation("com.google.android.material:material:${v.getProperty("materialVersion")}")
    implementation("androidx.annotation:annotation:${v.getProperty("annotationVersion")}")

    api(project(":lsp"))
}
