package net.pelsmaeker.katerm

/** Attaches layout information to a term. */
class LayoutAttachment(
    /** The separator strings to use. There must be one more separator than the number of subterms, with a minimum of one. */
    val separators: List<String>,
) {
    companion object {
        /** The term attachment key for [LayoutAttachment]. */
        object Key: TermAttachments.Key<LayoutAttachment>(LayoutAttachment::class.java)
    }
}
