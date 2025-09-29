package net.pelsmaeker.katerm.regex

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.ArbitraryBuilderContext
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.alphanumeric
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.codepoints
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll


val regexArb: Arb<RegexAst> = arbitrary { rs: RandomSource ->
    suspend fun ArbitraryBuilderContext.simpleTerm(): RegexAst {
        val str = Arb.string(minSize = 1, maxSize = 6, codepoints = Codepoint.alphanumeric()).bind()
        val matcher = EqualityMatcher<String, Unit>(str)
        return RegexAst.Builder.atom(matcher)
    }
    suspend fun ArbitraryBuilderContext.recursive(depth: Int): RegexAst {
        if (depth <= 0) return simpleTerm()
        return when (rs.random.nextInt(0, 7)) {
            0 -> RegexAst.Builder.concat(recursive(depth - 1), recursive(depth - 1))
            1 -> RegexAst.Builder.union(recursive(depth - 1), recursive(depth - 1))
            2 -> RegexAst.Builder.star(recursive(depth - 1))
            3 -> RegexAst.Builder.plus(recursive(depth - 1))
            4 -> RegexAst.Builder.question(recursive(depth - 1))
            5 -> RegexAst.Builder.starLazy(recursive(depth - 1))
            6 -> RegexAst.Builder.plusLazy(recursive(depth - 1))
            7 -> RegexAst.Builder.questionLazy(recursive(depth - 1))
            else -> simpleTerm()
        }
    }
    recursive(rs.random.nextInt(1, 8))
}

class RegexParserTests: FunSpec({

    test("should parse simple regex") {
        // Arrange
        val regex = with(RegexAst.Builder) {
            "a"().."b"().."c"()
        }
        val regexStr = regex.toString()
        val parser = RegexParser<RegexAst, String, Unit>(
            builder = RegexAst.Builder,
            tokenMatcherBuilder = { token, _ -> EqualityMatcher(token) },
        )

        // Act
        val parsedRegex = parser.parse(regexStr)

        // Assert
        withClue("Parsed regex: $regexStr") {
            parsedRegex shouldBe regex
        }
    }


    test("property should succeed") {
        checkAll(regexArb) { regex ->
            // Arrange
            val regexStr = regex.toString()
            val parser = RegexParser<RegexAst, String, Unit>(
                builder = RegexAst.Builder,
                tokenMatcherBuilder = { token, _ -> EqualityMatcher(token) },
            )

            // Act
            val parsedRegex = parser.parse(regexStr)

            // Assert
            withClue("Parsed regex: $regexStr") {
                parsedRegex shouldBe regex
            }
        }
    }

})