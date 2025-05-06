package net.pelsmaeker.katerm.attachments

import net.pelsmaeker.katerm.Term

/**
 * Annotates a term with layout information.
 *
 * @property separators The separator strings to use. There must be one more separator than the number of subterms,
 * with a minimum of one.
 */
class LayoutTermAttachment(
    val separators: List<String>,
) {
    companion object {
        /** The term attachment key for [LayoutTermAttachment]. */
        object Key: TermAttachments.Key<LayoutTermAttachment>(LayoutTermAttachment::class.java)
    }
}


/** Gets the layout attachment of this term, if any; otherwise, `null`. */
val Term.layoutAttachment: LayoutTermAttachment? get() = termAttachments[LayoutTermAttachment.Companion.Key]

/** Whether this term has a layout attachment. */
val Term.hasLayoutAttachment: Boolean get() = layoutAttachment != null