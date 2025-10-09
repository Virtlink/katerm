plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    // KSP
    implementation      (project(":katerm"))
    implementation      (libs.ksp.api)

    // Testing
    testImplementation  (libs.kotest)
    testImplementation  (libs.kotest.property)
}

buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin", version = "2.1.21"))
    }
}