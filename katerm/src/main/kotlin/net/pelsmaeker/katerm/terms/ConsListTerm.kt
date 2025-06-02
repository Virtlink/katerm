package net.pelsmaeker.katerm.terms

import net.pelsmaeker.katerm.TermKind
import net.pelsmaeker.katerm.TermVisitor
import net.pelsmaeker.katerm.TermVisitor1
import net.pelsmaeker.katerm.attachments.TermAttachments
import java.util.Objects

/**
 * List cons term (a list head with a tail).
 *
 * @property head The head of the list.
 * @property tail The tail of the list. It cannot be a concatenation.
 * @property termAttachments The attachments of the term.
 */
class ConsListTerm<out E: Term> internal constructor(
    val head: E,
    val tail: ListTerm<E>,
    override val termAttachments: TermAttachments,
) : ListTerm<E> {

    init {
        require(tail !is ConcatListTerm<E>) { "The tail list must not be a concatenation." }
    }

    override val termChildren: List<Term> get() = listOf(head, tail)

    override val termKind: TermKind get() = TermKind.LIST_CONS

    private var _termVars: Set<TermVar>? = null
    override val termVars: Set<TermVar> get() = _termVars ?: (head.termVars + tail.termVars).also { _termVars = it }

    override val isTermVar: Boolean get() = false

    override fun isEmpty(): Boolean = false

    override fun isNotEmpty(): Boolean = true

    override val minSize: Int get() = 1 + tail.minSize

    override val size: Int? get() = tail.size?.let { 1 + it }

    override val elements: List<E> get() = listOf(head) + tail.elements

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitConsList(this)

    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitConsList(this, arg)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true                     // Identity equality
        val that = other as? Term ?: return false           // Must be a Term
        return equals(that, compareSubterms = true, compareAttachments = true)
    }

    override fun equals(that: Term, compareSubterms: Boolean, compareAttachments: Boolean): Boolean {
        if (that !is ConsListTerm<*>) return false
        if (this.hashCode(compareSubterms, compareAttachments) != that.hashCode(compareSubterms, compareAttachments)) return false
        if (compareSubterms) {
            if (!this.head.equals(that.head, compareSubterms = true, compareAttachments)) return false
            if (!this.tail.equals(that.tail, compareSubterms = true, compareAttachments)) return false
        }
        if (compareAttachments && (this.termAttachments != that.termAttachments)) return false
        return true
    }

    private val subtermHash: Int = Objects.hash(head, tail)

    override fun hashCode(): Int = hashCode(compareSubterms = true, compareAttachments = true)

    override fun hashCode(compareSubterms: Boolean, compareAttachments: Boolean): Int {
        return Objects.hash(
            this::class.java,                                   // Hash the class name
            if (compareSubterms) subtermHash else 0,
            if (compareAttachments) termAttachments else null
        )
    }

    override fun toString(): String = buildString {
        append("[")
        append(head)
        var current: ListTerm<Term>? = tail
        while (current != null) {
            if (current.isEmpty()) {
                append("]")
                return@buildString
            } else if (current is ConsListTerm<*>) {
                append(", ")
                append(current.head)
                current = current.tail
            } else if (current is TermVar) {
                append(" | ")
                append(current)
                append("]")
                return@buildString
            } else {
                append("]")
                append(current)
                break
            }
        }
        append("]")
    }
}