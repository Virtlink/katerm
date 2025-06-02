package net.pelsmaeker.katerm.annotations

import net.pelsmaeker.katerm.terms.Term

/**
 * Holds a set of term annotations for a term.
 */
interface TermAnnotations: Set<Term> {

    companion object {
        /** Gets an empty term annotations object. */
        fun empty(): TermAnnotations = EmptyTermAnnotations

        /** Gets an empty term annotations object. */
        fun of(): TermAnnotations = EmptyTermAnnotations

        /** Gets a term annotations object with the specified term. */
        fun of(annotation: Term): TermAnnotations = SingletonTermAnnotations(annotation)

        /** Gets a term annotations object with the specified terms. */
        fun of(vararg annotations: Term): TermAnnotations = from(annotations.asList())

        /** Gets a term annotations object from the specified iterable of terms. */
        fun from(annotations: Iterable<Term>): TermAnnotations {
            val distinctAnnotations = annotations.toSet() // Also creates a safety copy
            return when (distinctAnnotations.size) {
                0 -> EmptyTermAnnotations
                1 -> SingletonTermAnnotations(distinctAnnotations.first())
                else -> MultiTermAnnotations(distinctAnnotations);
            }
        }
    }

    // This is a `private object` so that there is always just one instance.
    // This is important because it is the default value for [Term.termAttachments]

    /** An empty term annotations object. */
    private object EmptyTermAnnotations: AbstractSet<Term>(), TermAnnotations {
        override val size: Int get() = 0
        override fun iterator(): Iterator<Term> = emptyList<Term>().iterator()
    }

    /** A singleton term annotations object. */
    private data class SingletonTermAnnotations(
        private val attachment: Term
    ): AbstractSet<Term>(), TermAnnotations {
        override val size: Int get() = 1
        override fun iterator(): Iterator<Term> = iterator {
            yield(attachment)
        }
    }

    /** A multiple term annotations object. */
    private class MultiTermAnnotations(
        private val annotations: Set<Term>
    ): AbstractSet<Term>(), TermAnnotations {
        override val size: Int get() = annotations.size
        override fun iterator(): Iterator<Term> = annotations.iterator()
        // TODO: Override more methods to call [annotations] method's directly for better performance
    }
}