package net.pelsmaeker.katerm.collections

import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.collections.shouldBeOneOf
import io.kotest.matchers.shouldBe

fun testDisjointSetsTests(
    newDisjointSet: (Set<Set<String>>) -> DisjointSets<String>,
) = funSpec {

    context("isEmpty()") {
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

    context("isNotEmpty()") {
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

    context("find()") {
        test("shoudl return null, when empty") {
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

    context("contains()") {
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

    context("inSameSet()") {
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

    context("toSets()") {
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

}
