plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    // Testing
    testImplementation  (libs.kotest)
    testImplementation  (libs.kotest.datatest)
    testImplementation  (libs.kotest.property)
}
