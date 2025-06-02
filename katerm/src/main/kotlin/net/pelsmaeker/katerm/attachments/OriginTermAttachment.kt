package net.pelsmaeker.katerm.attachments

import net.pelsmaeker.katerm.terms.Term
import net.pelsmaeker.katerm.TextSpan

/**
 * Annotates a term with origin information.
 *
 * @property path The path of the origin source file.
 * @property span The origin text span.
 */
class OriginTermAttachment(
    val path: String,
    val span: TextSpan,
) {
    companion object {
        /** The term attachment key for [OriginTermAttachment]. */
        object Key: TermAttachments.Key<OriginTermAttachment>(OriginTermAttachment::class.java)
    }
}


/** Gets the origin attachment of this term, if any; otherwise, `null`. */
val Term.originAttachment: OriginTermAttachment? get() = termAttachments[OriginTermAttachment.Companion.Key]

/** Whether this term has a origin attachment. */
val Term.hasOriginAttachment: Boolean get() = originAttachment !=  null