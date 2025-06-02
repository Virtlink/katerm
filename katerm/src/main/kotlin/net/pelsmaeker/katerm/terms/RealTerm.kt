package net.pelsmaeker.katerm.terms

import net.pelsmaeker.katerm.TermKind
import net.pelsmaeker.katerm.TermVisitor
import net.pelsmaeker.katerm.TermVisitor1
import net.pelsmaeker.katerm.attachments.TermAttachments

/**
 * A real value term.
 *
 * @property value The real value.
 * @param termAttachments The attachments of the term.
 */
class RealTerm internal constructor(
    override val value: Double,
    termAttachments: TermAttachments,
) : ValueTerm(termAttachments) {

    override val termKind: TermKind get() = TermKind.VALUE_REAL

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitReal(this)

    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitReal(this, arg)

    override fun equalValues(that: ValueTerm): Boolean {
        return that is RealTerm && this.value == that.value
    }

    override fun valueHashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String {
        return value.toString()
    }

}