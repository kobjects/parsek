import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.SourcesJar
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.vanniktech.maven.publish") version "0.37.0"
    id("org.jetbrains.dokka") version "2.3.0"
}

// CocoaPods requires the podspec to have a version.
group = "org.kobjects.parsek"
version = project.properties["org.kobjects.parsek.version"].toString()

dokka {
    moduleName.set("Parsek")
    dokkaPublications.html {
        outputDirectory.set(layout.buildDirectory.dir("dokka"))
    }
    dokkaSourceSets.configureEach {
        includes.from("module.md")
    }
}

mavenPublishing {
    publishToMavenCentral(automaticRelease = true)
    signAllPublications()

    configure(
        KotlinMultiplatform(
            javadocJar = JavadocJar.Empty(),
            sourcesJar = SourcesJar.Sources(),
        )
    )

    coordinates(group.toString(), "core", version.toString())

    pom {
        name.set("parsek")
        description.set("Parser library for Kotlin")
        inceptionYear.set("2022")
        url.set("https://github.com/kobjects/parsek")

        licenses {
            license {
                name.set("Apache-2.0")
                url.set("https://opensource.org/licenses/Apache-2.0")
            }
        }
        developers {
            developer {
                id.set("stefanhaustein")
                name.set("Stefan Haustein")
                email.set("stefan.haustein@gmail.com")
            }
        }
        scm {
            url.set("https://github.com/kobjects/parsek")
            connection.set("scm:git:git://github.com/kobjects/parsek.git")
            developerConnection.set("scm:git:ssh://git@github.com/kobjects/parsek.git")
        }
    }
}

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
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    cocoapods {
        summary = "Kotlin Parser Library"
        homepage = "https://github.com/kobjects/parsek"
        ios.deploymentTarget = "14.1"
        //podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "core"
        }
    }

    sourceSets {
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
