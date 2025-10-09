plugins {
    `java-library`
    `java-test-fixtures`
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    // Collections
    implementation      (libs.kotlinx.immutablecollections)

    // Testing
    testImplementation  (libs.jmh)
    testImplementation  (libs.jmh.generator)

    testFixturesApi     (libs.kotest)
    testFixturesApi     (libs.kotest.property)
}
