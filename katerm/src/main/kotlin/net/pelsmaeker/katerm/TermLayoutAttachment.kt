package net.pelsmaeker.katerm

/** Attaches layout information to a term. */
class TermLayoutAttachment(
    /** The separator strings to use. There must be one more separator than the number of subterms, with a minimum of one. */
    val separators: List<String>,
) {
    companion object {
        /** The term attachment key for [TermLayoutAttachment]. */
        object Key: TermAttachments.Key<TermLayoutAttachment>(TermLayoutAttachment::class.java)
    }
}


val Term.termLayout: TermLayoutAttachment? get() = termAttachments[TermLayoutAttachment.Companion.Key]
val Term.hasTermLayout: Boolean get() = termLayout != null