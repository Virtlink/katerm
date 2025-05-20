package net.pelsmaeker.katerm.collections

/**
 * An immutable data structure of disjoint sets mapping to a value.
 *
 * @param E The type of the elements in the disjoint sets.
 * @param V The type of the values each disjoint set is mapped to.
 */
interface ImmutableDisjointMap<E, out V> : DisjointMap<E, V>