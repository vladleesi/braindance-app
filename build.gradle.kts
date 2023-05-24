import io.gitlab.arturbosch.detekt.Detekt

plugins {
    val kotlinVersion: String by System.getProperties()
    // trick: for the same plugin versions in all sub-modules
    id("com.android.application").version("8.0.0").apply(false)
    id("com.android.library").version("8.0.0").apply(false)
    kotlin("android").version(kotlinVersion).apply(false)
    kotlin("multiplatform").version(kotlinVersion).apply(false)
    kotlin("plugin.serialization") version kotlinVersion
    id("org.jlleitschuh.gradle.ktlint") version "11.3.2"
    id("io.gitlab.arturbosch.detekt") version "1.21.0"
}

allprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "io.gitlab.arturbosch.detekt").also {
        configureDetektTasks(tasks)
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

fun configureDetektTasks(tasks: NamedDomainObjectContainer<Task>) {
    tasks.withType<Detekt>().configureEach {
        config.setFrom(file("config/detekt-config.yml"))
        parallel = true
        autoCorrect = true
        reports {
            xml.required.set(false)
            html.required.set(false)
            txt.required.set(false)
            sarif.required.set(false)
        }
    }
    tasks.withType<Detekt> {
        setSource(files(project.projectDir))
        exclude("**/build/**")
        exclude {
            it.file.relativeTo(projectDir).startsWith(project.buildDir.relativeTo(projectDir))
        }
    }
    tasks.register("detektAll") {
        dependsOn(tasks.withType<Detekt>())
    }
}
