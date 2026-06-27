pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/") }
        maven { url = uri("https://s01.oss.sonatype.org/content/groups/public/") }
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "CODE-IDE"

includeBuild("composite-builds") {
    name.dependencySubstitution {
        substitute(module("com.itsaky.androidide.build:java-compiler")).using(project(":java-compiler"))
        substitute(module("com.itsaky.androidide.build:jdk-compiler")).using(project(":jdk-compiler"))
        substitute(module("com.itsaky.androidide.build:jaxp")).using(project(":jaxp"))
    }
}

include(":app")
include(":editor")
include(":lsp")
include(":tooling")
include(":templates")
