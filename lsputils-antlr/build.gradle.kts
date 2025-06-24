plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    // Parsing
    implementation      (libs.antlr)
    implementation      (project(":lsputils"))

    // Testing
    testImplementation  (libs.kotest)
    testImplementation  (libs.kotest.datatest)
    testImplementation  (libs.kotest.property)
}
