package net.pelsmaeker.katerm.collections

/**
 * A persistent data structure of disjoint sets.
 *
 * @param E The type of the elements in the disjoint sets.
 * @param V The type of the values each disjoint set is mapped to.
*/
interface PersistentDisjointMap<E, V> : ImmutableDisjointMap<E, V> {

    /**
     * Sets the [value] of the set that contains the specified [element].
     *
     * If the element is not part of this map, it is added in its own set.
     *
     * @param element The element whose set to change the mapping of.
     * @param value The value to associate with the set that contains the specified [element].
     * @return A result object with the resulting persistent map,
     * and the value associated with the previous set that contained the key,
     * or `null` if the key was not present in the map.
     */
    operator fun set(element: E, value: V): Result<E, V, V>

    /**
     * Clears all sets from the data structure.
     *
     * @return A new empty disjoint map instance.
     */
    fun clear(): PersistentDisjointMap<E, V>

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
     * @return A new disjoint map instance with the merged sets; or the same instance if the sets are already merged.
     */
    fun union(left: E, right: E, default: () -> V, unify: (V, V) -> V): PersistentDisjointMap<E, V>

    /**
     * Computes the value associated with the set that includes the given element.
     *
     * @param element The element of a set to add/change the mapping of.
     * @param mapping The mapping from the representative element of the set and its existing value
     * (or `null` when it doesn't exist) to a new value.
     * @return A result object with the resulting persistent map,
     * and the computed value of the set that includes the given key.
     */
    fun compute(element: E, mapping: (E, V?) -> V): Result<E, V, V>

    /**
     * Computes the value if the set that includes the given element does already have an associated value.
     *
     * @param element The element of a set to change the mapping of.
     * @param mapping The mapping from the representative element of the set and its existing value to a new value.
     * @return A result object with the resulting persistent map,
     * and the computed value of the set that includes the given key; or `null` when it was not present.
     */
    fun computeIfPresent(element: E, mapping: (E, V) -> V): Result<E, V, V?>

    /**
     * Computes the value if the set that includes the given element does not already have an associated value.
     *
     * @param element The element of a set to add the mapping of.
     * @param mapping The mapping from the representative element of the set to a value.
     * @return A result object with the resulting persistent map,
     * and the computed value of the set that includes the given element.
     */
    fun computeIfAbsent(element: E, mapping: (E) -> V): Result<E, V, V>

    /**
     * Returns a new builder with the same contents as this disjoint map data structure.
     *
     * The builder can be used to efficiently perform multiple modification operations.
     */
    fun builder(): Builder<E, V>

    /**
     * A builder of the persistent disjoint map data structure.
     * The builder exposes its modification operations through the [MutableDisjointMap] interface.
     *
     * Builders are reusable, that is [build] method can be called multiple times with modifications between these calls.
     * However, applied modifications do not affect previously built persistent collection instances.
     */
    interface Builder<E, V>: MutableDisjointMap<E, V> {

        /**
         * Builds the persistent disjoint map data structure with the same contents as this builder.
         *
         * This method can be applied multiple times.
         *
         * @return The built persistent disjoint map data structure;
         * or the same instance as returned previously if no modifications were made;
         * or the same instance as this builder was based on if no modifications were made and [build] was not previously called.
         */
        fun build(): PersistentDisjointMap<E, V>
    }

    /**
     * A result object.
     *
     * @property map The modified map.
     * @property value The return value of the method.
     */
    data class Result<E, V, T>(
        val map: PersistentDisjointMap<E, V>,
        val value: T,
    )
}