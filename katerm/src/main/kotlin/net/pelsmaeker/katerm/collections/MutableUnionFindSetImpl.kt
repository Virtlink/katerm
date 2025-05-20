package net.pelsmaeker.katerm.collections

/**
 * An implementation of [MutableDisjointSet] using a union-find algorithm.
 */
internal class MutableUnionFindSetImpl<E> : MutableDisjointSet<E> {

    /** The underlying union-find data structure. */
    private val unionFind: MutableUnionFind<E> = MutableUnionFind()

    override val representatives: Set<E> get() = unionFind.roots

    override val elements: Set<E> get() = unionFind.elements

    override fun find(element: E): E? {
        return unionFind.find(element)
    }

    override fun add(element: E): Boolean {
        return unionFind.add(element)
    }

    override fun union(left: E, right: E): Boolean {
        return unionFind.union(left, right)
    }

    override fun clear() {
        unionFind.clear()
    }

    override fun toString(): String {
        val sets = toSets()
        val setsString = if (sets.isNotEmpty()) sets.joinToString(", ") else "∅"
        return "MutableDisjointSet($setsString)"
    }
}