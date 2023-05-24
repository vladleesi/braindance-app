val composeVersion: String by project
val coroutinesVersion: String by project

plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "io.github.vladleesi.braindanceapp.android"
    compileSdk = 33
    defaultConfig {
        applicationId = "io.github.vladleesi.braindanceapp.android"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.7"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(":shared"))

    // Compose
    implementation("androidx.activity:activity-compose:1.7.1")
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    implementation("androidx.compose.foundation:foundation:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")

    // LiveData
    implementation("androidx.compose.runtime:runtime-livedata:$composeVersion")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.5.3")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")

    // Testing
    testImplementation("junit:junit:4.13.2")
}
