import org.gradle.plugins.ide.idea.model.IdeaModel

plugins {
    java
    application
    antlr
    idea
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    // Kotlin generation
    implementation      (libs.kotlinpoet)

    // CLI
    implementation      (libs.clikt)

    // Parsing
    antlr               (libs.antlr)
    implementation      (libs.lsp4k)
    implementation      (libs.lsp4k.antlr)

    // Testing
    testImplementation  (libs.kotest)
    testImplementation  (libs.kotest.datatest)
    testImplementation  (libs.kotest.property)
}

tasks.named("compileKotlin") {
    dependsOn("generateGrammarSource")
}

tasks.named("compileTestKotlin") {
    dependsOn("generateTestGrammarSource")
}

tasks.named<AntlrTask>("generateGrammarSource").configure {
    val pkg = "net.pelsmaeker.katerm.generator"
    maxHeapSize = "64m"
    arguments = arguments + listOf(
        "-visitor",
        "-long-messages",
        "-package", pkg,
    )
}

// Workaround to avoid duplicate generated ANTLR files and implicit task dependencies
// See: https://github.com/gradle/gradle/issues/19555#issuecomment-1593252653
// Fixes: "Task ':sourcesJar' uses this output of task ':generateGrammarSource' without declaring an explicit or implicit dependency."
// Fixes: "Entry is a duplicate but no duplicate handling strategy has been set"
sourceSets.configureEach {
    val generateGrammarSource = tasks.named(getTaskName("generate", "GrammarSource"))
    java.srcDir(generateGrammarSource.map { files() })
}

// To avoid the error "cannot find tokens file" in IntelliJ, set:
// - IntelliJ settings:
//   - Languages & Frameworks:
//     - ANTLR v4 default project settings:
//       - Output directory where all the output is generated:
//         / ..absolute path.. /katerm/katerm-gen/build/generated-src/antlr/main
plugins.withId("idea") {
    configure<IdeaModel> {
        afterEvaluate {
            module.sourceDirs.add(file("src/main/antlr"))
        }
    }
}

kotlin {
    compilerOptions.freeCompilerArgs.add("-Xcontext-parameters")
}

application {
    mainClass = "net.pelsmaeker.katerm.generator.CLIKt"
}