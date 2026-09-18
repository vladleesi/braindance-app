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
    alias(libs.plugins.android.kotlin.multiplatform.library)
}

kotlin {
    android {
        namespace = "$appNamespace.shared"
        compileSdk = 37
        minSdk = 24
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
        androidResources {
            enable = true
        }
        withHostTest {
            isIncludeAndroidResources = true
        }
    }

    listOf(
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
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.compose.components.resources)
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
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        androidMain.dependencies {
            // Network
            implementation(libs.ktor.client.okhttp)
            // Security
            implementation(libs.androidx.security.crypto.ktx)
            // Koin
            implementation(libs.koin.android)
        }
        getByName("androidHostTest") {
            dependencies {
                implementation(libs.unit.tests.junit)
            }
        }
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        iosMain.dependencies {
            // Network
            implementation(libs.ktor.client.darwin)
        }
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "$appNamespace.resources"
    generateResClass = always
}

buildkonfig {
    packageName = appNamespace

    defaultConfigs {
        val buildWithoutApiKey = project.property("buildWithoutApiKey").toString().toBoolean()
        val localProperties = if (buildWithoutApiKey) null else gradleLocalProperties(rootDir, project.providers)
        val clientId: String? = localProperties?.getProperty("CLIENT_ID")
        val clientSecret: String? = localProperties?.getProperty("CLIENT_SECRET")

        require((!clientId.isNullOrEmpty() && !clientSecret.isNullOrEmpty()) || buildWithoutApiKey) {
            "Please add CLIENT_ID and CLIENT_SECRET to local.properties."
        }

        buildConfigField(FieldSpec.Type.STRING, "CLIENT_ID", clientId.orEmpty())
        buildConfigField(FieldSpec.Type.STRING, "CLIENT_SECRET", clientSecret.orEmpty())
    }
}
