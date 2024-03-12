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
        maven {
            setUrl("https://jitpack.io")
        }
    }
}
rootProject.name = "MultiModularArch-JC"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include (":app")
include (":core:ui")
include (":core:util")
include (":data:mongo")
include (":feature:auth")
include (":feature:home")
include (":feature:note")
