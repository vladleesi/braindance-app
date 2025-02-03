import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    kotlin("plugin.serialization")
    id("com.codingfeline.buildkonfig")
}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
                }
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                // Compose
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.components.resources)
                // Navigation
                implementation(libs.compose.navigation)
                // View Model
                implementation(libs.compose.viewmodel)
                // Ktor
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.auth)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.serialization)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                // Datetime
                implementation(libs.kotlinx.datetime)
                // Serialization
                implementation(libs.kotlinx.serialization.json)
                // Coroutines
                implementation(libs.kotlinx.coroutines)
                // Coil
                implementation(libs.coil.compose)
                implementation(libs.coil.network.ktor)
                // Logger
                implementation(libs.logging.napier)
                // Security
                implementation(libs.multiplatform.settings)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                // Network
                implementation(libs.ktor.client.okhttp)
                // Security
                implementation(libs.androidx.security.crypto.ktx)
                // Tests
                implementation(libs.androidx.tests.runner)
                implementation(libs.androidx.tests.ext.junit)
            }
        }
        val androidUnitTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                // Network
                implementation(libs.ktor.client.darwin)
            }
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "dev.vladleesi.braindanceapp.resources"
    generateResClass = always
}

android {
    namespace = "dev.vladleesi.braindanceapp"
    compileSdk = 35
    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

buildkonfig {
    packageName = "dev.vladleesi.braindanceapp"

    defaultConfigs {
        val clientId: String? = gradleLocalProperties(rootDir, project.providers).getProperty("CLIENT_ID")
        val clientSecret: String? = gradleLocalProperties(rootDir, project.providers).getProperty("CLIENT_SECRET")
        val buildWithoutApiKey = project.property("buildWithoutApiKey").toString().toBoolean()

        require((!clientId.isNullOrEmpty() && !clientSecret.isNullOrEmpty()) || buildWithoutApiKey) {
            "Please add CLIENT_ID and CLIENT_SECRET to local.properties."
        }

        buildConfigField(FieldSpec.Type.STRING, "CLIENT_ID", clientId.orEmpty())
        buildConfigField(FieldSpec.Type.STRING, "CLIENT_SECRET", clientSecret.orEmpty())
    }
}
