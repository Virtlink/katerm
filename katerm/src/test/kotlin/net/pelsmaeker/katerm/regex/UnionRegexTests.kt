package net.pelsmaeker.katerm.regex

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import net.pelsmaeker.katerm.substitutions.emptySubstitution
import net.pelsmaeker.katerm.testRegexBuilder

class UnionRegexTests : FunSpec({

    context("deriveAndUnify()") {
        test("should derive and unify with a term matching the left term") {
            with(testRegexBuilder) {
                // Arrange
                val firstPattern = T(string("Foo"))
                val secondPattern = T(string("Bar"))
                val regex = union(firstPattern, secondPattern)
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

        test("should derive and unify with a term matching the right term") {
            with(testRegexBuilder) {
                // Arrange
                val firstPattern = T(string("Foo"))
                val secondPattern = T(string("Bar"))
                val regex = union(firstPattern, secondPattern)
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

        test("should derive and unify with a term matching both terms") {
            with(testRegexBuilder) {
                // Arrange
                val firstPattern = T(string("Foo"))
                val secondPattern = T(string("Foo"))
                val regex = union(firstPattern, secondPattern)
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

        test("should not derive and unify with a term matching neither term") {
            with(testRegexBuilder) {
                // Arrange
                val firstPattern = T(string("Foo"))
                val secondPattern = T(string("Bar"))
                val regex = union(firstPattern, secondPattern)
                val terms = listOf(string("Qux"))

                // Act
                val m0 = regex.buildMatcher(emptySubstitution())
                val m1 = m0.match(terms[0])

                // Assert
                m1.isEmpty() shouldBe true
            }
        }

        test("should handle nullable first pattern correctly") {
            with(testRegexBuilder) {
                // Arrange
                val firstPattern = star(T(string("Foo")))
                val secondPattern = T(string("Foo"))
                val regex = union(firstPattern, secondPattern)
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

        test("should handle nullable second pattern correctly") {
            with(testRegexBuilder) {
                // Arrange
                val firstPattern = T(string("Foo"))
                val secondPattern = star(T(string("Foo")))
                val regex = union(firstPattern, secondPattern)
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
    }

//    context("termVars") {
//        test("should return the union of termVars from both patterns") {
//            with(testTermBuilder) {
//                // Arrange
//                val firstPattern = T(!"A")
//                val secondPattern = T(!"B")
//                val regex = union(firstPattern, secondPattern)
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