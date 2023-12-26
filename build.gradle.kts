import org.jmailen.gradle.kotlinter.tasks.FormatTask
import org.jmailen.gradle.kotlinter.tasks.LintTask

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.kotlinter) apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")
    apply(plugin = "org.jmailen.kotlinter")
    tasks {
        register("detektAll", io.gitlab.arturbosch.detekt.Detekt::class) {
            exclude("**/resources/**")
            exclude("**/build/**")
            config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
            buildUponDefaultConfig = false
            parallel = true
//            allRules = true
//            autoCorrect = true
        }
    }

    tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
        parallel = true
        //reports are set in config file
    }

    tasks.withType<LintTask> {
        ignoreFailures = true
        source(files("src"))
    }

    tasks.withType<FormatTask> {

    }

}

task("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}