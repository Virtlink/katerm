package net.pelsmaeker.katerm.collections

import kotlinx.collections.immutable.toPersistentMap


/**
 * Returns a new empty disjoint sets data structure.
 *
 * @param E The type of the elements in the disjoint sets.
 * @return An empty disjoint sets data structure.
 */
fun <E> emptyDisjointSets(): PersistentDisjointSets<E> =
    @Suppress("UNCHECKED_CAST")
    PersistentUnionFindImpl.EMPTY as PersistentUnionFindImpl<E>



/**
 * Returns a new disjoint sets data structure from the given sets.
 *
 * @param E The type of the elements in the disjoint sets.
 * @param sets The sets of disjoint sets of elements to include in the data structure.
 * @return A new disjoint sets data structure with the given sets.
 */
fun <E> disjointSetsOf(sets: Iterable<Set<E>>): ImmutableDisjointSets<E> {
    return mutableDisjointSetsOf(sets).toImmutableDisjointSets()
}

/**
 * Returns a new mutable disjoint sets data structure from the given sets.
 *
 * @param E The type of the elements in the disjoint sets.
 * @param sets The sets of disjoint sets of elements to include in the data structure.
 * @return A new mutable disjoint sets data structure with the given sets.
 */
fun <E> mutableDisjointSetsOf(sets: Iterable<Set<E>>): MutableDisjointSets<E> {
    val rootRanks = mutableMapOf<E, Int>()
    val parents = mutableMapOf<E, E>()

    for (set in sets) {
        val iterator = set.iterator()
        // If the set is empty, skip it
        if (!iterator.hasNext()) continue
        // Take the first element as the root
        val root = iterator.next()
        // Check if the root is already in the map
        check(root !in rootRanks && root !in parents) { "Sets are not disjoint, duplicated element: $root" }
        // Add the rest of the elements as children of the root
        rootRanks.put(root, set.size)
        for (element in iterator) {
            // Check if the element is already in the map
            check(element !in rootRanks && element !in parents) { "Sets are not disjoint, duplicated element: $element" }
            parents.put(element, root)
        }
    }

    return MutableUnionFindImpl(rootRanks, parents)
}

/**
 * Returns a new persistent disjoint sets data structure from the given sets.
 *
 * @param E The type of the elements in the disjoint sets.
 * @param sets The sets of disjoint sets of elements to include in the data structure.
 * @return A new persistent disjoint sets data structure with the given sets.
 */
fun <E> persistentDisjointSetsOf(sets: Iterable<Set<E>>): PersistentDisjointSets<E> {
    val rootRanks = mutableMapOf<E, Int>()
    val parents = mutableMapOf<E, E>()

    for (set in sets) {
        val iterator = set.iterator()
        // If the set is empty, skip it
        if (!iterator.hasNext()) continue
        // Take the first element as the root
        val root = iterator.next()
        // Check if the root is already in the map
        check(root !in rootRanks && root !in parents) { "Sets are not disjoint, duplicated element: $root" }
        // Add the rest of the elements as children of the root
        rootRanks.put(root, set.size)
        for (element in iterator) {
            // Check if the element is already in the map
            check(element !in rootRanks && element !in parents) { "Sets are not disjoint, duplicated element: $element" }
            parents.put(element, root)
        }
    }

    return PersistentUnionFindImpl(rootRanks.toPersistentMap(), parents.toPersistentMap())
}


/**
 * Converts a read-only or mutable substitution to an immutable one.
 * If the receiver is already immutable, it is returned as-is.
 *
 * @param E The type of the elements in the disjoint sets.
 * @receiver The substitution for which to return an immutable version.
 * @return The immutable substitution.
 */
fun <E> DisjointSets<E>.toImmutableDisjointSets(): ImmutableDisjointSets<E> {
    if (this is ImmutableDisjointSets) return this
    return toPersistentDisjointSets()
}

/**
 * Converts a read-only or mutable substitution to a persistent one.
 * If the receiver is already persistent, it is returned as-is.
 *
 * @param E The type of the elements in the disjoint sets.
 * @receiver The substitution for which to return a persistent version.
 * @return The persistent substitution.
 */
fun <E> DisjointSets<E>.toPersistentDisjointSets(): PersistentDisjointSets<E> {
    if (this is PersistentDisjointSets) return this
    return persistentDisjointSetsOf(this.toSets())
}

/**
 * Returns a new mutable substitution with the same contents as the receiver.
 *
 * Modifying the returned mutable substitution does not modify the original substitution,
 * even if it was already mutable.
 *
 * @param E The type of the elements in the disjoint sets.
 * @receiver The substitution for which to return a mutable version.
 * @return The mutable substitution.
 */
fun <E> DisjointSets<E>.toMutableDisjointSets(): MutableDisjointSets<E> {
    return mutableDisjointSetsOf(this.toSets())
}