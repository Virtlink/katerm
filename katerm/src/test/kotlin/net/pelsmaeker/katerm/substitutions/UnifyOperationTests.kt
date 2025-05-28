package net.pelsmaeker.katerm.substitutions

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import net.pelsmaeker.katerm.Term
import net.pelsmaeker.katerm.TermVar
import net.pelsmaeker.katerm.withTermBuilder

class UnifyOperationTests: FunSpec({

    context("unification") {
        withData<Pair<Pair<Term, Term>, Map<Set<TermVar>, Term>?>>(
            nameFn = { (pair, substitution) -> "unify(${pair.first}, ${pair.second}) -> " + (substitution?.let { "$it" } ?: "⊥") },
            withTermBuilder(testTermBuilder) {
                listOf(
                    // Int and another term
                    Pair(
                        int(42),
                        int(42)
                    ) to emptyMap(),
                    Pair(
                        int(42),
                        int(1337)
                    ) to null,
                    Pair(
                        int(42),
                        real(3.14)
                    ) to null,
                    Pair(
                        int(42),
                        real(6.28)
                    ) to null,
                    Pair(
                        int(42),
                        string("A")
                    ) to null,
                    Pair(
                        int(42),
                        string("B")
                    ) to null,
                    Pair(
                        int(42),
                        list()
                    ) to null,
                    Pair(
                        int(42),
                        list(int(42))
                    ) to null,
                    Pair(
                        int(42),
                        list(int(42), int(42))
                    ) to null,
                    Pair(
                        int(42),
                        list(int(42), int(1337))
                    ) to null,
                    Pair(
                        int(42),
                        none()
                    ) to null,
                    Pair(
                        int(42),
                        some(int(42))
                    ) to null,
                    Pair(
                        int(42),
                        !"X"
                    ) to mapOf(
                        setOf(!"X") to int(42),
                    ),
                    Pair(
                        int(42),
                        !"Y"
                    ) to mapOf(
                        setOf(!"Y") to int(42),
                    ),
                    Pair(
                        int(42),
                        !"X"..!"XS"
                    ) to null,
                    Pair(
                        int(1337),
                        int(42)
                    ) to null,
                    Pair(
                        int(1337),
                        int(1337)
                    ) to emptyMap(),
                    Pair(
                        int(1337),
                        real(3.14)
                    ) to null,
                    Pair(
                        int(1337),
                        real(6.28)
                    ) to null,
                    Pair(
                        int(1337),
                        string("A")
                    ) to null,
                    Pair(
                        int(1337),
                        string("B")
                    ) to null,
                    Pair(
                        int(1337),
                        list()
                    ) to null,
                    Pair(
                        int(1337),
                        list(int(1337))
                    ) to null,
                    Pair(
                        int(1337),
                        list(int(1337), int(42))
                    ) to null,
                    Pair(
                        int(1337),
                        list(int(1337), int(1337))
                    ) to null,
                    Pair(
                        int(1337),
                        none()
                    ) to null,
                    Pair(
                        int(1337),
                        some(int(1337))
                    ) to null,
                    Pair(
                        int(1337),
                        !"X"
                    ) to mapOf(
                        setOf(!"X") to int(1337),
                    ),
                    Pair(
                        int(1337),
                        !"Y"
                    ) to mapOf(
                        setOf(!"Y") to int(1337),
                    ),
                    Pair(
                        int(1337),
                        !"X"..!"XS"
                    ) to null,

                    // Real and another term
                    Pair(
                        real(3.14),
                        int(42)
                    ) to null,
                    Pair(
                        real(3.14),
                        int(1337)
                    ) to null,
                    Pair(
                        real(3.14),
                        real(3.14)
                    ) to emptyMap(),
                    Pair(
                        real(3.14),
                        real(6.28)
                    ) to null,
                    Pair(
                        real(3.14),
                        string("A")
                    ) to null,
                    Pair(
                        real(3.14),
                        string("B")
                    ) to null,
                    Pair(
                        real(3.14),
                        list()
                    ) to null,
                    Pair(
                        real(3.14),
                        list(real(3.14))
                    ) to null,
                    Pair(
                        real(3.14),
                        list(real(3.14), int(42))
                    ) to null,
                    Pair(
                        real(3.14),
                        list(real(3.14), int(1337))
                    ) to null,
                    Pair(
                        real(3.14),
                        none()
                    ) to null,
                    Pair(
                        real(3.14),
                        some(real(3.14))
                    ) to null,
                    Pair(
                        real(3.14),
                        !"X"
                    ) to mapOf(
                        setOf(!"X") to real(3.14),
                    ),
                    Pair(
                        real(3.14),
                        !"Y"
                    ) to mapOf(
                        setOf(!"Y") to real(3.14),
                    ),
                    Pair(
                        real(3.14),
                        !"X"..!"XS"
                    ) to null,
                    Pair(
                        real(6.28),
                        int(42)
                    ) to null,
                    Pair(
                        real(6.28),
                        int(1337)
                    ) to null,
                    Pair(
                        real(6.28),
                        real(3.14)
                    ) to null,
                    Pair(
                        real(6.28),
                        real(6.28)
                    ) to emptyMap(),
                    Pair(
                        real(6.28),
                        string("A")
                    ) to null,
                    Pair(
                        real(6.28),
                        string("B")
                    ) to null,
                    Pair(
                        real(6.28),
                        list()
                    ) to null,
                    Pair(
                        real(6.28),
                        list(real(6.28))
                    ) to null,
                    Pair(
                        real(6.28),
                        list(real(6.28), int(42))
                    ) to null,
                    Pair(
                        real(6.28),
                        list(real(6.28), int(1337))
                    ) to null,
                    Pair(
                        real(6.28),
                        none()
                    ) to null,
                    Pair(
                        real(6.28),
                        some(real(6.28))
                    ) to null,
                    Pair(
                        real(6.28),
                        !"X"
                    ) to mapOf(
                        setOf(!"X") to real(6.28),
                    ),

                    Pair(
                        real(6.28),
                        !"Y"
                    ) to mapOf(
                        setOf(!"Y") to real(6.28),
                    ),
                    Pair(
                        real(6.28),
                        !"X"..!"XS"
                    ) to null,

                    // String and another term
                    Pair(
                        string("A"),
                        int(42)
                    ) to null,
                    Pair(
                        string("A"),
                        int(1337)
                    ) to null,
                    Pair(
                        string("A"),
                        real(3.14)
                    ) to null,
                    Pair(
                        string("A"),
                        real(6.28)
                    ) to null,
                    Pair(
                        string("A"),
                        string("A")
                    ) to emptyMap(),
                    Pair(
                        string("A"),
                        string("B")
                    ) to null,
                    Pair(
                        string("A"),
                        list()
                    ) to null,
                    Pair(
                        string("A"),
                        list(string("A"))
                    ) to null,
                    Pair(
                        string("A"),
                        list(string("A"), int(42))
                    ) to null,
                    Pair(
                        string("A"),
                        list(string("A"), int(1337))
                    ) to null,
                    Pair(
                        string("A"),
                        none()
                    ) to null,
                    Pair(
                        string("A"),
                        some(string("A"))
                    ) to null,
                    Pair(
                        string("A"),
                        !"X"
                    ) to mapOf(
                        setOf(!"X") to string("A"),
                    ),
                    Pair(
                        string("A"),
                        !"Y"
                    ) to mapOf(
                        setOf(!"Y") to string("A"),
                    ),
                    Pair(
                        string("A"),
                        !"X"..!"XS"
                    ) to null,
                    Pair(
                        string("B"),
                        int(42)
                    ) to null,
                    Pair(
                        string("B"),
                        int(1337)
                    ) to null,
                    Pair(
                        string("B"),
                        real(3.14)
                    ) to null,
                    Pair(
                        string("B"),
                        real(6.28)
                    ) to null,
                    Pair(
                        string("B"),
                        string("A")
                    ) to null,
                    Pair(
                        string("B"),
                        string("B")
                    ) to emptyMap(),
                    Pair(
                        string("B"),
                        list()
                    ) to null,
                    Pair(
                        string("B"),
                        list(string("B"))
                    ) to null,
                    Pair(
                        string("B"),
                        list(string("B"), int(42))
                    ) to null,
                    Pair(
                        string("B"),
                        list(string("B"), int(1337))
                    ) to null,
                    Pair(
                        string("B"),
                        none()
                    ) to null,
                    Pair(
                        string("B"),
                        some(string("B"))
                    ) to null,
                    Pair(
                        string("B"),
                        !"X"
                    ) to mapOf(
                        setOf(!"X") to string("B"),
                    ),
                    Pair(
                        string("B"),
                        !"Y"
                    ) to mapOf(
                        setOf(!"Y") to string("B"),
                    ),
                    Pair(
                        string("B"),
                        !"X"..!"XS"
                    ) to null,

                    // Empty list and another term
                    Pair(
                        list(),
                        int(42)
                    ) to null,
                    Pair(
                        list(),
                        int(1337)
                    ) to null,
                    Pair(
                        list(),
                        real(3.14)
                    ) to null,
                    Pair(
                        list(),
                        real(6.28)
                    ) to null,
                    Pair(
                        list(),
                        string("A")
                    ) to null,
                    Pair(
                        list(),
                        string("B")
                    ) to null,
                    Pair(
                        list(),
                        list()
                    ) to emptyMap(),
                    Pair(
                        list(),
                        list(list())
                    ) to null,
                    Pair(
                        list(),
                        list(list(), int(42))
                    ) to null,
                    Pair(
                        list(),
                        list(list(), int(1337))
                    ) to null,
                    Pair(
                        list(),
                        none()
                    ) to null,
                    Pair(
                        list(),
                        some(list())
                    ) to null,
                    Pair(
                        list(),
                        !"X"
                    ) to mapOf(
                        setOf(!"X") to list(),
                    ),
                    Pair(
                        list(),
                        !"Y"
                    ) to mapOf(
                        setOf(!"Y") to list(),
                    ),
                    Pair(
                        list(),
                        !"X"..!"XS"
                    ) to null,

                    // List with a single term and another term
                    Pair(
                        list(int(42)),
                        int(42)
                    ) to null,
                    Pair(
                        list(int(1337)),
                        int(1337)
                    ) to null,
                    Pair(
                        list(real(3.14)),
                        real(3.14)
                    ) to null,
                    Pair(
                        list(real(6.28)),
                        real(6.28)
                    ) to null,
                    Pair(
                        list(string("A")),
                        string("A")
                    ) to null,
                    Pair(
                        list(string("B")),
                        string("B")
                    ) to null,
                    Pair(
                        list(list()),
                        list()
                    ) to null,
                    Pair(
                        list(list(int(1))),
                        list(list(int(1)))
                    ) to emptyMap(),
                    Pair(
                        list(list(int(1), int(42))),
                        list(list(int(1)), int(42))
                    ) to null,
                    Pair(
                        list(list(int(1), int(1337))),
                        list(list(int(1)), int(1337))
                    ) to null,
                    Pair(
                        list(none()),
                        none()
                    ) to null,
                    Pair(
                        list(some(int(1))),
                        some(list(int(1)))
                    ) to null,
                    Pair(
                        list(int(1)),
                        !"X"
                    ) to mapOf(
                        setOf(!"X") to list(int(1)),
                    ),
                    Pair(
                        list(int(1)),
                        !"Y"
                    ) to mapOf(
                        setOf(!"Y") to list(int(1)),
                    ),
                    Pair(
                        list(int(1)),
                        !"X"..!"XS"
                    ) to mapOf(
                        setOf(!"X") to int(1),
                        setOf(!"XS") to list(),
                    ),
                    Pair(
                        list(!"X"),
                        !"X"
                    ) to null,
                    Pair(
                        list(!"Y"),
                        !"Y"
                    ) to null,
                    Pair(
                        list(!"X"..!"XS"),
                        !"X"..!"XS"
                    ) to null,

                    // List with multiple terms and another term
                    Pair(
                        list(int(42), int(42)),
                        int(42)
                    ) to null,
                    Pair(
                        list(int(1337), int(42)),
                        int(1337)
                    ) to null,
                    Pair(
                        list(real(3.14), int(42)),
                        real(3.14)
                    ) to null,
                    Pair(
                        list(real(6.28), int(42)),
                        real(6.28)
                    ) to null,
                    Pair(
                        list(string("A"), int(42)),
                        string("A")
                    ) to null,
                    Pair(
                        list(string("B"), int(42)),
                        string("B")
                    ) to null,
                    Pair(
                        list(list(), int(42)),
                        list()
                    ) to null,
                    Pair(
                        list(list(int(1)), int(42)),
                        list(list(int(1), int(42)))
                    ) to null,
                    Pair(
                        list(list(int(1), int(42)), int(42)),
                        list(list(int(1), int(42)), int(42))
                    ) to emptyMap(),
                    Pair(
                        list(list(int(1), int(1337)), int(42)),
                        list(list(int(1), int(42)), int(1337))
                    ) to null,
                    Pair(
                        list(none(), int(42)),
                        none()
                    ) to null,
                    Pair(
                        list(some(int(1)), int(42)),
                        some(list(int(1), int(42)))
                    ) to null,
                    Pair(
                        list(int(1), int(42)),
                        !"X"
                    ) to mapOf(
                        setOf(!"X") to list(int(1), int(42)),
                    ),
                    Pair(
                        list(int(1), int(42)),
                        !"Y"
                    ) to mapOf(
                        setOf(!"Y") to list(int(1), int(42)),
                    ),
                    Pair(
                        list(int(1), int(42)),
                        !"X"..!"XS"
                    ) to mapOf(
                        setOf(!"X") to int(1),
                        setOf(!"XS") to list(int(42)),
                    ),
                    Pair(
                        list(!"X", int(42)),
                        !"X"
                    ) to null,
                    Pair(
                        list(!"Y", int(42)),
                        !"Y"
                    ) to null,
                    Pair(
                        list(!"X"..!"XS", int(42)),
                        !"X"..!"XS"
                    ) to null,

                    // Empty option and another term
                    Pair(
                        none(),
                        int(42)
                    ) to null,
                    Pair(
                        none(),
                        int(1337)
                    ) to null,
                    Pair(
                        none(),
                        real(3.14)
                    ) to null,
                    Pair(
                        none(),
                        real(6.28)
                    ) to null,
                    Pair(
                        none(),
                        string("A")
                    ) to null,
                    Pair(
                        none(),
                        string("B")
                    ) to null,
                    Pair(
                        none(),
                        list()
                    ) to null,
                    Pair(
                        none(),
                        list(none())
                    ) to null,
                    Pair(
                        none(),
                        list(none(), int(42))
                    ) to null,
                    Pair(
                        none(),
                        list(none(), int(1337))
                    ) to null,
                    Pair(
                        none(),
                        none()
                    ) to emptyMap(),
                    Pair(
                        none(),
                        some(none())
                    ) to null,
                    Pair(
                        none(),
                        !"X"
                    ) to mapOf(
                        setOf(!"X") to none(),
                    ),
                    Pair(
                        none(),
                        !"Y"
                    ) to mapOf(
                        setOf(!"Y") to none(),
                    ),
                    Pair(
                        none(),
                        !"X"..!"XS"
                    ) to null,

                    // Non-empty option and another term
                    Pair(
                        some(int(42)),
                        int(42)
                    ) to null,
                    Pair(
                        some(int(1337)),
                        int(1337)
                    ) to null,
                    Pair(
                        some(real(3.14)),
                        real(3.14)
                    ) to null,
                    Pair(
                        some(real(6.28)),
                        real(6.28)
                    ) to null,
                    Pair(
                        some(string("A")),
                        string("A")
                    ) to null,
                    Pair(
                        some(string("B")),
                        string("B")
                    ) to null,
                    Pair(
                        some(list()),
                        list()
                    ) to null,
                    Pair(
                        some(list(int(1))),
                        list(some(int(1)))
                    ) to null,
                    Pair(
                        some(list(int(1), int(42))),
                        list(some(int(1)), int(42))
                    ) to null,
                    Pair(
                        some(list(int(1), int(1337))),
                        list(some(int(1)), int(1337))
                    ) to null,
                    Pair(
                        some(none()),
                        none()
                    ) to null,
                    Pair(
                        some(some(int(1))),
                        some(some(int(1)))
                    ) to emptyMap(),
                    Pair(
                        some(int(1)),
                        !"X"
                    ) to mapOf(
                        setOf(!"X") to some(int(1)),
                    ),
                    Pair(
                        some(int(1)),
                        !"Y"
                    ) to mapOf(
                        setOf(!"Y") to some(int(1)),
                    ),
                    Pair(
                        some(int(1)),
                        !"X"..!"XS"
                    ) to null,
                    Pair(
                        some(!"X"),
                        !"X"
                    ) to null,
                    Pair(
                        some(!"Y"),
                        !"Y"
                    ) to null,
                    Pair(
                        some(!"X"..!"XS"),
                        !"X"..!"XS"
                    ) to null,

                    // A variable and another term
                    Pair(
                        !"X",
                        int(42)
                    ) to mapOf(
                        setOf(!"X") to int(42),
                    ),
                    Pair(
                        !"X",
                        int(1337)
                    ) to mapOf(
                        setOf(!"X") to int(1337),
                    ),
                    Pair(
                        !"X",
                        real(3.14)
                    ) to mapOf(
                        setOf(!"X") to real(3.14),
                    ),
                    Pair(
                        !"X",
                        real(6.28)
                    ) to mapOf(
                        setOf(!"X") to real(6.28),
                    ),
                    Pair(
                        !"X",
                        string("A")
                    ) to mapOf(
                        setOf(!"X") to string("A"),
                    ),
                    Pair(
                        !"X",
                        string("B")
                    ) to mapOf(
                        setOf(!"X") to string("B"),
                    ),
                    Pair(
                        !"X",
                        list()
                    ) to mapOf(
                        setOf(!"X") to list(),
                    ),
                    Pair(
                        !"X",
                        list(int(1))
                    ) to mapOf(
                        setOf(!"X") to list(int(1)),
                    ),
                    Pair(
                        !"X",
                        list(int(1), int(42))
                    ) to mapOf(
                        setOf(!"X") to list(int(1), int(42)),
                    ),
                    Pair(
                        !"X",
                        list(int(1), int(1337))
                    ) to mapOf(
                        setOf(!"X") to list(int(1), int(1337)),
                    ),
                    Pair(
                        !"X",
                        list(!"X")
                    ) to null,
                    Pair(
                        !"X",
                        list(!"X", int(42))
                    ) to null,
                    Pair(
                        !"X",
                        list(!"X", int(1337))
                    ) to null,
                    Pair(
                        !"X",
                        none()
                    ) to mapOf(
                        setOf(!"X") to none(),
                    ),
                    Pair(
                        !"X",
                        some(int(1))
                    ) to mapOf(
                        setOf(!"X") to some(int(1)),
                    ),
                    Pair(
                        !"X",
                        some(!"X")
                    ) to null,
                    Pair(
                        !"X",
                        !"X"
                    ) to emptyMap(),
                    Pair(
                        !"X",
                        !"Y"
                    ) to mapOf(
                        setOf(!"X") to !"Y",
                    ),
                    Pair(
                        !"X",
                        !"X"..!"XS"
                    ) to null,

                    // A list of a variable head and a variable tail, and another term
                    Pair(
                        !"X"..!"XS",
                        int(42)
                    ) to null,
                    Pair(
                        !"X"..!"XS",
                        int(1337)
                    ) to null,
                    Pair(
                        !"X"..!"XS",
                        real(3.14)
                    ) to null,
                    Pair(
                        !"X"..!"XS",
                        real(6.28)
                    ) to null,
                    Pair(
                        !"X"..!"XS",
                        string("A")
                    ) to null,
                    Pair(
                        !"X"..!"XS",
                        string("B")
                    ) to null,
                    Pair(
                        !"X"..!"XS",
                        list()
                    ) to null,
                    Pair(
                        !"X"..!"XS",
                        list(int(1))
                    ) to mapOf(
                        setOf(!"X") to int(1),
                        setOf(!"XS") to list(),
                    ),
                    Pair(
                        !"X"..!"XS",
                        list(!"X"..!"XS")
                    ) to null,
                    Pair(
                        !"X"..!"XS",
                        list(int(1), int(42))
                    ) to mapOf(
                        setOf(!"X") to int(1),
                        setOf(!"XS") to list(int(42)),
                    ),
                    Pair(
                        !"X"..!"XS",
                        list(int(1), int(1337))
                    ) to mapOf(
                        setOf(!"X") to int(1),
                        setOf(!"XS") to list(int(1337)),
                    ),
                    Pair(
                        !"X"..!"XS",
                        list(!"X"..!"XS", int(42))
                    ) to null,
                    Pair(
                        !"X"..!"XS",
                        list(!"X"..!"XS", int(1337))
                    ) to null,
                    Pair(
                        !"X"..!"XS",
                        none()
                    ) to null,
                    Pair(
                        !"X"..!"XS",
                        some(int(1))
                    ) to null,
                    Pair(
                        !"X"..!"XS",
                        some(!"X"..!"XS")
                    ) to null,
                    Pair(
                        !"X"..!"XS",
                        !"X"
                    ) to null,
                    Pair(
                        !"X"..!"XS",
                        !"Y"
                    ) to mapOf(
                        setOf(!"Y") to (!"X"..!"XS")
                    ),
                    Pair(
                        !"X"..!"XS",
                        !"X"..!"XS"
                    ) to emptyMap(),

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

//    test("test 1") {
//        withTermBuilder(testTermBuilder) {
//            // Arrange
//            val (left, right) = Pair<Term, Term>(
//                !"X",
//                none()
//            )
//            val substitution = null
//
//            // Act
//            val result = unify(left, right)
//
//            // Assert
//            result?.toMap() shouldBe substitution
//        }
//    }

})