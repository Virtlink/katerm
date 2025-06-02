package net.pelsmaeker.katerm.collections

import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.assume
import io.kotest.property.checkAll
import io.kotest.property.arbitrary.set
import io.kotest.property.arbitrary.string

fun testDisjointSetTests(
    newDisjointSet: (Set<Set<String>>) -> DisjointSet<String>,
) = funSpec {

    context("isEmpty") {
        test("should return true, when empty") {
            // Arrange
            val disjointSet = newDisjointSet(emptySet())

            // Act/Assert
            disjointSet.isEmpty() shouldBe true
        }

        test("should return false, when not empty") {
            // Arrange
            val disjointSet = newDisjointSet(setOf(setOf("a", "b")))

            // Act/Assert
            disjointSet.isEmpty() shouldBe false
        }
    }

    context("isNotEmpty") {
        test("should return true, when not empty") {
            // Arrange
            val disjointSet = newDisjointSet(setOf(setOf("a", "b")))

            // Act/Assert
            disjointSet.isNotEmpty() shouldBe true
        }

        test("should return false, when empty") {
            // Arrange
            val disjointSet = newDisjointSet(emptySet())

            // Act/Assert
            disjointSet.isNotEmpty() shouldBe false
        }
    }

    context("size") {
        test("should return 0, when empty") {
            // Arrange
            val disjointSet = newDisjointSet(emptySet())

            // Act/Assert
            disjointSet.size shouldBe 0
        }

        test("should return the number of sets, when not empty") {
            // Arrange
            val disjointSet = newDisjointSet(setOf(setOf("a", "b"), setOf("c")))

            // Act/Assert
            disjointSet.size shouldBe 2
        }
    }

    context("representatives") {
        test("should return empty set, when empty") {
            // Arrange
            val disjointSet = newDisjointSet(emptySet())

            // Act/Assert
            disjointSet.representatives shouldBe emptySet()
        }

        test("should return the representatives of the sets") {
            // Arrange
            val sets = setOf(setOf("a", "b"), setOf("c"))
            val disjointSet = newDisjointSet(sets)
            val representatives = sets.map { s -> disjointSet.find(s.first())!! }.toSet()

            // Act/Assert
            disjointSet.representatives shouldBe representatives
        }
    }

    context("elements") {
        test("should return empty set, when empty") {
            // Arrange
            val disjointSet = newDisjointSet(emptySet())

            // Act/Assert
            disjointSet.elements shouldBe emptySet()
        }

        test("should return all elements in the sets") {
            // Arrange
            val sets = setOf(setOf("a", "b"), setOf("c"))
            val disjointSet = newDisjointSet(sets)
            val elements = sets.flatten().toSet()

            // Act/Assert
            disjointSet.elements shouldBe elements
        }
    }

    context("find") {
        test("should return null, when empty") {
            // Arrange
            val disjointSet = newDisjointSet(emptySet())

            // Act/Assert
            disjointSet.find("a") shouldBe null
        }

        test("should return null, when element is not in any set") {
            // Arrange
            val disjointSet = newDisjointSet(setOf(setOf("a", "b")))

            // Act/Assert
            disjointSet.find("c") shouldBe null
        }

        test("should return the element itself, when element is a representative of a set") {
            // Arrange
            val sets = setOf(setOf("a", "b"), setOf("c"))
            val disjointSet = newDisjointSet(sets)
            val repr = disjointSet.find("a")!!

            // Act/Assert
            disjointSet.find(repr) shouldBe repr
            disjointSet.find("c") shouldBe "c"
        }

        test("should return the representative, when the element is not a representative but is in a set") {
            // Arrange
            val firstSet = setOf("a", "b")
            val sets = setOf(firstSet, setOf("c"))
            val disjointSet = newDisjointSet(sets)
            val repr = disjointSet.find("a")!!
            val nonRepr = firstSet.first { it != repr }

            // Act/Assert
            disjointSet.find(nonRepr) shouldBe repr
        }
    }

    context("contains") {
        test("should return false, when empty") {
            // Arrange
            val disjointSet = newDisjointSet(emptySet())

            // Act/Assert
            disjointSet.contains("a") shouldBe false
        }

        test("should return false, when element is not in any set") {
            // Arrange
            val disjointSet = newDisjointSet(setOf(setOf("a", "b")))

            // Act/Assert
            disjointSet.contains("c") shouldBe false
        }

        test("should return true, when element is a representative of a set") {
            // Arrange
            val sets = setOf(setOf("a", "b"), setOf("c"))
            val disjointSet = newDisjointSet(sets)
            val repr = disjointSet.find("a")!!

            // Act/Assert
            disjointSet.contains(repr) shouldBe true
            disjointSet.contains("c") shouldBe true
        }

        test("should return true, when the element is not a representative but is in a set") {
            // Arrange
            val firstSet = setOf("a", "b")
            val sets = setOf(firstSet, setOf("c"))
            val disjointSet = newDisjointSet(sets)
            val repr = disjointSet.find("a")!!
            val nonRepr = firstSet.first { it != repr }

            // Act/Assert
            disjointSet.contains(nonRepr) shouldBe true
        }
    }

    context("inSameSet") {
        test("should return false, when empty") {
            // Arrange
            val disjointSet = newDisjointSet(emptySet())

            // Act/Assert
            disjointSet.inSameSet("a", "b") shouldBe false
        }

        test("should return false, when elements are not in any set") {
            // Arrange
            val disjointSet = newDisjointSet(setOf(setOf("a", "b")))

            // Act/Assert
            disjointSet.inSameSet("c", "d") shouldBe false
        }

        test("should return true, when both elements are in the same set") {
            // Arrange
            val sets = setOf(setOf("a", "b"), setOf("c"))
            val disjointSet = newDisjointSet(sets)
            val repr1 = disjointSet.find("a")!!
            val repr2 = disjointSet.find("b")!!

            // Act/Assert
            disjointSet.inSameSet(repr1, repr2) shouldBe true
        }

        test("should return true, when both elements are not representatives but are in the same set") {
            // Arrange
            val firstSet = setOf("a", "b")
            val sets = setOf(firstSet, setOf("c"))
            val disjointSet = newDisjointSet(sets)
            val repr = disjointSet.find("a")!!
            val nonRepr1 = firstSet.first { it != repr }
            val nonRepr2 = firstSet.first { it != nonRepr1 }

            // Act/Assert
            disjointSet.inSameSet(nonRepr1, nonRepr2) shouldBe true
        }

        test("should return false, when elements are in different sets") {
            // Arrange
            val sets = setOf(setOf("a", "b"), setOf("c"))
            val disjointSet = newDisjointSet(sets)

            // Act/Assert
            disjointSet.inSameSet("a", "c") shouldBe false
        }
    }

    context("toSets") {
        test("should return empty set, when empty") {
            // Arrange
            val disjointSet = newDisjointSet(emptySet())

            // Act/Assert
            disjointSet.toSets() shouldBe emptySet()
        }

        test("should return the sets") {
            // Arrange
            val sets = setOf(setOf("a", "b"), setOf("c"))
            val disjointSet = newDisjointSet(sets)

            // Act/Assert
            disjointSet.toSets() shouldBe sets
        }
    }

    context("property-based tests") {
        test("all elements in toSets are contained in elements") {
            checkAll(Arb.set(Arb.set(Arb.string(1..3), 1..4), 0..5)) { sets ->
                // Skip test cases where sets are not disjoint
                assume(areDisjoint(sets))

                val ds = newDisjointSet(sets)
                val allElements = ds.toSets().flatten().toSet()
                ds.elements shouldBe allElements
            }
        }

        test("find returns null for elements not in any set") {
            checkAll(
                Arb.set(Arb.set(Arb.string(1..3), 1..4), 0..5),
                Arb.string(1..3)
            ) { sets, element ->
                // Skip test cases where sets are not disjoint
                assume(areDisjoint(sets))

                val ds = newDisjointSet(sets)
                if (sets.flatten().none { it == element }) {
                    ds.find(element) shouldBe null
                }
            }
        }

        test("inSameSet is reflexive for all elements") {
            checkAll(Arb.set(Arb.set(Arb.string(1..3), 1..4), 0..5)) { sets ->
                // Skip test cases where sets are not disjoint
                assume(areDisjoint(sets))

                val ds = newDisjointSet(sets)
                ds.elements.forEach { e ->
                    ds.inSameSet(e, e) shouldBe true
                }
            }
        }

        test("inSameSet is symmetric for all elements") {
            checkAll(Arb.set(Arb.set(Arb.string(1..3), 2..4), 1..3)) { sets ->
                // Skip test cases where sets are not disjoint
                assume(areDisjoint(sets))

                val ds = newDisjointSet(sets)
                val elems = ds.elements.toList()
                for (i in elems.indices) {
                    for (j in elems.indices) {
                        ds.inSameSet(elems[i], elems[j]) shouldBe ds.inSameSet(elems[j], elems[i])
                    }
                }
            }
        }
    }
}

// Helper function to check if sets are disjoint (no element appears in multiple sets)
private fun areDisjoint(sets: Set<Set<String>>): Boolean {
    val seen = mutableSetOf<String>()
    for (set in sets) {
        for (element in set) {
            if (element in seen) return false
            seen.add(element)
        }
    }
    return true
}
