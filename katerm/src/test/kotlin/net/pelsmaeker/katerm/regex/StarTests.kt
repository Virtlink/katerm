package net.pelsmaeker.katerm.regex

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import net.pelsmaeker.katerm.substitutions.emptySubstitution
import net.pelsmaeker.katerm.testRegexBuilder

class StarTests : FunSpec({

    context("deriveAndUnify()") {

        test("should derive and unify with a single term matching the pattern") {
            with(testRegexBuilder) {
                // Arrange
                val subPattern = T(string("Foo"))
                val regex = star(subPattern)
                val terms = listOf(string("Foo"))

                // Act
                val m0 = regex.buildMatcher(emptySubstitution())
                val m1 = m0.match(terms[0])

                // Assert
                m1.isEmpty() shouldBe false
                val newSubstitution = m1.getAcceptingMetadata()!!
                newSubstitution.isEmpty() shouldBe true
            }
        }

        test("should derive and unify with a multiple terms matching the pattern") {
            with(testRegexBuilder) {
                // Arrange
                val subPattern = T(string("Foo"))
                val regex = star(subPattern)
                val terms = listOf(string("Foo"), string("Foo"), string("Foo"))

                // Act
                val m0 = regex.buildMatcher(emptySubstitution())
                val m1 = m0.match(terms[0])
                val m2 = m1.match(terms[1])
                val m3 = m1.match(terms[2])

                // Assert
                m3.isEmpty() shouldBe false
                val newSubstitution = m1.getAcceptingMetadata()!!
                newSubstitution.isEmpty() shouldBe true
            }
        }

        test("should derive and unify with no terms matching the pattern in a concat") {
            with(testRegexBuilder) {
                // Arrange
                val subPattern = T(string("Foo"))
                val followPattern = T(string("Bar"))
                val regex = concat(star(subPattern), followPattern)
                val terms = listOf(string("Bar"))

                // Act
                val m0 = regex.buildMatcher(emptySubstitution())
                val m1 = m0.match(terms[0])

                // Assert
                m1.isEmpty() shouldBe false
                val newSubstitution = m1.getAcceptingMetadata()!!
                newSubstitution.isEmpty() shouldBe true
            }
        }

        test("should derive and unify with a single term matching the pattern in a concat") {
            with(testRegexBuilder) {
                // Arrange
                val subPattern = T(string("Foo"))
                val followPattern = T(string("Bar"))
                val regex = concat(star(subPattern), followPattern)
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

        test("should derive and unify with a multiple terms matching the pattern in a concat") {
            with(testRegexBuilder) {
                // Arrange
                val subPattern = T(string("Foo"))
                val followPattern = T(string("Bar"))
                val regex = concat(star(subPattern), followPattern)
                val terms = listOf(string("Foo"), string("Foo"), string("Foo"), string("Bar"))

                // Act
                val m0 = regex.buildMatcher(emptySubstitution())
                val m1 = m0.match(terms[0])
                val m2 = m1.match(terms[1])
                val m3 = m2.match(terms[2])
                val m4 = m3.match(terms[3])

                // Assert
                m3.isEmpty() shouldBe false
                val newSubstitution = m4.getAcceptingMetadata()!!
                newSubstitution.isEmpty() shouldBe true
            }
        }
    }

//    context("termVars") {
//        test("should return the union of termVars from both patterns") {
//            with(testTermBuilder) {
//                // Arrange
//                val subPattern = T(!"A")
//                val regex = Star(subPattern)
//
//                // Act
//                val termVars = regex.termVars
//
//                // Assert
//                termVars shouldBe subPattern.termVars
//            }
//        }
//    }

})