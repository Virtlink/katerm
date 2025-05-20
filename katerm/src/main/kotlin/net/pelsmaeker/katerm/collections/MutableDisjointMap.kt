package net.pelsmaeker.katerm.collections

/**
 * A mutable data structure of disjoint sets.
 *
 * @param E The type of the elements in the disjoint sets.
 * @param V The type of the values each disjoint set is mapped to.
*/
interface MutableDisjointMap<E, V> : DisjointMap<E, V> {

    /**
     * Sets the value that the set that contains the specified element is mapped to.
     *
     * If the element is not part of this map, it is added in its own set.
     * Is it possible for a set to be mapped to a `null` value.
     *
     * @param element The element whose set to change the mapping of.
     * @param value The value to associate with the set that contains the specified element.
     * @return The value associated with the previous set that contained the key,
     * or `null` if the element is not in any set.
     */
    operator fun set(element: E, value: V): V?

    /**
     * Clears all sets from the data structure.
     */
    fun clear()

    /**
     * Merges the two sets that contain the given elements.
     *
     * When one or both of the elements don't exist in any set, they are added.
     * Two sets that map to the same value do not imply that the sets are the same.
     *
     * @param left An element of the first set to merge.
     * @param right An element of the second set to merge.
     * @param default Function that provides a default value to use when no value is specified.
     * @param unify Function that unifies the values that each of the sets is mapped to.
     * @return `true` if the sets were merged; otherwise, `false` if the sets are already merged.
     */
    fun union(left: E, right: E, default: () -> V, unify: (V, V) -> V): Boolean

    /**
     * Computes the value associated with the set that includes the given element.
     *
     * @param element The element of a set to add/change the mapping of.
     * @param mapping The mapping from the representative element of the set and its existing value
     * (or `null` when it doesn't exist) to a new value.
     * @return The computed value of the set that includes the given element.
     */
    fun compute(element: E, mapping: (E, V?) -> V): V

    /**
     * Computes the value if the set that includes the given element does already have an associated value.
     *
     * @param element The element of a set to change the mapping of.
     * @param mapping The mapping from the representative element of the set and its existing value to a new value.
     * @return The computed value of the set that includes the given element; or `null` when it was not present.
     */
    fun computeIfPresent(element: E, mapping: (E, V) -> V): V?

    /**
     * Computes the value if the set that includes the given element does not already have an associated value.
     *
     * @param element The element of a set to add the mapping of.
     * @param mapping The mapping from the representative element of the set to a value.
     * @return The computed value of the set that includes the given element.
     */
    fun computeIfAbsent(element: E, mapping: (E) -> V): V

}