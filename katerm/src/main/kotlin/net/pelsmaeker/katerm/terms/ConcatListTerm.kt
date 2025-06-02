package net.pelsmaeker.katerm.terms

import net.pelsmaeker.katerm.attachments.TermAttachments
import java.util.Objects

/**
 * A concatenation of two lists.
 *
 * A concatenation cannot have term attachments.
 *
 * @property left The left list.
 * @property right The right list.
 */
class ConcatListTerm<out E: Term> internal constructor(
    val left: ListTerm<E>,
    val right: ListTerm<E>,
) : ListTerm<E> {

    init {
        require(left is TermVar || (left is ConsListTerm<E> && left.tail is TermVar)) {
            "The left list must be a variable or have a variable as the tail."
        }
        require(right.size != 0) {
            "The right list must not be empty."
        }
    }

    override val termChildren: List<Term> get() = listOf(left, right)

    private var _termVars: Set<TermVar>? = null
    override val termVars: Set<TermVar> get() = _termVars ?: (left.termVars + right.termVars).also { _termVars = it }

    override val isTermVar: Boolean get() = false

    override val termAttachments: TermAttachments get() = TermAttachments.Companion.empty()

    override fun isEmpty(): Boolean = false

    override fun isNotEmpty(): Boolean = left.isNotEmpty() && right.isNotEmpty()

    override val minSize: Int get() = left.minSize + right.minSize

    override val size: Int? get() = null

    override val elements: List<E> get() = left.elements + right.elements

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitConcatList(this)

    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitConcatList(this, arg)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true                     // Identity equality
        val that = other as? Term ?: return false           // Must be a Term
        return equals(that, compareSubterms = true, compareAttachments = true)
    }

    override fun equals(that: Term, compareSubterms: Boolean, compareAttachments: Boolean): Boolean {
        if (that !is ConcatListTerm<*>) return false
        if (this.hashCode(compareSubterms, compareAttachments) != that.hashCode(compareSubterms, compareAttachments)) return false
        if (compareSubterms) {
            if (!this.left.equals(that.left, compareSubterms = true, compareAttachments)) return false
            if (!this.right.equals(that.right, compareSubterms = true, compareAttachments)) return false
        }
        if (compareAttachments && (this.termAttachments != that.termAttachments)) return false
        return true
    }

    private val subtermHash: Int = Objects.hash(left, right)

    override fun hashCode(): Int = hashCode(compareSubterms = true, compareAttachments = true)

    override fun hashCode(compareSubterms: Boolean, compareAttachments: Boolean): Int {
        return Objects.hash(
            this::class.java,                                   // Hash the class name
            if (compareSubterms) subtermHash else 0,
            if (compareAttachments) termAttachments else null
        )
    }

    override fun toString(): String {
        return "$left ++ $right"
    }
}