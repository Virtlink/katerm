package net.pelsmaeker.katerm.collections

import java.util.LinkedHashSet

/**
 * A data structure of disjoint sets mapping to a value.
 *
 * @param E The type of the elements in the disjoint sets.
 * @param V The type of the values each disjoint set is mapped to.
 */
interface DisjointMap<E, out V> : DisjointSet<E> {

    /**
     * Checks whether any set is mapped to the given value.
     *
     * @param value The value to check.
     * @return `true` if any set is mapped to the given value; otherwise, `false`.
     */
    fun containsValue(value: @UnsafeVariance V): Boolean

    /**
     * Gets the value that the set that contains the specified element is mapped to.
     *
     * Is it possible for a set to be mapped to a `null` value.
     *
     * @param element The element whose set to change the mapping of.
     * @return The value associated with the set that contains the specified element;
     * or `null` if the element is not in any set.
     */
    operator fun get(element: E): V?

    /**
     * Gets the value that the set that contains the specified element is mapped to,
     * or the default value if the key is not in any set.
     *
     * Is it possible for a set to be mapped to a `null` value,
     * which is then returned instead of the default value.
     *
     * @param element The element whose set to change the mapping of.
     * @param defaultValue The default value to return if the element is not in any set.
     * @return The value asociated with the set that contains the specified element;
     * or the default value if the element is not in any set.
     */
    fun getOrDefault(element: E, defaultValue: @UnsafeVariance V): V

    /**
     * Copies the sets from this data structure into a new set of sets.
     *
     * @return A new set of disjoint sets containing the same elements as this data structure.
     */
    fun toMap(): Map<Set<E>, V> {
        val sets = mutableMapOf<E, MutableSet<E>>()
        for (element in elements) {
            val representative = find(element) ?: continue
            sets.getOrPut(representative) { LinkedHashSet(setOf(representative)) }.add(element)
        }
        return sets.map { (repr, set) -> set to get(repr)!! }.toMap()
    }

}