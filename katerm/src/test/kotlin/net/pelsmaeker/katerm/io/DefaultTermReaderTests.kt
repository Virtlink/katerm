package net.pelsmaeker.katerm.io

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import net.pelsmaeker.katerm.terms.SimpleTermBuilder
import net.pelsmaeker.katerm.annotations.TermAnnotationKey
import net.pelsmaeker.katerm.attachments.TermAttachments
import net.pelsmaeker.katerm.terms.Term

/**
 * Tests the [DefaultTermReader] class.
 */
class DefaultTermReaderTests: FunSpec({

    val builder = SimpleTermBuilder()

    /** The test data for the tests. */
    val termTests: List<Pair<String, Term>> = builder.run {
        listOf(
            "()" to newAppl(""),
            "(10)" to newAppl("", newInt(10)),
            "(\"a\", 10)" to newAppl("", newString("a"), newInt(10)),

            "MyCons()" to newAppl("MyCons"),
            "MyCons(10)" to newAppl("MyCons", newInt(10)),
            "MyCons(\"a\", 10)" to newAppl("MyCons", newString("a"), newInt(10)),

            "[]" to newEmptyList(),
            "[10]" to newListOf(listOf(newInt(10))),
            "[\"a\", 10]" to newListOf(listOf(newString("a"), newInt(10))),

            "\"\"" to newString(""),
            "\"abc\"" to newString("abc"),

            "0" to newInt(0),
            "42" to newInt(42),

            ".0" to newReal(.0),
            "0.0" to newReal(0.0),
            "42.1" to newReal(42.1),
            "42e2" to newReal(42e2),
            ".1e2" to newReal(.1e2),
            "42.1e2" to newReal(42.1e2),
        )
    }

    context("should parse terms correctly") { builder.run {
        withData<Pair<String, Term>>(
            nameFn = { "should parse \"${it.first}\" to \"${it.second}\"" },
            termTests,
        ) { (input, expected) ->
            // Arrange
            val builder = SimpleTermBuilder()
            val reader = DefaultTermReader(builder)

            // Act
            val actual = reader.readFromString(input)

            // Assert
            actual shouldBe expected
        }
    } }

    context("should parse empty annotations correctly") { builder.run {
        withData<Pair<String, Term>>(
            nameFn = { "should parse \"${it.first}\" to \"${it.second}\"" },
            termTests.map { (input, expected) ->
                "$input{}" to expected
            },
        ) { (input, expected) ->
            // Arrange
            val builder = SimpleTermBuilder()
            val reader = DefaultTermReader(builder)

            // Act
            val actual = reader.readFromString(input)

            // Assert
            actual shouldBe expected
        }
    } }

    context("should parse one annotation correctly") { builder.run {
        withData<Pair<String, Term>>(
            nameFn = { "should parse \"${it.first}\" to \"${it.second}\"" },
            termTests.map { (input, expected) ->
                "$input{MyAnno(42)}" to withAttachments(expected, TermAttachments.of(
                    TermAnnotationKey to listOf(
                    newAppl("MyAnno", newInt(42)))
                ))
            },
        ) { (input, expected) ->
            // Arrange
            val builder = SimpleTermBuilder()
            val reader = DefaultTermReader(builder)

            // Act
            val actual = reader.readFromString(input)

            // Assert
            actual shouldBe expected
        }
    } }

    context("should parse multiple annotations correctly") { builder.run {
        withData<Pair<String, Term>>(
            nameFn = { "should parse \"${it.first}\" to \"${it.second}\"" },
            termTests.map { (input, expected) ->
                "$input{.4,MyAnno(42),\"xyz\"}" to withAttachments(expected, TermAttachments.of(
                    TermAnnotationKey to listOf(
                    newReal(.4),
                    newAppl("MyAnno", newInt(42)),
                    newString("xyz"))
                ))
            },
        ) { (input, expected) ->
            // Arrange
            val builder = SimpleTermBuilder()
            val reader = DefaultTermReader(builder)

            // Act
            val actual = reader.readFromString(input)

            // Assert
            actual shouldBe expected
        }
    } }

    test("should parse example1 from resources correctly") {
        // Arrange
        val builder = SimpleTermBuilder()
        val reader = DefaultTermReader(builder)

        // Act
        val term = reader.readFromResource(DefaultTermReaderTests::class, "/net/pelsmaeker/katerm/io/example1.aterm")

        // Assert
        term shouldBe builder.run {
            newAppl(
                "Module", newAppl("ModuleDecl", newString("example1")), newListOf(listOf(
                    newAppl(
                        "StrategyDecl", newString("repeat"), newListOf(listOf(
                            newAppl("TypeParamDef", newString("a"))
                        )), newAppl(
                            "Strategy", newListOf(listOf(
                                newAppl(
                                    "StrategyNoArgs",
                                    newAppl("TypeName", newString("a")),
                                    newAppl("TypeName", newString("a"))
                                )
                            )), newAppl("TypeName", newString("a")), newAppl("TypeName", newString("a"))
                        )
                    ),
                    newAppl(
                        "StrategyDef", newString("repeat"), newListOf(listOf(newAppl("ParamDef", newString("s")))),
                        newAppl(
                            "Call", newString("try"), newListOf(listOf(
                                newAppl(
                                    "Seq",
                                    newAppl("Var", newString("s")),
                                    newAppl("Call", newString("repeat"), newListOf(listOf(newAppl("Var", newString("s")))))
                                )
                            ))
                        )
                    ), newAppl(
                        "StrategyDecl", newString("try"), newListOf(listOf(newAppl("TypeParamDef", newString("a")))),
                        newAppl(
                            "Strategy", newListOf(listOf(
                                newAppl(
                                    "StrategyNoArgs", newAppl("TypeName", newString("a")),
                                    newAppl("TypeName", newString("a"))
                                )
                            )), newAppl("TypeName", newString("a")),
                            newAppl("TypeName", newString("a"))
                        )
                    ), newAppl(
                        "StrategyDef", newString("try"), newListOf(listOf(
                            newAppl("ParamDef", newString("s"))
                        )), newAppl(
                            "Call", newString("glc"), newListOf(listOf(
                                newAppl("Var", newString("s")), newAppl("Id"), newAppl("Id")
                            ))
                        )
                    )
                ))
            )
        }
    }

})