import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
}

group = "org.kobjects.parsek"
version = project.properties["org.kobjects.parsek.version"].toString()

kotlin {
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
    }
    js {
        browser()
    }
    cocoapods {
        summary = "Kotlin Parsing Library Examples"
        homepage = "https://github.com/kobjects/parsek"
        ios.deploymentTarget = "14.1"
        framework {
            baseName = "examples"
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core"))
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
