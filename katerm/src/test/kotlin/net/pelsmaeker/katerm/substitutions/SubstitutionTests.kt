package net.pelsmaeker.katerm.substitutions

import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import net.pelsmaeker.katerm.Term
import net.pelsmaeker.katerm.TermVar
import io.kotest.core.test.TestScope
import io.kotest.matchers.collections.shouldBeOneOf
import io.kotest.property.assume

/**
 * Tests the implementation of the [Substitution] interface.
 *
 * @param builder A function that takes a collection of pairs of variables and terms and returns a [Substitution].
 */
fun testSubstitution(
    builder: TestScope.(pairs: Collection<Pair<TermVar, Term>>) -> Substitution,
) = funSpec {

    beforeContainer {
        registerTermArbs()
    }

    context("isEmpty()") {
        test("should return true, when the substitution is empty") {
            // Arrange
            val pairs = emptyList<Pair<TermVar, Term>>()
            val substitution = builder(pairs)

            // Act/Assert
            substitution.isEmpty() shouldBe true
        }

        test("should return false, when the substitution is not empty") {
            with(testTermBuilder) {
                // Arrange
                val pairs = listOf<Pair<TermVar, Term>>(
                    newVar("x") to newVar("y"),
                )
                val substitution = builder(pairs)

                // Act/Assert
                substitution.isEmpty() shouldBe false
            }
        }

        test("should match whether the pairs are empty") {
            checkAll<List<Pair<TermVar, Term>>> { pairs ->
                // Arrange
                val substitution = builder(pairs)

                // Act/Assert
                substitution.isEmpty() shouldBe pairs.isEmpty()
            }
        }
    }

    context("isNotEmpty()") {
        test("should return true, when the substitution is not empty") {
            with(testTermBuilder) {
                // Arrange
                val pairs = listOf<Pair<TermVar, Term>>(
                    newVar("x") to newVar("y"),
                )
                val substitution = builder(pairs)

                // Act/Assert
                substitution.isNotEmpty() shouldBe true
            }
        }

        test("should return false, when the substitution is empty") {
            // Arrange
            val pairs = emptyList<Pair<TermVar, Term>>()
            val substitution = builder(pairs)

            // Act/Assert
            substitution.isNotEmpty() shouldBe false
        }

        test("should match whether the pairs are not empty") {
            checkAll<List<Pair<TermVar, Term>>> { pairs ->
                // Arrange
                val substitution = builder(pairs)

                // Act/Assert
                substitution.isNotEmpty() shouldBe pairs.isNotEmpty()
            }
        }
    }

    context("variables") {
        test("should return the set of variables in the substitution") {
            with(testTermBuilder) {
                // Arrange
                val pairs = listOf<Pair<TermVar, Term>>(
                    newVar("x") to newString("Y"),
                    newVar("a") to newString("B"),
                )
                val substitution = builder(pairs)

                // Act/Assert
                substitution.variables shouldBe setOf(newVar("x"), newVar("a"))
            }
        }

        test("should also return variables that have the same value in the substitution") {
            with(testTermBuilder) {
                // Arrange
                val pairs = listOf<Pair<TermVar, Term>>(
                    newVar("a") to newString("X"),
                    newVar("b") to newVar("d"),
                    newVar("c") to newString("X"),
                    newVar("d") to newString("X"),
                )
                val substitution = builder(pairs)

                // Act/Assert
                substitution.variables shouldBe setOf(
                    newVar("a"), newVar("b"), newVar("c"), newVar("d"),
                )
            }
        }

        test("should return an empty set when the substitution is empty") {
            // Arrange
            val pairs = emptyList<Pair<TermVar, Term>>()
            val substitution = builder(pairs)

            // Act/Assert
            substitution.variables shouldBe emptySet()
        }
    }

    context("get()") {
        test("should return the term the variable is mapped to, when the variable is in the substitution") {
            with(testTermBuilder) {
                // Arrange
                val pairs = listOf<Pair<TermVar, Term>>(
                    newVar("x") to newString("Y"),
                    newVar("a") to newString("B"),
                )
                val substitution = builder(pairs)

                // Act/Assert
                substitution[newVar("x")] shouldBe newString("y")
                substitution[newVar("a")] shouldBe newString("b")
            }
        }

        test("should return the variable itself, when the variable is not in the substitution") {
            with(testTermBuilder) {
                // Arrange
                val pairs = listOf<Pair<TermVar, Term>>(
                    newVar("x") to newString("Y"),
                    newVar("a") to newString("B"),
                )
                val substitution = builder(pairs)

                // Act/Assert
                substitution[newVar("z")] shouldBe newVar("z")
            }
        }

        test("should return the term the variable is mapped to, when the variable is in the substitution") {
            checkAll<List<Pair<TermVar, Term>>> { pairs ->
                // Arrange
                val substitution = builder(pairs)

                // Act/Assert
                for ((k, v) in pairs) {
                    substitution[k] shouldBe v
                }
            }
        }
    }

    context("find()") {
        test("should return the variable, when the variable is alone in the substitution") {
            with(testTermBuilder) {
                // Arrange
                val pairs = listOf<Pair<TermVar, Term>>(
                    newVar("x") to newString("Y"),
                    newVar("a") to newString("B"),
                )
                val substitution = builder(pairs)

                // Act/Assert
                substitution.find(newVar("x")) shouldBe newVar("x")
                substitution.find(newVar("a")) shouldBe newVar("a")
            }
        }

        test("should return the representative variable for the given variable, when the variable is in the substitution") {
            with(testTermBuilder) {
                // Arrange
                val pairs = listOf<Pair<TermVar, Term>>(
                    newVar("x") to newVar("y"),
                    newVar("y") to newString("Z"),
                    newVar("z") to newString("Z"),
                    newVar("a") to newVar("b"),
                )
                val substitution = builder(pairs)

                // Act
                val reprX = substitution.find(newVar("x"))
                val reprY = substitution.find(newVar("y"))
                val reprZ = substitution.find(newVar("z"))
                val reprA = substitution.find(newVar("a"))
                val reprB = substitution.find(newVar("b"))

                // Act/Assert
                reprX shouldBeOneOf listOf(newVar("x"), newVar("y"), newVar("z"))
                reprY shouldBe reprX
                reprZ shouldBe reprX
                reprA shouldBe newVar("a")
                reprB shouldBe null
            }
        }

        test("should return null, when the variable is not in the substitution") {
            with(testTermBuilder) {
                // Arrange
                val pairs = listOf<Pair<TermVar, Term>>(
                    newVar("x") to newString("Y"),
                    newVar("a") to newString("B"),
                )
                val substitution = builder(pairs)

                // Act/Assert
                substitution.find(newVar("z")) shouldBe null
            }
        }
    }

    context("contains()") {
        test("should return true, when the variable is alone in the substitution") {
            with(testTermBuilder) {
                // Arrange
                val pairs = listOf<Pair<TermVar, Term>>(
                    newVar("x") to newString("Y"),
                    newVar("a") to newString("B"),
                )
                val substitution = builder(pairs)

                // Act/Assert
                substitution[newVar("x")] shouldBe true
                substitution[newVar("a")] shouldBe true
            }
        }

        test("should return true, when the variable has a representative in the substitution") {
            with(testTermBuilder) {
                // Arrange
                val pairs = listOf<Pair<TermVar, Term>>(
                    newVar("x") to newVar("y"),
                    newVar("y") to newString("Z"),
                    newVar("z") to newString("Z"),
                    newVar("a") to newVar("b"),
                )
                val substitution = builder(pairs)

                // Act
                val containsX = substitution.contains(newVar("x"))
                val containsY = substitution.contains(newVar("y"))
                val containsZ = substitution.contains(newVar("z"))
                val containsA = substitution.contains(newVar("a"))
                val containsB = substitution.contains(newVar("b"))

                // Act/Assert
                containsX shouldBe true
                containsY shouldBe true
                containsZ shouldBe true
                containsA shouldBe true
                containsB shouldBe false
            }
        }

        test("should return false, when the variable is not in the substitution") {
            with(testTermBuilder) {
                // Arrange
                val pairs = listOf<Pair<TermVar, Term>>(
                    newVar("x") to newString("Y"),
                    newVar("a") to newString("B"),
                )
                val substitution = builder(pairs)

                // Act/Assert
                substitution[newVar("z")] shouldBe false
            }
        }
    }

    context("areEqual()") {
        test("should return true, when the terms are equal and contain no variables") {
            checkAll<Term> { term ->
                // Assume
                assume(term.termVars.isEmpty())

                // Arrange
                val substitution = builder(emptyList())

                // Act/Assert
                substitution.areEqual(term, term) shouldBe true
            }
        }

        test("should return false, when the terms are not equal and contain no variables") {
            checkAll<Term, Term> { term1, term2 ->
                // Assume
                assume(term1 != term2)
                assume(term1.termVars.isEmpty())
                assume(term2.termVars.isEmpty())

                // Arrange
                val substitution = builder(emptyList())

                // Act/Assert
                substitution.areEqual(term1, term2) shouldBe false
            }
        }

        test("should return true, when there exists a unifier that makes the terms equal") {
            checkAll<Term, Term> { term1, term2 ->
                // Arrange
                val substitution = builder(emptyList())

                val unifier = substitution.unify(term1, term2)

                // Act/Assert
                substitution.areEqual(term1, term2) shouldBe (unifier != null)
            }
        }

        // TODO: Add tests where there are already variables in the substitution (using addHolesToTerm())

    }

    context("getFreeVars()") {
        // TODO: Add tests
    }

    context("isGround()") {
        // TODO: Add tests
    }

    context("apply()") {
        // TODO: Add tests
    }
}