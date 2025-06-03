plugins {
    `java-library`
    `java-test-fixtures`
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    // Collections
    implementation      (libs.kotlinx.immutablecollections)

    // Testing
//    testImplementation  (libs.kotest)
//    testImplementation  (libs.kotest.datatest)
//    testImplementation  (libs.kotest.property)
    testImplementation  (libs.jmh)
    testImplementation  (libs.jmh.generator)

    testFixturesApi     (libs.kotest)
    testFixturesApi     (libs.kotest.datatest)
    testFixturesApi     (libs.kotest.property)
}
