package net.pelsmaeker.katerm.collections

/**
 * An implementation of [MutableDisjointSet] using a union-find algorithm.
 *
 * @property mappings Maps each representative to its value.
 */
class MutableUnionFindMapImpl<E, V> internal constructor(
    private val mappings: MutableMap<E, V>,
) : MutableDisjointMap<E, V> {

    constructor() : this(mutableMapOf())

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
        val repr = unionFind.find(element)
        return if (repr != null) mappings[repr] else null
    }

    override fun getOrDefault(element: E, defaultValue: V): V {
        val repr = unionFind.find(element)
        return if (repr != null) mappings.getOrDefault(repr, defaultValue) else defaultValue
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
        val repr = unionFind.find(element) ?: element
        val existingValue = mappings[repr]
        val newValue = mapping(repr, existingValue)
        mappings[repr] = newValue
        return newValue
    }

    override fun computeIfPresent(element: E, mapping: (E, V) -> V): V? {
        val repr = unionFind.find(element) ?: return null
        val existingValue = mappings[repr] ?: return null
        val newValue = mapping(repr, existingValue)
        mappings[repr] = newValue
        return newValue
    }

    override fun computeIfAbsent(element: E, mapping: (E) -> V): V {
        val repr = unionFind.find(element) ?: element
        val existingValue = mappings[repr]
        if (existingValue != null) return existingValue
        val newValue = mapping(repr)
        mappings[repr] = newValue
        return newValue
    }

    override fun toString(): String {
        val map = toMap()
        return "MutableDisjointSet($map)"
    }
}