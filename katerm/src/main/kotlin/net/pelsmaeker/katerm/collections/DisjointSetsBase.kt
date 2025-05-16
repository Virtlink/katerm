package net.pelsmaeker.katerm.collections

import java.util.LinkedHashSet

/**
 * Base class for implementations of [DisjointSets].
 *
 * @param E The type of the elements in the disjoint sets.
 */
abstract class DisjointSetsBase<E> : DisjointSets<E> {
    override fun isEmpty(): Boolean = size == 0

    override fun isNotEmpty(): Boolean = !isEmpty()

    override val size: Int get() = representatives.size

    abstract override val representatives: Set<E>

    abstract override val elements: Set<E>

    abstract override fun find(element: E): E?

    override fun inSameSet(left: E, right: E): Boolean {
        val leftSet = find(left) ?: return false
        val rightSet = find(right) ?: return false
        return leftSet == rightSet
    }

    override fun toSets(): Collection<Set<E>> {
        val sets = mutableMapOf<E, MutableSet<E>>()
        for (element in elements) {
            val representative = find(element) ?: continue
            sets.getOrPut(representative) { LinkedHashSet(setOf(representative)) }.add(element)
        }
        return sets.values
    }

    override fun toString(): String {
        val sets = toSets()
        val setsString = if (sets.isNotEmpty()) sets.joinToString(", ") else "∅"
        return "DisjointSets($setsString)"
    }
}