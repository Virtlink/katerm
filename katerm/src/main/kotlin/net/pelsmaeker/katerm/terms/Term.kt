package net.pelsmaeker.katerm.terms

import net.pelsmaeker.katerm.attachments.TermAttachments
import net.pelsmaeker.katerm.substitutions.OccursCheckFailedException

/**
 * A term.
 *
 * Terms are immutable. To create or change a term, use a [TermBuilder].
 */
interface Term : TermContext {

    /** A list of child terms of the term. */
    val termChildren: List<Term>

    /** The attachments of the term. */
    val termAttachments: TermAttachments

    /** The free variables that occur in the term at any depth. This can be used for an 'occurs check'. */
    override val termVars: Set<TermVar>

    /** Whether this term is a variable or a list/option variable. */
    val isTermVar: Boolean

    /**
     * Determines whether this term and its subterms represent the same value
     * as the given term and it subterms, regardless of the actual implementations
     * of the terms and its subterms.
     *
     * Note that subterms and attachments are also checked by this method.
     *
     * This method may be used in tests to assert equality.
     */
    override fun equals(other: Any?): Boolean

    /**
     * Determines whether this term and optionally its subterms represent the same value
     * as the given term and it subterms, regardless of the actual implementations
     * of the terms and its subterms.
     *
     * Implementations should compare equal to other implementations of the same term type,
     * but can take shortcuts when comparing to the same implementation of the term type.
     *
     * @param that The other term to compare to.
     * @param compareSubterms Whether to compare subterms.
     * @param compareAttachments Whether to compare the attachments.
     * @return `true` if the terms are equal (optionally modulo subterms/attachments); otherwise, `false`.
     */
    fun equals(that: Term, compareSubterms: Boolean = true, compareAttachments: Boolean = true): Boolean


    /**
     * Returns a hash code value for the term, including its subterms and attachments.
     *
     * This method may be used in tests to assert equality of terms of the same implementation.
     * If [equals] returns `true` for two terms, then this method should return the same value for both terms.
     *
     * @return A hash code value for the term, including its subterms and attachments.
     */
    override fun hashCode(): Int

    /**
     * Returns a hash code value for the term, optionally including its subterms and attachments.
     *
     * This method may be used in tests to assert equality of terms of the same implementation.
     * For a given setting of [compareSubterms] and [compareAttachments],
     * if [equals] returns `true` for two terms, then this method should return the same value for both terms.
     *
     * @param compareSubterms Whether to hash subterms.
     * @param compareAttachments Whether to hash the attachments.
     * @return A hash code value for the term, optionally including its subterms and attachments.
     */
    fun hashCode(compareSubterms: Boolean = true, compareAttachments: Boolean = true): Int

    /**
     * Accepts a term visitor.
     *
     * @param visitor The visitor to accept.
     * @return The result returned by the visitor.
     */
    fun <R> accept(visitor: TermVisitor<R>): R

    /**
     * Accepts a term visitor.
     *
     * @param visitor The visitor to accept.
     * @param arg The argument to pass to the visitor.
     * @return The result returned by the visitor.
     */
    fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R
}