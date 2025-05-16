package net.pelsmaeker.katerm.collections

/**
 * A data structure of disjoint sets.
 *
 * @param E The type of the elements in the disjoint sets.
 */
interface DisjointSets<out E> {

    /**
     * Determines whether the data structure contains no sets.
     *
     * @return `true` if this data structure contains no sets; otherwise, `false`.
     */
    fun isEmpty(): Boolean

    /**
     * Determines whether the data structure contains any sets.
     *
     * @return `true` if this data structure contains any sets; otherwise, `false`.
     */
    fun isNotEmpty(): Boolean

    /** The number of sets in this data structure. */
    val size: Int

    /** The set of all representatives of the sets in this data structure. */
    val representatives: Set<E>

    /** The set of all elements in this data structure. */
    val elements: Set<E>

    /**
     * Find the representative of the set that contains the given element.
     *
     * @param element The element to find the representative of its set for.
     * @return The representative element of the element's set; or `null` when the element is not in any set.
     */
    fun find(element: @UnsafeVariance E): E?

    /**
     * Checks whether any set contains the given element.
     *
     * @param element The element to check.
     * @return `true` if the element is in any set; otherwise, `false`.
     */
    operator fun contains(element: @UnsafeVariance E): Boolean =
        find(element) != null

    /**
     * Checks whether two elements are in the same set.
     *
     * This method checks whether the two elements are in the same set.
     *
     * @param left The first element to check.
     * @param right The second element to check.
     * @return `true` if both elements are in the same set; otherwise, `false`.
     */
    fun inSameSet(left: @UnsafeVariance E, right: @UnsafeVariance E): Boolean

    /**
     * Copies the sets from this data structure into a new set of sets.
     *
     * @return A new set of disjoint sets containing the same elements as this data structure.
     */
    fun toSets(): Collection<Set<E>>

}