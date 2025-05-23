package net.pelsmaeker.katerm.substitutions

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import net.pelsmaeker.katerm.Term
import net.pelsmaeker.katerm.TermVar

class UnifyOperationTests: FunSpec({

    context("unification") {
        withData<Pair<Pair<Term, Term>, Map<Set<TermVar>, Term>?>>(
            nameFn = { (pair, substitution) -> "unify(${pair.first}, ${pair.second}) -> " + (substitution?.let { "$it" } ?: "⊥") },
            testTermBuilder.run {
                listOf(
                    // Literals
                    Pair(newInt(42), newInt(42)) to emptyMap<Set<TermVar>, Term>(),
                    Pair(newString("A"), newString("A")) to emptyMap<Set<TermVar>, Term>(),
                    Pair(newReal(42.0), newReal(42.0)) to emptyMap<Set<TermVar>, Term>(),

                    Pair(newInt(42), newInt(43)) to null,
                    Pair(newString("A"), newString("a")) to null,
                    Pair(newReal(42.0), newReal(42.1)) to null,
                    Pair(newInt(42), newString("A")) to null,

                    // Lists
                    Pair(newEmptyList(), newEmptyList()) to emptyMap<Set<TermVar>, Term>(),
                    Pair(newListOf(newInt(42)), newListOf(newInt(42))) to emptyMap<Set<TermVar>, Term>(),
                    Pair(newListOf(newInt(42), newString("A")), newListOf(newInt(42), newString("A"))) to emptyMap<Set<TermVar>, Term>(),

                    Pair(newEmptyList(), newListOf(newInt(42))) to null,
                    Pair(newListOf(newInt(42)), newInt(42)) to null,
                    Pair(newListOf(newString("A")), newString("A")) to null,

                    // Options
                    Pair(newOption(newInt(42)), newOption(newInt(42))) to emptyMap<Set<TermVar>, Term>(),
                    Pair(newOption(newString("A")), newOption(newString("A"))) to emptyMap<Set<TermVar>, Term>(),
                    Pair(newOption(newReal(42.0)), newOption(newReal(42.0))) to emptyMap<Set<TermVar>, Term>(),
                    Pair(newEmptyOption(), newEmptyOption()) to emptyMap<Set<TermVar>, Term>(),
                    Pair(newOption(newInt(42)), newOption(newString("A"))) to null,
                    Pair(newOption(newString("A")), newOption(newInt(42))) to null,
                    Pair(newOption(newInt(42)), newOption(newReal(42.0))) to null,
                    Pair(newEmptyOption(), newOption(newInt(42))) to null,
                    Pair(newEmptyOption(), newEmptyList()) to null,

                    // Variables
                    Pair(newVar("X"), newVar("X")) to emptyMap<Set<TermVar>, Term>(),
                    Pair(newVar("X"), newVar("Y")) to mapOf(setOf(newVar("X")) to newVar("Y")),
                    Pair(newVar("X"), newInt(42)) to mapOf(setOf(newVar("X")) to newInt(42)),
                    Pair(newVar("X"), newString("A")) to mapOf(setOf(newVar("X")) to newString("A")),
                    Pair(newVar("X"), newReal(42.0)) to mapOf(setOf(newVar("X")) to newReal(42.0)),
                    Pair(newVar("X"), newEmptyList()) to mapOf(setOf(newVar("X")) to newEmptyList()),
                    Pair(newVar("X"), newListOf(newInt(42))) to mapOf(setOf(newVar("X")) to newListOf(newInt(42))),
                    Pair(newVar("X"), newOption(newInt(42))) to mapOf(setOf(newVar("X")) to newOption(newInt(42))),
                    Pair(newVar("X"), newEmptyOption()) to mapOf(setOf(newVar("X")) to newEmptyOption()),
                    Pair(newList(newVar("X"), newVar("XS")), newList(newInt(42), newListOf(newString("A"))) ) to mapOf(
                        setOf(newVar("X")) to newInt(42),
                        setOf(newVar("XS")) to newListOf(newString("A"))
                    ),

                )
            }
        ) { (pair, substitution) ->
            // Arrange
            val (left, right) = pair

            // Act
            val result = unify(left, right)

            // Assert
            result?.toMap() shouldBe substitution
        }
    }

})