package net.pelsmaeker.katerm.regex

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import net.pelsmaeker.katerm.substitutions.emptySubstitution
import net.pelsmaeker.katerm.testRegexBuilder

class ConcatTests : FunSpec({

    context("deriveAndUnify()") {
        test("should derive and unify with a matching term for both patterns") {
            with(testRegexBuilder) {
                // Arrange
                val firstPattern = T(string("Foo"))
                val secondPattern = T(string("Bar"))
                val regex = concat(firstPattern, secondPattern)
                val terms = listOf(string("Foo"), string("Bar"))

                // Act
                val m0 = regex.buildMatcher(emptySubstitution())
                val m1 = m0.match(terms[0])
                val m2 = m1.match(terms[1])

                // Assert
                m2.isEmpty() shouldBe false
                val newSubstitution = m2.getAcceptingMetadata()!!
                newSubstitution.isEmpty() shouldBe true
            }
        }

        test("should not derive and unify with a non-matching first term") {
            with(testRegexBuilder) {
                // Arrange
                val firstPattern = T(string("Foo"))
                val secondPattern = T(string("Bar"))
                val regex = concat(firstPattern, secondPattern)
                val terms = listOf(string("Qux"), string("Bar"))

                // Act
                val m0 = regex.buildMatcher(emptySubstitution())
                val m1 = m0.match(terms[0])

                // Assert
                m1.isEmpty() shouldBe true
            }
        }

        test("should not derive and unify with a non-matching second term") {
            with(testRegexBuilder) {
                // Arrange
                val firstPattern = T(string("Foo"))
                val secondPattern = T(string("Bar"))
                val regex = concat(firstPattern, secondPattern)
                val matcher = regex.buildMatcher(emptySubstitution())
                val terms = listOf(string("Foo"), string("Qux"))

                // Act
                val m0 = regex.buildMatcher(emptySubstitution())
                val m1 = m0.match(terms[0])
                val m2 = m1.match(terms[1])

                // Assert
                m2.isEmpty() shouldBe true
            }
        }

        test("should handle nullable first pattern correctly") {
            with(testRegexBuilder) {
                // TODO
            }
        }
    }

//    context("termVars") {
//        test("should return the union of termVars from both patterns") {
//            with(testTermBuilder) {
//                // Arrange
//                val firstPattern = T(!"A")
//                val secondPattern = T(!"B")
//                val regex = concat(firstPattern, secondPattern)
//
//                // Act
//                val termVars = regex.termVars
//
//                // Assert
//                termVars shouldBe (firstPattern.termVars + secondPattern.termVars)
//            }
//        }
//    }

})