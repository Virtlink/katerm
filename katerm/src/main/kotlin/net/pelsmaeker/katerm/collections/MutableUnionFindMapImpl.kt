package net.pelsmaeker.katerm.collections

/**
 * An implementation of [MutableDisjointSet] using a union-find algorithm.
 *
 * @property mappings Maps each representative to its value.
 */
internal class MutableUnionFindMapImpl<E, V>(
    private val mappings: MutableMap<E, V> = mutableMapOf(),
) : MutableDisjointMap<E, V> {

    /** The underlying union-find data structure. */
    private val unionFind: MutableUnionFind<E> = MutableUnionFind<E>()

    override val representatives: Set<E> get() = unionFind.roots

    override val elements: Set<E> get() = unionFind.elements

    override fun find(element: E): E? {
        return unionFind.find(element)
    }

    override fun containsValue(value: V): Boolean {
        return mappings.containsValue(value)
    }

    override fun get(element: E): V? {
        TODO("Not yet implemented")
    }

    override fun getOrDefault(key: E, defaultValue: V): V {
        TODO("Not yet implemented")
    }

    override fun set(element: E, value: V): V? {
        val added = unionFind.add(element)
        val repr = if (added) element else unionFind.find(element)!!
        return mappings.put(repr, value)
    }

    override fun clear() {
        unionFind.clear()
        mappings.clear()
    }

    override fun union(left: E, right: E, default: () -> V, unify: (V, V) -> V): Boolean {
        return unionFind.union(
            left,
            right,
            onNewSet = { newRoot ->
                mappings[newRoot] = default()
            },
            onMergeSetsInto = { fromRoot, intoRoot ->
                val fromValue = mappings[fromRoot]
                val intoValue = mappings[intoRoot]
                if (fromValue != null && intoValue != null) {
                    mappings[intoRoot] = unify(fromValue, intoValue)
                }
            }
        )
    }

    override fun compute(element: E, mapping: (E, V?) -> V): V {
        TODO("Not yet implemented")
    }

    override fun computeIfPresent(element: E, mapping: (E, V) -> V): V? {
        TODO("Not yet implemented")
    }

    override fun computeIfAbsent(element: E, mapping: (E) -> V): V {
        TODO("Not yet implemented")
    }

    override fun toString(): String {
        val sets = toSets()
        val setsString = if (sets.isNotEmpty()) sets.joinToString(", ") else "∅"
        return "MutableDisjointSet($setsString)"
    }
}