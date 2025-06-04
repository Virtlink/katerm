package net.pelsmaeker.katerm.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration

class ApplTermProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        // Find symbols annotated with @ApplTerm
        val symbols = resolver.getSymbolsWithAnnotation(GenerateApplTerm::class.qualifiedName!!)

        symbols.filterIsInstance<KSClassDeclaration>().forEach { classDeclaration ->
            // Generate code for each annotated class
            generateApplTermImpl(classDeclaration)
        }

        return emptyList()
    }

    private fun generateApplTermImpl(classDeclaration: KSClassDeclaration) {
        val fileName = "${classDeclaration.simpleName.asString()}Generated"
        val packageName = classDeclaration.packageName.asString()

        // Use the CodeGenerator to create a new Kotlin file
        val file = codeGenerator.createNewFile(
            Dependencies(false),
            packageName,
            fileName
        )

        file.writer().use { writer ->
            writer.write("package $packageName\n\n")
            writer.write("class $fileName {\n")
            writer.write("    override fun toString(): String {\n")
            writer.write("        return \"Generated toString for ${classDeclaration.simpleName.asString()}\"\n")
            writer.write("    }\n")
            writer.write("}\n")
        }
    }

}