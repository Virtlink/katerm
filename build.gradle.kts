import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.tasks.UsesKotlinJavaToolchain

plugins {
    java
    jacoco
    `maven-publish`
    signing
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.gitversion)
    alias(libs.plugins.versions)
    alias(libs.plugins.nexuspublish)        // Publish on Maven Central
}

allprojects {
    apply(plugin = "com.palantir.git-version")
    apply(plugin = "com.github.ben-manes.versions")

    val gitVersion: groovy.lang.Closure<String> by extra

    group = "net.pelsmaeker"
    version = gitVersion()
    description = "Advanced term library."

    extra["isSnapshotVersion"] = version.toString().endsWith("-SNAPSHOT")
    extra["isDirtyVersion"] = version.toString().endsWith(".dirty")
    extra["isCI"] = !System.getenv("CI").isNullOrEmpty()

    repositories {
        google()
        mavenCentral()
    }

    tasks.register<DependencyReportTask>("allDependencies") {}
    tasks.register<BuildEnvironmentReportTask>("allBuildEnvironment") {}
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")
    apply(plugin = "jacoco")
    apply(plugin = "project-report")

    tasks.test {
        useJUnitPlatform()
    }

    configure<JavaPluginExtension> {
        toolchain.languageVersion.set(JavaLanguageVersion.of(11))
        withSourcesJar()
        withJavadocJar()
    }

    tasks.withType<UsesKotlinJavaToolchain>().configureEach {
        val service = project.extensions.getByType<JavaToolchainService>()
        val customLauncher = service.launcherFor {
            languageVersion.set(JavaLanguageVersion.of("11"))
        }
        kotlinJavaToolchain.toolchain.use(customLauncher)
    }

    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
            freeCompilerArgs.add("-Xcontext-parameters")
        }
    }

    publishing {
        publications {
            create<MavenPublication>("library") {
                from(components["java"])

                pom {
                    name.set("katerm")
                    description.set(project.description)
                    url.set("https://github.com/Virtlink/katerm")
                    inceptionYear.set("2023")
                    packaging = "jar"
                    licenses {
                        // From: https://spdx.org/licenses/
                        license {
                            name.set("Apache-2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0")
                            distribution.set("repo")
                        }
                    }
                    developers {
                        developer {
                            id.set("Virtlink")
                            name.set("Daniel A. A. Pelsmaeker")
                            email.set("developer@pelsmaeker.net")
                        }
                    }
                    scm {
                        connection.set("scm:git@github.com:Virtlink/katerm.git")
                        developerConnection.set("scm:git@github.com:Virtlink/katerm.git")
                        url.set("scm:git@github.com:Virtlink/katerm.git")
                    }
                }
            }
        }
        repositories {
            maven {
                name = "GitHub"
                url = uri("https://maven.pkg.github.com/Virtlink/katerm")
                credentials {
                    username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
                    password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
                }
            }
        }
    }

    signing {
        sign(publishing.publications["library"])
        if (!project.hasProperty("signing.secretKeyRingFile")) {
            // If no secretKeyRingFile was set, we assume an in-memory key in the SIGNING_KEY environment variable (used in CI)
            useInMemoryPgpKeys(
                project.findProperty("signing.keyId") as String? ?: System.getenv("SIGNING_KEY_ID"),
                System.getenv("SIGNING_KEY"),
                project.findProperty("signing.password") as String? ?: System.getenv("SIGNING_KEY_PASSWORD"),
            )
        }
    }

    val checkNotDirty by tasks.registering {
        doLast {
            if (project.extra["isDirtyVersion"] as Boolean) {
                throw GradleException("Cannot publish a dirty version: ${project.version}")
            }
        }
    }

    tasks.publish { dependsOn(checkNotDirty) }
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            username.set(project.findProperty("ossrh.user") as String? ?: System.getenv("OSSRH_USERNAME"))
            password.set(project.findProperty("ossrh.token") as String? ?: System.getenv("OSSRH_TOKEN"))
        }
    }
}