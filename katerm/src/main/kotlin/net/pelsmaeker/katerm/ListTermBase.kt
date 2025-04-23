package net.pelsmaeker.katerm

import java.util.Objects

/**
 * Base class for [ListTerm] implementations.
 *
 * @property E The type of the elements in the list.
 * @property termAttachments The term attachments associated with this term.
 */
abstract class ListTermBase<E: Term>(
    termAttachments: TermAttachments = TermAttachments.empty(),
): ListTerm<E>, TermBase(termAttachments) {

    /**
     * Creates a copy of this term with the specified new attachments.
     *
     * Calling this method can be more efficient than deconstructing and rebuilding a term.
     *
     * @param newAttachments The new attachments of the term.
     * @return The copy of the term, but with the new attachments.
     */
    abstract override fun withAttachments(newAttachments: TermAttachments): ListTermBase<E>

    final override fun equals(that: TermBase): Boolean {
        if (that !is ListTermBase<E>) return false
        return equalSubterms(that)
    }

    /**
     * Checks whether this term and the given term have equal subterms.
     *
     * @param that The term to check.
     * @return `true` if this term has the same subterms as the specified term; otherwise, `false`.
     */
    protected fun equalSubterms(that: ListTermBase<E>): Boolean {
        return this.termChildren == that.termChildren
    }
}