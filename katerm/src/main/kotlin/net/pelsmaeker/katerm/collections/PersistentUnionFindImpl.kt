package net.pelsmaeker.katerm.collections

import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf

/**
 * An implementation of [PersistentDisjointSets] using a union-find algorithm.
 *
 * @property rootRanks Maps a root element to its rank, which is the number of elements it represents, including itself.
 * This map contains only entries for those element that are root elements.
 * @property parents Maps each element to its parent element. This map contains only entries
 * for those element that are not root element.
 */
class PersistentUnionFindImpl<E> internal constructor(
    private val rootRanks: PersistentMap<E, Int>,
    private val parents: PersistentMap<E, E>,
) : PersistentDisjointSetsBase<E>() {

    override val representatives: Set<E> get() = rootRanks.keys

    override val elements: Set<E> get() = rootRanks.keys + parents.keys

    override fun find(element: E): E? {
        if (element in rootRanks) return element

        val parent = parents[element] ?: return null
        val root = find(parent)!!

        // FIXME: Path compression: make the parent of the element point to the root
        //  This is not allowed in a persistent data structure

        return root
    }

    override fun union(left: E, right: E): PersistentUnionFindImpl<E> {
        val leftRepr = find(left)
        val rightRepr = find(right)
        val leftRank = rootRanks[leftRepr] ?: 1
        val rightRank = rootRanks[rightRepr] ?: 1

        if (leftRepr == null && rightRepr == null) {
            // Both elements are not in any set, so create a new set with `left` as the root
            val newRootRanks = rootRanks.put(left, 2)
            val newParents = parents.put(right, left)
            return PersistentUnionFindImpl(newRootRanks, newParents)
        } else if (leftRepr == rightRepr) {
            // Both elements are in the same set, so they are already merged
            return this
        } else if (leftRepr != null && rightRepr != null) {
            // Both elements are in different sets, so merge them
            var newRootRanks = rootRanks
            var newParents = parents

            if (leftRank > rightRank) {
                newParents = newParents.put(rightRepr, leftRepr)     // Right now has left as its parent
                newRootRanks = rootRanks.remove(rightRepr)     // Thus, right is no longer a root

                newRootRanks = rootRanks.put(leftRepr, leftRank + rightRank)
            } else {
                newParents = newParents.put(leftRepr, rightRepr)           // Left now has right as its parent
                newRootRanks = newRootRanks.remove(leftRepr)      // Thus, left is no longer a root

                newRootRanks = newRootRanks.put(rightRepr, rightRank + leftRank)
            }
            return PersistentUnionFindImpl(newRootRanks, newParents)
        } else if (rightRepr != null) {
            // `left` is not in any set, so add it to the set of `right`
            val newParents = parents.put(left, rightRepr)
            val newRootRanks = rootRanks.put(rightRepr, rightRank + 1)
            return PersistentUnionFindImpl(newRootRanks, newParents)
        } else if (leftRepr != null) {
            // `right` is not in any set, so add it to the set of `left`
            val newParents = parents.put(right, leftRepr)
            val newRootRanks = rootRanks.put(leftRepr, leftRank + 1)
            return PersistentUnionFindImpl(newRootRanks, newParents)
        } else {
            // This can never happen
            throw IllegalStateException("Unexpected state.")
        }
    }

    companion object {
        internal val EMPTY: PersistentUnionFindImpl<Nothing> = PersistentUnionFindImpl(
            rootRanks = persistentMapOf(),
            parents = persistentMapOf(),
        )
    }

}