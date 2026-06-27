plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.neo.ide.lsp"
    compileSdk = 34

    defaultConfig {
        minSdk = 30
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
    implementation("org.eclipse.lsp4j:org.eclipse.lsp4j:0.21.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.greenrobot:eventbus:3.3.1")
    implementation("org.slf4j:slf4j-api:1.7.36")
    implementation("com.squareup:javapoet:1.13.0")
    implementation("com.itsaky.androidide.treesitter:android-tree-sitter:1.0.1")
    implementation("com.itsaky.androidide.treesitter:tree-sitter-java:1.0.1")
    implementation("com.itsaky.androidide.treesitter:tree-sitter-xml:1.0.1")
    
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.annotation:annotation:1.7.1")
}
