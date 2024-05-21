pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://api.xposed.info")
    }
}
rootProject.name = "No Internal Problem"

include(":app")