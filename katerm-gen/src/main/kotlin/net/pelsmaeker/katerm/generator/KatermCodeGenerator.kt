package net.pelsmaeker.katerm.generator

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import net.pelsmaeker.katerm.generator.ast.KatermUnit
import java.nio.file.Path

class KatermCodeGenerator(
    private val outputDir: Path,
    private val classPrefix: String,
) {

    fun generateSort(
        ast: KatermUnit,
        sortName: String,
    ) {
        val rules = ast.rules.filter { it.sort == sortName }

        val sortClassName = "${classPrefix}${sortName}"

        val file = FileSpec.builder("net.pelsmaeker.joelang.ast", sortClassName).apply {
            addType(TypeSpec.interfaceBuilder(sortClassName).apply {
                modifiers.add(KModifier.PUBLIC)
                modifiers.add(KModifier.SEALED)
            }.build())
        }.build()

        file.writeTo(outputDir)
    }

}