package net.pelsmaeker.katerm.terms

import net.pelsmaeker.katerm.attachments.TermAttachments
import java.util.Objects

/**
 * A constructor application term.
 *
 * @property termAttachments The attachments of the term.
 */
abstract class ApplTermBase protected constructor(
    override val termAttachments: TermAttachments,
) : ApplTerm {

    abstract override val termOp: String

    abstract override val termArity: Int

    abstract override val termArgs: List<Term>

    final override val isTermVar: Boolean get() = false

    final override val termChildren: List<Term> get() = termArgs

    private var _termVars: Set<TermVar>? = null
    final override val termVars: Set<TermVar>
        get() = _termVars ?: termArgs.flatMapTo(HashSet()) { it.termVars }.also { _termVars = it }

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitAppl(this)

    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitAppl(this, arg)

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true                     // Identity equality
        val that = other as? Term ?: return false           // Must be a Term
        return equals(that, compareSubterms = true, compareAttachments = true)
    }

    final override fun equals(that: Term, compareSubterms: Boolean, compareAttachments: Boolean): Boolean {
        if (that !is ApplTermBase) return false
        if (this.hashCode(compareSubterms, compareAttachments) != that.hashCode(compareSubterms, compareAttachments)) return false
        if (this.termArity != that.termArity) return false
        if (this.termOp != that.termOp) return false
        if (compareSubterms && !this.equalSubterms(that, compareAttachments)) return false
        if (compareAttachments && (this.termAttachments != that.termAttachments)) return false
        return true
    }

    /**
     * Checks whether this term and the given term have equal subterms.
     *
     * Override this method to customize or optimize the equality check.
     *
     * @param that The term to check.
     * @param compareAttachments Whether to compare the attachments of the subterms.
     * @return `true` if this term has equal subterms as the specified term; otherwise, `false`.
     */
    protected abstract fun equalSubterms(that: ApplTerm, compareAttachments: Boolean): Boolean

    /**
     * Pre-computes the hash code for the subterms of this term.
     *
     * If [equalSubterms] returns `true` for two appl terms,
     * then this method must return the same hash code value for both list of subterms.
     */
    protected abstract val subtermsHashCode: Int

    final override fun hashCode(): Int = hashCode(compareSubterms = true, compareAttachments = true)

    override fun hashCode(compareSubterms: Boolean, compareAttachments: Boolean): Int {
        return Objects.hash(
            this::class.java,                                   // Hash the class name
            termOp,
            termArity,
            if (compareSubterms) subtermsHashCode else 0,       // Use the precomputed subterm hash if comparing subterms
            if (compareAttachments) termAttachments else null
        )
    }

    override fun toString(): String {
        return "$termOp(${termArgs.joinToString(", ")})"
    }
}