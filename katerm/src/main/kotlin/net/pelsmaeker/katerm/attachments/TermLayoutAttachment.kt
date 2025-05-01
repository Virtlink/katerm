package net.pelsmaeker.katerm.attachments

import net.pelsmaeker.katerm.Term
import kotlin.collections.get

/**
 * Annotates a term with layout information.
 *
 * @property separators The separator strings to use. There must be one more separator than the number of subterms,
 * with a minimum of one.
 */
class TermLayoutAttachment(
    val separators: List<String>,
) {
    companion object {
        /** The term attachment key for [TermLayoutAttachment]. */
        object Key: TermAttachments.Key<TermLayoutAttachment>(TermLayoutAttachment::class.java)
    }
}


/** Gets the layout attachment of this term, if any; otherwise, `null`. */
val Term.termLayout: TermLayoutAttachment? get() = termAttachments[TermLayoutAttachment.Companion.Key]

/** Whether this term has a layout attachment. */
val Term.hasTermLayout: Boolean get() = termLayout != null