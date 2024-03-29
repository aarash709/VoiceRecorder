dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
pluginManagement{
    repositories{
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
rootProject.name = "Voice Recorder"
include(":app")
include(":feature:record")
include(":core:designsystem")
include(":feature:playlist")
include(":core:common")
include(":core:service")
