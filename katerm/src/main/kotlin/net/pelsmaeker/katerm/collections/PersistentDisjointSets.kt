package net.pelsmaeker.katerm.collections

import net.pelsmaeker.katerm.Term
import net.pelsmaeker.katerm.TermVar


/**
 * A persistent data structure of disjoint sets.
 *
 * @param E The type of the elements in the disjoint sets.
 */
interface PersistentDisjointSets<E> : ImmutableDisjointSets<E> {

    /**
     * Merges the two sets that contain the given elements.
     *
     * @param left An element of the first set to merge.
     * @param right An element of the second set to merge.
     * @return A new disjoint sets instance with the merged sets; or the same instance if the sets are already merged.
     */
    fun union(left: E, right: E): PersistentDisjointSets<E>

    /**
     * Clears all sets from the data structure.
     *
     * @return A new empty disjoint sets instance.
     */
    fun clear(): PersistentDisjointSets<E>

    /**
     * Returns a new builder with the same contents as this disjoint sets data structure.
     *
     * The builder can be used to efficiently perform multiple modification operations.
     */
    fun builder(): Builder<E>

    /**
     * A builder of the persistent disjoint sets data structure.
     * The builder exposes its modification operations through the [MutableDisjointSets] interface.
     *
     * Builders are reusable, that is [build] method can be called multiple times with modifications between these calls.
     * However, applied modifications do not affect previously built persistent collection instances.
     */
    interface Builder<E>: MutableDisjointSets<E> {

        /**
         * Builds the persistent disjoint sets data structure with the same contents as this builder.
         *
         * This method can be applied multiple times.
         *
         * @return The built persistent disjoint sets data structure;
         * or the same instance as returned previously if no modifications were made;
         * or the same instance as this builder was based on if no modifications were made and [build] was not previously called.
         */
        fun build(): PersistentDisjointSets<E>
    }

}
