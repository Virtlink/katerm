// Workaround for IntelliJ issue where `libs` is errored: https://youtrack.jetbrains.com/issue/KTIJ-19369
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    // Collections
    implementation      (libs.kotlinx.immutablecollections)

    // Testing
    testImplementation  (libs.kotest)
    testImplementation  (libs.kotest.datatest)
    testImplementation  (libs.kotest.property)
    testImplementation  (libs.jmh)
    testImplementation  (libs.jmh.generator)
}
