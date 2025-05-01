package net.pelsmaeker.katerm.attachments

import net.pelsmaeker.katerm.Term
import net.pelsmaeker.katerm.TextSpan
import kotlin.collections.get

/**
 * Annotates a term with origin information.
 *
 * @property path The path of the origin source file.
 * @property span The origin text span.
 */
class TermOriginAttachment(
    val path: String,
    val span: TextSpan,
) {
    companion object {
        /** The term attachment key for [TermOriginAttachment]. */
        object Key: TermAttachments.Key<TermOriginAttachment>(TermOriginAttachment::class.java)
    }
}


/** Gets the layout attachment of this term, if any; otherwise, `null`. */
val Term.termOrigin: TermOriginAttachment? get() = termAttachments[TermOriginAttachment.Companion.Key]

/** Whether this term has a layout attachment. */
val Term.hasTermOrigin: Boolean get() = termOrigin !=  null