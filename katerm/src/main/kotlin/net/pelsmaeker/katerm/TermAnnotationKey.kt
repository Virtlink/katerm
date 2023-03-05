package net.pelsmaeker.katerm

import kotlin.reflect.KClass
import kotlin.reflect.typeOf

/** A term attachment key for term annotations. */
//object TermAnnotationKey: TermAttachments.Key<List<Term>>(typeOf<List<Term>>().classifier!! as KClass<List<Term>>)
@Suppress("UNCHECKED_CAST")
object TermAnnotationKey: TermAttachments.Key<List<Term>>(List::class.java as Class<out List<Term>>)

