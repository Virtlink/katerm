package net.pelsmaeker.katerm.collections

/**
 * Base class for implementations of [PersistentDisjointSets].
 *
 * @param E The type of the elements in the disjoint sets.
 */
abstract class PersistentDisjointSetsBase<E> : DisjointSetsBase<E>(), PersistentDisjointSets<E> {

    abstract override val representatives: Set<E>

    abstract override val elements: Set<E>

    abstract override fun find(element: E): E?

    abstract override fun union(left: E, right: E): PersistentDisjointSets<E>

    override fun clear(): PersistentDisjointSets<E> {
        return emptyDisjointSets()
    }

    override fun builder(): PersistentDisjointSets.Builder<E> = object: DisjointSetsBase<E>(), PersistentDisjointSets.Builder<E> {

        private var current: PersistentDisjointSets<E> = this@PersistentDisjointSetsBase

        override val representatives: Set<E>
            get() = current.representatives

        override val elements: Set<E>
            get() = current.elements

        override fun find(element: E): E? {
            return current.find(element)
        }

        override fun union(left: E, right: E): Boolean {
            val next = current.union(left, right)
            if (next == current) return false
            current = next
            return true
        }

        override fun clear() {
            current = emptyDisjointSets()
        }

        override fun build(): PersistentDisjointSets<E> {
            return current
        }
    }

}