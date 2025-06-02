package net.pelsmaeker.katerm.terms

import net.pelsmaeker.katerm.attachments.TermAttachments

/**
 * A string value term.
 *
 * @property value The string value.
 * @param termAttachments The attachments of the term.
 */
class StringTerm internal constructor(
    override val value: String,
    termAttachments: TermAttachments,
) : ValueTerm(termAttachments) {

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitString(this)

    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitString(this, arg)

    override fun equalValues(that: ValueTerm): Boolean {
        return that is StringTerm && this.value == that.value
    }

    override fun valueHashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String {
        return "\"${escape(value)}\""
    }

    private fun escape(s: String): String = s
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t")

}