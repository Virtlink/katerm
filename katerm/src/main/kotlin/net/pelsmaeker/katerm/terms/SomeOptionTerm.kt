package net.pelsmaeker.katerm.terms

import net.pelsmaeker.katerm.TermKind
import net.pelsmaeker.katerm.TermVisitor
import net.pelsmaeker.katerm.TermVisitor1
import net.pelsmaeker.katerm.attachments.TermAttachments
import java.util.Objects

/**
 * A non-empty option term.
 *
 * @param E The type of the element in the option.
 * @property element The term that is the element of the option.
 * @property termAttachments The attachments of the term.
 */
class SomeOptionTerm<out E: Term> internal constructor(
    val element: E,
    override val termAttachments: TermAttachments,
) : OptionTerm<E> {

    override fun isEmpty(): Boolean = false

    override fun isNotEmpty(): Boolean = true

    override val termKind: TermKind get() = TermKind.OPTION_SOME

    override val termChildren: List<Term> get() = listOf(element)

    override val termVars: Set<TermVar> get() = element.termVars

    override val isTermVar: Boolean get() = false

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitSomeOption(this)

    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitSomeOption(this, arg)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true                     // Identity equality
        val that = other as? Term ?: return false           // Must be a Term
        return equals(that, compareSubterms = true, compareAttachments = true)
    }

    override fun equals(that: Term, compareSubterms: Boolean, compareAttachments: Boolean): Boolean {
        if (that !is SomeOptionTerm<*>) return false
        if (this.hashCode(compareSubterms, compareAttachments) != that.hashCode(compareSubterms, compareAttachments)) return false
        if (compareSubterms) {
            if (!this.element.equals(that.element, compareSubterms = true, compareAttachments)) return false
        }
        if (compareAttachments && (this.termAttachments != that.termAttachments)) return false
        return true
    }

    private val subtermHash: Int = Objects.hash(element)

    override fun hashCode(): Int = hashCode(compareSubterms = true, compareAttachments = true)

    override fun hashCode(compareSubterms: Boolean, compareAttachments: Boolean): Int {
        return Objects.hash(
            this::class.java,                                   // Hash the class name to make minimal hash of SomeOptionTerm and NoneOptionTerm different
            if (compareSubterms) subtermHash else 0,
            if (compareAttachments) termAttachments else null
        )
    }

    override fun toString(): String {
        return "<${this.element}>"
    }

}