import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val appNamespace = "dev.vladleesi.braindanceapp"

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.android.application)
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
        commonMain.dependencies {
            // Compose
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.components.resources)
            // Navigation
            implementation(libs.compose.navigation3)
            // View Model
            implementation(libs.compose.viewmodel)
            implementation(libs.compose.lifecycle)
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
            // Koin
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel.navigation)
            // Tests
            implementation(libs.unit.tests.junit)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        androidMain.dependencies {
            // Compose
            implementation(libs.androidx.activity.compose)
            implementation(libs.compose.ui)
            implementation(libs.compose.ui.tooling)
            implementation(libs.compose.ui.tooling.preview)
            // Network
            implementation(libs.ktor.client.okhttp)
            // Security
            implementation(libs.androidx.security.crypto.ktx)
            // Koin
            implementation(libs.koin.android)
            // Tests
            implementation(libs.androidx.tests.runner)
            implementation(libs.androidx.tests.ext.junit)
        }
        val androidUnitTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        iosMain.dependencies {
            // Network
            implementation(libs.ktor.client.darwin)
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "$appNamespace.resources"
    generateResClass = always
}

android {
    namespace = appNamespace
    compileSdk = 36
    defaultConfig {
        applicationId = appNamespace
        targetSdk = 36
        minSdk = 24
        versionCode = 1
        versionName = "0.2.0"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        debug {
            versionNameSuffix = "-debug"
        }
        release {
            isMinifyEnabled = false
            versionNameSuffix = "-release"
        }
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
    packageName = appNamespace

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
