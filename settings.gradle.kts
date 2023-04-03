pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "parserlib"
include(":core")
include(":examples")
includeBuild("convention-plugins")
include(":demo-jre")
