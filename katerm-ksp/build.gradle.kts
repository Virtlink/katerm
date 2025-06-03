plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
//    alias(libs.plugins.google.devtools.ksp)
}

dependencies {
    // KSP
    implementation      (libs.ksp.api)

    // Testing
    testImplementation  (libs.kotest)
    testImplementation  (libs.kotest.datatest)
    testImplementation  (libs.kotest.property)
}

buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin", version = "2.1.21"))
    }
}