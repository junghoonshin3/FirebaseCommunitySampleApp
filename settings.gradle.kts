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
    }
}

rootProject.name = "PickUp"
include(":app")
include(":domain:list")
include(":domain:setting")
include(":domain:chat")
include(":feature:chat")
include(":feature:list")
include(":feature:setting")
include(":data:repository")