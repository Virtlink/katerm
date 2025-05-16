package net.pelsmaeker.katerm.collections

import kotlinx.collections.immutable.persistentMapOf

/**
 * An implementation of [MutableDisjointSets] using a union-find algorithm.
 *
 * @property rootRanks Maps a root element to its rank, which is the number of elements it represents, including itself.
 * This map contains only entries for those element that are root elements.
 * @property parents Maps each element to its parent element. This map contains only entries
 * for those element that are not root element.
 */
class MutableUnionFindImpl<E> internal constructor(
    private val rootRanks : MutableMap<E, Int>,
    private val parents : MutableMap<E, E>,
) : DisjointSetsBase<E>(), MutableDisjointSets<E> {

    constructor() : this(mutableMapOf(), mutableMapOf())

    /**
     * Creates a new [MutableUnionFindImpl] with each of the given elements as a separate set.
     *
     * @param elements The elements to create sets for.
     */
    constructor(elements: Collection<E>) : this() {
        for (element in elements) {
            rootRanks[element] = 1
        }
    }

    /**
     * Creates a new [MutableUnionFindImpl] with the given sets as disjoint sets.
     *
     * @param sets The disjoint sets to initialize the data structure with.
     */
    constructor(sets: Set<Set<E>>) : this() {
        for (set in sets) {
            val iterator = set.iterator()
            // If the set is empty, skip it
            if (!iterator.hasNext()) continue
            // Take the first element as the root
            val root = iterator.next()
            // Add the rest of the elements as children of the root
            rootRanks.compute(root) { _, i ->
                check(i == null) { "Sets are not disjoint, duplicated element: $root" }
                set.size
            }
            for (element in iterator) {
                parents.compute(element) { _, parent ->
                    check(parent == null) { "Sets are not disjoint, duplicated element: $element" }
                    root
                }
            }
        }
    }

    override val representatives: Set<E> get() = rootRanks.keys

    override val elements: Set<E> get() = rootRanks.keys + parents.keys

    override fun find(element: E): E? {
        if (element in rootRanks) return element

        val parent = parents[element] ?: return null
        val root = find(parent)!!

        // Path compression: make the parent of the element point to the root
        parents[element] = root

        return root
    }

    override fun union(left: E, right: E): Boolean {
        val leftRepr = find(left)
        val rightRepr = find(right)
        val leftRank = rootRanks[leftRepr] ?: 1
        val rightRank = rootRanks[rightRepr] ?: 1

        if (leftRepr == null && rightRepr == null) {
            // Both elements are not in any set, so create a new set with `left` as the root
            rootRanks[left] = 2
            parents[right] = left
            return true
        } else if (leftRepr == rightRepr) {
            // Both elements are in the same set, so they are already merged
            return false
        } else if (leftRepr != null && rightRepr != null) {
            // Both elements are in different sets, so merge them
            if (leftRank > rightRank) {
                parents[rightRepr] = leftRepr           // Right now has left as its parent
                rootRanks.remove(rightRepr)     // Thus, right is no longer a root

                rootRanks[leftRepr] = leftRank + rightRank
            } else {
                parents[leftRepr] = rightRepr           // Left now has right as its parent
                rootRanks.remove(leftRepr)      // Thus, left is no longer a root

                rootRanks[rightRepr] = rightRank + leftRank
            }
            return true
        } else if (rightRepr != null) {
            // `left` is not in any set, so add it to the set of `right`
            parents[left] = rightRepr
            rootRanks[rightRepr] = rightRank + 1
            return true
        } else if (leftRepr != null) {
            // `right` is not in any set, so add it to the set of `left`
            parents[right] = leftRepr
            rootRanks[leftRepr] = leftRank + 1
            return true
        } else {
            // This can never happen
            throw IllegalStateException("Unexpected state.")
        }
    }

    override fun clear() {
        parents.clear()
        rootRanks.clear()
    }
//
//
//    companion object {
//
//        /**
//         * Creates an empty [MutableUnionFindImpl] instance.
//         *
//         * @return An empty [MutableUnionFindImpl] instance.
//         */
//        @Suppress("UNCHECKED_CAST")
//        fun <E> empty(): MutableUnionFindImpl<E> = MutableUnionFindImpl<E>()
//
//        /**
//         * Creates a new [MutableUnionFindImpl] instance from the given set of sets.
//         *
//         * @param sets The set of sets to create the [MutableUnionFindImpl] from.
//         * @return A new [MutableUnionFindImpl] instance containing the given sets.
//         * @throws IllegalArgumentException If an element occurs in multiple sets.
//         */
//        fun <E> of(sets: Iterable<Set<E>>): MutableUnionFindImpl<E> {
//            val rootRanks = mutableMapOf<E, Int>()
//            val parents = mutableMapOf<E, E>()
//
//            for (set in sets) {
//                val iterator = set.iterator()
//                // If the set is empty, skip it
//                if (!iterator.hasNext()) continue
//                // Take the first element as the root
//                val root = iterator.next()
//                // Check if the root is already in the map
//                check(root !in rootRanks && root !in parents) { "Sets are not disjoint, duplicated element: $root" }
//                // Add the rest of the elements as children of the root
//                rootRanks.put(root, set.size)
//                for (element in iterator) {
//                    // Check if the element is already in the map
//                    check(element !in rootRanks && element !in parents) { "Sets are not disjoint, duplicated element: $element" }
//                    parents.put(element, root)
//                }
//            }
//
//            return MutableUnionFindImpl(rootRanks, parents)
//        }
//    }
}