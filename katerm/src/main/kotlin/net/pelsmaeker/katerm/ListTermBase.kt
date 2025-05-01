package net.pelsmaeker.katerm

import net.pelsmaeker.katerm.attachments.TermAttachments
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