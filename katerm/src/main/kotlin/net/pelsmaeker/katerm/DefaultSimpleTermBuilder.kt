package net.pelsmaeker.katerm

import net.pelsmaeker.katerm.attachments.TermAttachments
import net.pelsmaeker.katerm.terms.ApplTerm
import net.pelsmaeker.katerm.terms.ApplTermBase
import net.pelsmaeker.katerm.terms.Term
import java.util.*

/**
 * The default simple term builder.
 */
open class SimpleTermBuilder: TermBuilderBase() {

    override fun newAppl(op: String, args: List<Term>, attachments: TermAttachments): ApplTerm {
        return SimpleApplTermImpl(op, args, attachments)
    }

    /** Constructor application term. */
    private inner class SimpleApplTermImpl(
        override val termOp: String,
        termArgs: List<Term>,
        attachments: TermAttachments = TermAttachments.empty(),
    ) : ApplTermBase(attachments) {

        override val termArity: Int = termArgs.size

        override val termArgs: List<Term> = termArgs.toList() // Safety copy.

        override fun equalSubterms(that: ApplTerm, compareAttachments: Boolean): Boolean {
            return this.termArgs == that.termArgs
        }

        // The fields in the hash must match the fields in [equalSubterms]
        override val subtermsHashCode: Int = Objects.hash(this.termArgs)

    }

    companion object : SimpleTermBuilder()

}