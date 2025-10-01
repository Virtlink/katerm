package net.pelsmaeker.katerm.regex

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import net.pelsmaeker.katerm.substitutions.emptySubstitution
import net.pelsmaeker.katerm.terms.withTermBuilder
import net.pelsmaeker.katerm.testTermBuilder

class EmptyTests : FunSpec({

    context("deriveAndUnify()") {
        test("should always return null") {
            with(testTermBuilder) {
                // Arrange
                val regex = epsilon()
                val substitution = emptySubstitution()

                // Act
                val term = string("Bar")
                val m0 = regex.buildMatcher(substitution)
                val m1 = m0.match(term)

                // Assert
                m1.isEmpty() shouldBe true
            }
        }
    }

//    context("substitute()") {
//        test("should not change regex") {
//            with(testTermBuilder) {
//                // Arrange
//                val termVar = !"X"
//                val regex = epsilon()
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

//    context("termVars") {
//        test("should return an empty set") {
//            with(testTermBuilder) {
//                // Arrange
//                val regex = epsilon()
//
//                // Act
//                val termVars = regex.termVars
//
//                // Assert
//                termVars shouldBe emptySet()
//            }
//        }
//    }

})