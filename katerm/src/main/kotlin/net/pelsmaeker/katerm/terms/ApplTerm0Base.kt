package net.pelsmaeker.katerm.terms

import net.pelsmaeker.katerm.attachments.TermAttachments

/**
 * A constructor application term with no arguments.
 *
 * @property termOp The constructor name.
 * @param termAttachments The attachments of the term.
 */
abstract class ApplTerm0Base protected constructor(
    override val termOp: String,
    termAttachments: TermAttachments = TermAttachments.empty(),
) : ApplTermBase(termAttachments) {

    override val termArity: Int get() = 0
    override val termArgs: List<Term> get() = emptyList()

    override fun equalSubterms(
        that: ApplTerm,
        compareAttachments: Boolean,
    ): Boolean {
        return that is ApplTerm0Base
    }

    override val subtermsHashCode: Int
        get() = 0

}