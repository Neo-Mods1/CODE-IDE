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
include(":app")
include(":editor")
include(":lsp")
include(":tooling")
include(":projects")
include(":templates")
