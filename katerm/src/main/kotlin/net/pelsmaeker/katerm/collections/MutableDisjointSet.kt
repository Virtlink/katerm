package net.pelsmaeker.katerm.collections

/**
 * A mutable data structure of disjoint sets.
 *
 * @param E The type of the elements in the disjoint sets.
*/
interface MutableDisjointSet<E> : DisjointSet<E> {

    /**
     * Adds the given element as its own set.
     *
     * If the element is already part of a set, it is ignored.
     *
     * @param element The element to add as its own set.
     * @return `true` if the element was added; otherwise, `false` if the element was already in a set.
     */
    fun add(element: E): Boolean

    /**
     * Merges the two sets that contain the given elements.
     *
     * If the element is not part of this map, it is added in its own set.
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