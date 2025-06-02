package net.pelsmaeker.katerm.annotations

import net.pelsmaeker.katerm.terms.Term
import net.pelsmaeker.katerm.attachments.TermAttachments

/** A term attachment key for term annotations. */
@Suppress("UNCHECKED_CAST")
object TermAnnotationKey: TermAttachments.Key<List<Term>>(List::class.java as Class<out List<Term>>)