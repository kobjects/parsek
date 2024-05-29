pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "parsek"
include(":core")
include(":examples")
includeBuild("convention-plugins")
include(":demo-jre")
