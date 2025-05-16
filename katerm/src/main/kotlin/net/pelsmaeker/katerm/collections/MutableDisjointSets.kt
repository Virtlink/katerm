package net.pelsmaeker.katerm.collections

/**
 * A mutable data structure of disjoint sets.
 *
 * @param E The type of the elements in the disjoint sets.
*/
interface MutableDisjointSets<E> : DisjointSets<E> {

    /**
     * Merges the two sets that contain the given elements.
     *
     * @param left An element of the first set to merge.
     * @param right An element of the second set to merge.
     * @return `true` if the sets were merged; otherwise, `false` if the sets are already merged.
     */
    fun union(left: E, right: E): Boolean

    /**
     * Clears all sets from the data structure.
     */
    fun clear()

}