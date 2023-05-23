plugins {
    val kotlinVersion: String by System.getProperties()
    //trick: for the same plugin versions in all sub-modules
    id("com.android.application").version("8.0.0").apply(false)
    id("com.android.library").version("8.0.0").apply(false)
    kotlin("android").version(kotlinVersion).apply(false)
    kotlin("multiplatform").version(kotlinVersion).apply(false)
    kotlin("plugin.serialization") version kotlinVersion
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
