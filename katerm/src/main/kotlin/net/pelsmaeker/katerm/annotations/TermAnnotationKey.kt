package net.pelsmaeker.katerm.annotations

import net.pelsmaeker.katerm.Term
import net.pelsmaeker.katerm.attachments.TermAttachments

/** A term attachment key for term annotations. */
//object TermAnnotationKey: TermAttachments.Key<List<Term>>(typeOf<List<Term>>().classifier!! as KClass<List<Term>>)
@Suppress("UNCHECKED_CAST")
object TermAnnotationKey: TermAttachments.Key<List<Term>>(List::class.java as Class<out List<Term>>)