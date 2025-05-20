package net.pelsmaeker.katerm.collections


/**
 * Returns a new empty disjoint sets data structure.
 *
 * @param E The type of the elements in the disjoint sets.
 * @return An empty disjoint sets data structure.
 */
fun <E> emptyDisjointSet(): PersistentDisjointSet<E> =
    TODO()
//    @Suppress("UNCHECKED_CAST")
//    PersistentUnionFindSetImpl.EMPTY as PersistentUnionFindSetImpl<E>



/**
 * Returns a new disjoint sets data structure from the given sets.
 *
 * @param E The type of the elements in the disjoint sets.
 * @param sets The sets of disjoint sets of elements to include in the data structure.
 * @return A new disjoint sets data structure with the given sets.
 */
fun <E> disjointSetOf(sets: Iterable<Set<E>>): ImmutableDisjointSet<E> {
    return mutableDisjointSetOf(sets).toImmutableDisjointSet()
}

/**
 * Returns a new mutable disjoint sets data structure from the given sets.
 *
 * @param E The type of the elements in the disjoint sets.
 * @param sets The sets of disjoint sets of elements to include in the data structure.
 * @return A new mutable disjoint sets data structure with the given sets.
 */
fun <E> mutableDisjointSetOf(sets: Iterable<Set<E>>): MutableDisjointSet<E> {
    val disjointSet = MutableUnionFindSetImpl<E>()

    addDisjointSetsTo(sets, disjointSet)

    return disjointSet
}

/**
 * Returns a new persistent disjoint sets data structure from the given sets.
 *
 * @param E The type of the elements in the disjoint sets.
 * @param sets The sets of disjoint sets of elements to include in the data structure.
 * @return A new persistent disjoint sets data structure with the given sets.
 */
fun <E> persistentDisjointSetOf(sets: Iterable<Set<E>>): PersistentDisjointSet<E> {
    val builder = emptyDisjointSet<E>().builder()

    addDisjointSetsTo(sets, builder)

    return builder.build()
}

/**
 * Adds the given sets to the disjoint set.
 *
 * @param E The type of the elements in the disjoint sets.
 * @param sets The sets of disjoint sets of elements to add to the disjoint set.
 * @return The disjoint set with the given sets added.
 */
private fun <E> addDisjointSetsTo(
    sets: Iterable<Set<E>>,
    disjointSet: MutableDisjointSet<E>,
) {
    for (set in sets) {
        val iterator = set.iterator()
        // If the set is empty, skip it
        if (!iterator.hasNext()) continue
        // Take the first element as the root
        val root = iterator.next()
        // Add the root to the map and ensure it is not already in the map
        check(disjointSet.add(root)) { "Sets are not disjoint, duplicated element: $root" }
        // Add the rest of the elements as children of the root
        for (element in iterator) {
            // Add the element to the map and ensure it is not already in the map
            check(disjointSet.add(element)) { "Sets are not disjoint, duplicated element: $element" }
            // Unify the sets
            disjointSet.union(root, element)
        }
    }
}


/**
 * Converts a read-only or mutable substitution to an immutable one.
 * If the receiver is already immutable, it is returned as-is.
 *
 * @param E The type of the elements in the disjoint sets.
 * @receiver The substitution for which to return an immutable version.
 * @return The immutable substitution.
 */
fun <E> DisjointSet<E>.toImmutableDisjointSet(): ImmutableDisjointSet<E> {
    if (this is ImmutableDisjointSet) return this
    return toPersistentDisjointSet()
}

/**
 * Converts a read-only or mutable substitution to a persistent one.
 * If the receiver is already persistent, it is returned as-is.
 *
 * @param E The type of the elements in the disjoint sets.
 * @receiver The substitution for which to return a persistent version.
 * @return The persistent substitution.
 */
fun <E> DisjointSet<E>.toPersistentDisjointSet(): PersistentDisjointSet<E> {
    if (this is PersistentDisjointSet) return this
    return persistentDisjointSetOf(this.toSets())
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
fun <E> DisjointSet<E>.toMutableDisjointSet(): MutableDisjointSet<E> {
    return mutableDisjointSetOf(this.toSets())
}