package net.pelsmaeker.katerm.terms

import net.pelsmaeker.katerm.TermKind
import net.pelsmaeker.katerm.attachments.TermAttachments
import java.util.Objects

/**
 * A value term.
 *
 * This replaces the BLOB type of term from the standard ATerm library.
 * When representing a custom value, it is preferred to represent it as an [ApplTerm].
 * When that's not possible, implement this [ValueTerm] class instead.
 * The value should be immutable.
 *
 * @property termAttachments The attachments of the term.
 */
abstract class ValueTerm protected constructor(
    override val termAttachments: TermAttachments,
) : Term {

    /** The value of the term. */
    abstract val value: Any

    abstract override val termKind: TermKind

    final override val termChildren: List<Term> get() = emptyList()

    final override val termVars: Set<TermVar> get() = emptySet()

    final override val isTermVar: Boolean get() = false

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true                     // Identity equality
        val that = other as? Term ?: return false           // Must be a Term
        return equals(that, compareSubterms = true, compareAttachments = true)
    }

    final override fun equals(that: Term, compareSubterms: Boolean, compareAttachments: Boolean): Boolean {
        if (that::class.java != this::class.java) return false
        that as ValueTerm
        if (this.hashCode(compareSubterms, compareAttachments) != that.hashCode(compareSubterms, compareAttachments)) return false
        if (compareAttachments && (this.termAttachments != that.termAttachments)) return false
        return equalValues(that)
    }

    /**
     * Determines whether this value term and the given value term represent the same value.
     *
     * @return `true` if the value terms represent the same value; otherwise, `false`.
     */
    abstract fun equalValues(that: ValueTerm): Boolean

    final override fun hashCode(): Int = hashCode(compareSubterms = true, compareAttachments = true)

    final override fun hashCode(compareSubterms: Boolean, compareAttachments: Boolean): Int {
        return Objects.hash(
            this::class.java,
            valueHashCode(),
            if (compareAttachments) termAttachments else null
        )
    }

    /**
     * Computes a hash code value for the value of the term.
     *
     * If [equalValues] returns `true` for two value terms,
     * then this method must return the same hash code value for both values.
     *
     * @return A hash code value for the value of the term.
     */
    protected abstract fun valueHashCode(): Int

    override fun toString(): String {
        return value.toString()
    }
}