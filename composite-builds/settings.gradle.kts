pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "build-deps"

include(":java-compiler")
include(":jdk-compiler")
include(":jaxp")
include(":layoutlib-api")
