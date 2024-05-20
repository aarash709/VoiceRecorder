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
include(":feature:settings")
include(":feature:playlist")

include(":core:designsystem")
include(":core:common")
include(":core:service")
include(":core:datastore")
