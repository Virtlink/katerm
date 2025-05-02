package net.pelsmaeker.katerm

import net.pelsmaeker.katerm.attachments.TermAttachments
import java.util.*

/**
 * The default simple term builder.
 */
class SimpleTermBuilder: TermBuilderBase() {

    override fun newAppl(op: String, args: List<Term>, attachments: TermAttachments): ApplTerm {
        return SimpleApplTerm(op, args, attachments)
    }

    /** Constructor application term. */
    private inner class SimpleApplTerm(
        override val termOp: String,
        termArgs: List<Term>,
        attachments: TermAttachments = TermAttachments.empty(),
    ) : ApplTerm, ApplTermBase(attachments) {

        override val termArgs: List<Term> = termArgs.toList() // Safety copy.

        override fun equalsAppl(that: ApplTerm): Boolean {
            // @formatter:off
            return this.termOp == that.termOp
                && this.termArgs == that.termArgs
            // @formatter:on
        }

        // The fields in the hash must match the fields in [equalsAppl]
        override val hash: Int = Objects.hash(termOp, termArgs)
    }

}