package net.pelsmaeker.katerm

import net.pelsmaeker.katerm.attachments.TermAttachments
import net.pelsmaeker.katerm.substitutions.Substitution
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
    ) : ApplTerm, ApplTermBase(attachments) {

        override val termArity: Int = termArgs.size

        override val termArgs: List<Term> = termArgs.toList() // Safety copy.

        override fun equalsSubterms(that: ApplTerm, compareAttachments: Boolean): Boolean {
            return super.equalsSubterms(that, compareAttachments)
        }

        // The fields in the hash must match the fields in [equalsAppl]
        override val hash: Int = Objects.hash(termOp, termArgs)
    }

    companion object : SimpleTermBuilder()

}