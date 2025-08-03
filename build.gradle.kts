import io.gitlab.arturbosch.detekt.Detekt

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.kotlin.compose.compiler) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.buildkonfig) apply false
}

allprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "io.gitlab.arturbosch.detekt").also {
        configureDetektTasks(tasks)
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
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
            val buildDirPath =
                project.layout.buildDirectory.asFile
                    .get()
                    .toPath()
            val isInBuildDir = it.file.toPath().startsWith(buildDirPath)
            return@exclude isInBuildDir
        }
    }
    tasks.register("detektAll") {
        dependsOn(tasks.withType<Detekt>())
    }
}
