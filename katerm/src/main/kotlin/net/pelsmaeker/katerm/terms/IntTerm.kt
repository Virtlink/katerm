package net.pelsmaeker.katerm.terms

import net.pelsmaeker.katerm.TermKind
import net.pelsmaeker.katerm.TermVisitor
import net.pelsmaeker.katerm.TermVisitor1
import net.pelsmaeker.katerm.attachments.TermAttachments

/**
 * An integer value term.
 *
 * @property value The integer value.
 * @param termAttachments The attachments of the term.
 */
class IntTerm internal constructor(
    override val value: Int,
    termAttachments: TermAttachments,
) : ValueTerm(termAttachments) {

    override val termKind: TermKind get() = TermKind.VALUE_INT

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitInt(this)

    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitInt(this, arg)

    override fun equalValues(that: ValueTerm): Boolean {
        return that is IntTerm && this.value == that.value
    }

    override fun valueHashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String {
        return value.toString()
    }

}