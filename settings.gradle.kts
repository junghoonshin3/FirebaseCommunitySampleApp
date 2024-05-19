import java.net.URI


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
        maven { url = URI("https://devrepo.kakao.com/nexus/content/groups/public/") }
    }
}

rootProject.name = "PickUp"
include(":app")
include(":presentation")
include(":domain")
include(":data")
include(":model")
