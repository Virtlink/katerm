package net.pelsmaeker.katerm.regex

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import net.pelsmaeker.katerm.substitutions.emptySubstitution
import net.pelsmaeker.katerm.testRegexBuilder

class AtomTests : FunSpec({

    context("match()") {
        test("should derive and unify with a matching term") {
            with(testRegexBuilder) {
                // Arrange
                val regex = T(string("Foo"))
                val substitution = emptySubstitution()
                val matcher = regex.buildMatcher(substitution)

                // Act
                val term = string("Foo")
                val newMatcher = matcher.match(term)

                // Assert
                newMatcher.isEmpty() shouldNotBe true
                val newSubstitution = newMatcher.getAcceptingMetadata()!!
                newSubstitution.isEmpty() shouldBe true
            }
        }

        test("should not derive and unify with a non-matching term") {
            with(testRegexBuilder) {
                // Arrange
                val regex = T(string("Foo"))
                val substitution = emptySubstitution()
                val matcher = regex.buildMatcher(substitution)

                // Act
                val term = string("Bar")
                val newMatcher = matcher.match(term)

                // Assert
                newMatcher.isEmpty() shouldBe true
            }
        }

        test("should unify a term variable with a term") {
            with(testRegexBuilder) {
                // Arrange
                val regex = T(!"X")
                val substitution = emptySubstitution()
                val matcher = regex.buildMatcher(substitution)

                // Act
                val term = string("Foo")
                val newMatcher = matcher.match(term)

                // Assert
                newMatcher.isEmpty() shouldNotBe true
                val newSubstitution = newMatcher.getAcceptingMetadata()!!
                newSubstitution.toMap() shouldBe mapOf(
                    setOf(!"X") to string("Foo")
                )
            }
        }

        test("should unify a term variable with another term variable") {
            with(testRegexBuilder) {
                // Arrange
                val regex = T(!"X")
                val substitution = emptySubstitution()
                val matcher = regex.buildMatcher(substitution)

                // Act
                val term = !"Y"
                val newMatcher = matcher.match(term)

                // Assert
                newMatcher.isEmpty() shouldNotBe true
                val newSubstitution = newMatcher.getAcceptingMetadata()!!
                newSubstitution.toMap() shouldBe mapOf(
                    setOf(!"X") to !"Y"
                )
            }
        }
    }

//    context("substitute()") {
//        test("should substitute term variables correctly") {
//            with(testTermBuilder) {
//                // Arrange
//                val termVar = !"X"
//                val regex = T(termVar)
//                val newTerm = string("NewValue")
//
//                // Act
//                val substitutedRegex = regex.substitute(termVar, newTerm, this)
//
//                // Assert
//                substitutedRegex shouldBe Atom(string("NewValue"))
//            }
//        }
//
//        test("should not change regex when substituting with the same term") {
//            with(testTermBuilder) {
//                // Arrange
//                val termVar = !"X"
//                val regex = T(termVar)
//                val newTerm = termVar
//
//                // Act
//                val substitutedRegex = regex.substitute(termVar, newTerm, this)
//
//                // Assert
//                substitutedRegex shouldBe regex
//            }
//        }
//    }


})