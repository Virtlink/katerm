package net.pelsmaeker.katerm.terms

import net.pelsmaeker.katerm.TermKind
import net.pelsmaeker.katerm.TermVisitor
import net.pelsmaeker.katerm.TermVisitor1
import net.pelsmaeker.katerm.attachments.TermAttachments
import java.util.Objects

/**
 * An empty option term.
 *
 * @property termAttachments The attachments of the term.
 */
class NoneOptionTerm internal constructor(
    override val termAttachments: TermAttachments,
) : OptionTerm<Nothing> {

    override fun isEmpty(): Boolean = true

    override fun isNotEmpty(): Boolean = false

    override val termKind: TermKind get() = TermKind.OPTION_NONE

    override val termChildren: List<Term> get() = emptyList()

    override val termVars: Set<TermVar> get() = emptySet()

    override val isTermVar: Boolean get() = false

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitNoneOption(this)

    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitNoneOption(this, arg)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true                     // Identity equality
        val that = other as? Term ?: return false           // Must be a Term
        return equals(that, compareSubterms = true, compareAttachments = true)
    }

    override fun equals(that: Term, compareSubterms: Boolean, compareAttachments: Boolean): Boolean {
        if (that !is NoneOptionTerm) return false
        if (this.hashCode(compareSubterms, compareAttachments) != that.hashCode(compareSubterms, compareAttachments)) return false
        if (compareAttachments && (this.termAttachments != that.termAttachments)) return false
        return true
    }

    override fun hashCode(): Int = hashCode(compareSubterms = true, compareAttachments = true)

    override fun hashCode(compareSubterms: Boolean, compareAttachments: Boolean): Int {
        return Objects.hash(
            this::class.java,                                   // Hash the class name to make minimal hash of SomeOptionTerm and NoneOptionTerm different
            if (compareAttachments) termAttachments else null
        )
    }

    override fun toString(): String {
        return "<>"
    }
}