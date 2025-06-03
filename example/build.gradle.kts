plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.google.devtools.ksp)
}

dependencies {
    implementation      (project(":katerm"))
    implementation      (project(":katerm-ksp"))
    ksp                 (project(":katerm-ksp"))

    // Testing
    testImplementation  (libs.kotest)
    testImplementation  (libs.kotest.datatest)
    testImplementation  (libs.kotest.property)
}
