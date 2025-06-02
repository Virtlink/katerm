package net.pelsmaeker.katerm.terms

import net.pelsmaeker.katerm.attachments.TermAttachments
import java.util.Objects

/**
 * A (list/option) term variable.
 *
 * Note: a [TermVar] is also a [ListTerm] and an [OptionTerm], so take care when matching against this interface type.
 *
 * @property name The variable name. Any resource names should be encoded as part of the variable name.
 * @property termAttachments The attachments of the term.
 */
class TermVar internal constructor(
    val name: String,
    override val termAttachments: TermAttachments,
) : Term, ListTerm<Nothing>, OptionTerm<Nothing> {

    override val termChildren: List<Term> get() = emptyList()

    override val termVars: Set<TermVar> get() = setOf(this)

    override val isTermVar: Boolean get() = true

    override fun isEmpty(): Boolean = false

    override fun isNotEmpty(): Boolean = false

    override val minSize: Int get() = 0

    override val size: Int? get() = null

    override val elements: List<Nothing> get() = emptyList()

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitVar(this)

    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitVar(this, arg)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true                     // Identity equality
        val that = other as? Term ?: return false           // Must be a Term
        return equals(that, compareSubterms = true, compareAttachments = true)
    }

    override fun equals(that: Term, compareSubterms: Boolean, compareAttachments: Boolean): Boolean {
        if (that !is TermVar) return false
        if (this.hashCode(compareSubterms, compareAttachments) != that.hashCode(compareSubterms, compareAttachments)) return false
        if (compareAttachments && (this.termAttachments != that.termAttachments)) return false
        return true
    }

    override fun hashCode(): Int = hashCode(compareSubterms = true, compareAttachments = true)

    override fun hashCode(compareSubterms: Boolean, compareAttachments: Boolean): Int {
        return Objects.hash(
            this::class.java,                                   // Hash the class name
            name,
            if (compareAttachments) termAttachments else null
        )
    }

    override fun toString(): String {
        return "?$name"
    }

}