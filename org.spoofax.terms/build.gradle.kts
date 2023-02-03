// Workaround for IntelliJ issue where `libs` is errored: https://youtrack.jetbrains.com/issue/KTIJ-19369
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    // Terms
    implementation      (project(":katerm"))

    // Annotations
    implementation      (libs.jsr305)

    // Testing
    testImplementation  (libs.kotest)
}

// Silence JavaDoc
tasks.withType<Javadoc>{
    options{
        this as CoreJavadocOptions
        addStringOption("Xdoclint:none","-quiet")
        encoding = "UTF-8"
        quiet()
        charset("UTF-8")
    }
}