package net.pelsmaeker.katerm.collections

import kotlin.collections.set

/**
 * An in-place mutable implementation of union-find.
 *
 * @property rootRanks Maps a root element to its rank, which is a heuristic for the maximum height of its tree (counting edges).
 * The actual height of the tree can be less. The rank is only updated when merging two trees of the same rank.
 * This map contains only entries for those element that are root elements.
 * @property parents Maps each element to its parent element. This map contains only entries
 * for those element that are not root element.
 */
internal class MutableUnionFind<E>(
    private val rootRanks : MutableMap<E, Int> = mutableMapOf(),     // Only roots
    private val parents : MutableMap<E, E> = mutableMapOf(),         // Only non-roots
) {

    /** The roots in this union-find. */
    val roots: Set<E> get() = rootRanks.keys

    /** The elements in this union-find. */
    val elements: Set<E> get() = rootRanks.keys + parents.keys

    /**
     * Find the representative of the set that contains the given element.
     *
     * @param element The element to find the representative of its set for.
     * @return The representative element of the element's set; or `null` when the element is not in any set.
     */
    fun find(element: E): E? {
        val root = findRepresentative(element) ?: return null
        compressPath(element, root)
        return root
    }

    /**
     * Finds the representative of the given element.
     *
     * @param element The element to find the representative of.
     * @return The representative element of the element's set; or `null` if the element is not in any set.
     */
    private fun findRepresentative(element: E): E? {
        var current = element
        var parent = parents[current]
        while (parent != null) {
            current = parent
            parent = parents[current]
        }
        return if (rootRanks.contains(current)) current else null
    }

    /**
     * Compresses the path from the given element to the root of its set.
     *
     * This method assumes that [element] is in a set (might be the representative itself)
     * and [root] is the representative of that set.
     *
     * @param element The element to compress the path for.
     * @param root The root of the set to compress the path to.
     */
    private fun compressPath(element: E, root: E) {
        var current = element
        var parent = parents[current]
        while (parent != null) {
            parents[current] = root
            current = parent
            parent = parents[current]
        }
    }

    /**
     * Adds a new set with the element as its representative and only member.
     *
     * @param element The element to add into its own set.
     * @return `true` when the element was added; otherwise, `false` if the element was already in a set.
     */
    fun add(element: E): Boolean {
        val repr = find(element)

        // Is the element already in a set?
        if (repr != null) return false

        rootRanks[element] = 0
        return true
    }

    /**
     * Merges the two sets that contain the given elements.
     *
     * @param left An element of the first set to merge.
     * @param right An element of the second set to merge.
     * @param onNewSet A callback that is called when a new set is added.
     * @param onMergeSetsInto A callback that is called when the first set is merged into the second set.
     * @return `true` if the sets were merged; otherwise, `false` if the sets are already merged.
     */
    fun union(
        left: E,
        right: E,
        onNewSet: (E) -> Unit = { _ -> },
        onMergeSetsInto: (E, E) -> Unit = { _, _ -> },
    ): Boolean {
        val leftRepr = find(left)
        val rightRepr = find(right)

        if (leftRepr != null && rightRepr != null) {
            // Either both elements are in the same set, so they are already merged
            if (leftRepr == rightRepr) return false

            // Or both elements are in different sets, so we merge them
            val leftRank = rootRanks[leftRepr]!!
            val rightRank = rootRanks[rightRepr]!!

            when {
                leftRank < rightRank -> {
                    // Right is deeper, so make it the parent of left
                    parents[leftRepr] = rightRepr
                    onMergeSetsInto(leftRepr, rightRepr)
                    rootRanks.remove(leftRepr)
                }
                leftRank > rightRank -> {
                    // Left is deeper, so make it the parent of right
                    parents[rightRepr] = leftRepr
                    onMergeSetsInto(rightRepr, leftRepr)
                    rootRanks.remove(rightRepr)
                }
                else -> {
                    // Both are of the same rank, so make left a parent of the right and increment its rank
                    parents[rightRepr] = leftRepr
                    onMergeSetsInto(rightRepr, leftRepr)
                    rootRanks.remove(rightRepr)
                    rootRanks[leftRepr] = leftRank + 1
                }
            }
            return true
        } else if (leftRepr != null) {
            // Only left is in a set, so we add right to that set
            if (rootRanks[leftRepr]!! == 0) {
                rootRanks[leftRepr] = 1
            }
            parents[right] = leftRepr
            return true
        } else if (rightRepr != null) {
            // Only right is in a set, so we add left to that set
            if (rootRanks[rightRepr]!! == 0) {
                rootRanks[rightRepr] = 1
            }
            parents[left] = rightRepr
            return true
        } else {
            // Neither element is in a set, so we add both to a new set
            rootRanks[left] = 1
            parents[right] = left
            onNewSet(left)
            return true
        }
    }

    /**
     * Clears all sets from the data structure.
     */
    fun clear() {
        parents.clear()
        rootRanks.clear()
    }
}