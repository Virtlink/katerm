package net.pelsmaeker.katerm

import net.pelsmaeker.katerm.attachments.TermAttachments
import java.util.Objects

/**
 * A term.
 *
 * Terms are immutable. To create or change a term, use a [TermBuilder].
 */
interface Term {

    /** A list of child terms of the term. */
    val termChildren: List<Term>

    /** The attachments of the term. */
    val termAttachments: TermAttachments

    /** The free variables that occur in the term at any depth. This can be used for an 'occurs check'. */
    val termVars: Set<TermVar>

    /** The kind of term. */
    val termKind: TermKind

    /** Whether this term is a variable or a list/option variable. */
    val isTermVar: Boolean

    /**
     * Determines whether this term and its subterms represent the same value
     * as the given term and it subterms, regardless of the actual implementations
     * of the terms and its subterms.
     *
     * Note that subterms and attachments are also checked by this method.
     *
     * This method may be used in tests to assert equality.
     */
    override fun equals(other: Any?): Boolean

    /**
     * Determines whether this term and optionally its subterms represent the same value
     * as the given term and it subterms, regardless of the actual implementations
     * of the terms and its subterms.
     *
     * Implementations should compare equal to other implementations of the same term type,
     * but can take shortcuts when comparing to the same implementation of the term type.
     *
     * @param that The other term to compare to.
     * @param compareSubterms Whether to compare subterms.
     * @param compareAttachments Whether to compare the attachments.
     * @return `true` if the terms are equal (optionally modulo subterms/attachments); otherwise, `false`.
     */
    fun equals(that: Term, compareSubterms: Boolean = true, compareAttachments: Boolean = true): Boolean


    /**
     * Returns a hash code value for the term, including its subterms and attachments.
     *
     * This method may be used in tests to assert equality of terms of the same implementation.
     * If [equals] returns `true` for two terms, then this method should return the same value for both terms.
     *
     * @return A hash code value for the term, including its subterms and attachments.
     */
    override fun hashCode(): Int

    /**
     * Returns a hash code value for the term, optionally including its subterms and attachments.
     *
     * This method may be used in tests to assert equality of terms of the same implementation.
     * For a given setting of [compareSubterms] and [compareAttachments],
     * if [equals] returns `true` for two terms, then this method should return the same value for both terms.
     *
     * @param compareSubterms Whether to hash subterms.
     * @param compareAttachments Whether to hash the attachments.
     * @return A hash code value for the term, optionally including its subterms and attachments.
     */
    fun hashCode(compareSubterms: Boolean = true, compareAttachments: Boolean = true): Int

    /**
     * Accepts a term visitor.
     *
     * @param visitor The visitor to accept.
     * @return The result returned by the visitor.
     */
    fun <R> accept(visitor: TermVisitor<R>): R

    /**
     * Accepts a term visitor.
     *
     * @param visitor The visitor to accept.
     * @param arg The argument to pass to the visitor.
     * @return The result returned by the visitor.
     */
    fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R
}


/**
 * Determines whether this nullable term and optionally its subterms represent the same value
 * as the given term and it subterms, regardless of the actual implementations
 * of the terms and its subterms.
 *
 * Implementations should compare equal to other implementations of the same term type,
 * but can take shortcuts when comparing to the same implementation of the term type.
 *
 * @receiver The term to compare, which may be `null`.
 * @param that The other term to compare to, which may be `null`.
 * @param compareSubterms Whether to compare subterms.
 * @param compareAttachments Whether to compare the attachments.
 * @return `true` if the terms are equal (optionally modulo subterms/attachments); otherwise, `false`.
 */
fun Term?.equals(that: Term?, compareSubterms: Boolean, compareAttachments: Boolean): Boolean {
    if (this == null && that == null) return true
    if (this == null || that == null) return false
    return this.equals(that, compareSubterms = compareSubterms, compareAttachments = compareAttachments)
}


/**
 * A constructor application term.
 */
interface ApplTerm : Term {

    /** The constructor name. */
    val termOp: String

    /** The constructor arity. */
    val termArity: Int

    /** The term arguments. */
    val termArgs: List<Term>

}


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

    final override val termKind: TermKind get() = TermKind.APPL

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


/**
 * A value term.
 *
 * This replaces the BLOB type of term from the standard ATerm library.
 * When representing a custom value, it is preferred to represent it as an [ApplTerm].
 * When that's not possible, implement this [ValueTerm] class instead.
 * The value should be immutable.
 *
 * @property termAttachments The attachments of the term.
 */
abstract class ValueTerm protected constructor(
    override val termAttachments: TermAttachments,
) : Term {

    /** The value of the term. */
    abstract val value: Any

    abstract override val termKind: TermKind

    final override val termChildren: List<Term> get() = emptyList()

    final override val termVars: Set<TermVar> get() = emptySet()

    final override val isTermVar: Boolean get() = false

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true                     // Identity equality
        val that = other as? Term ?: return false           // Must be a Term
        return equals(that, compareSubterms = true, compareAttachments = true)
    }

    final override fun equals(that: Term, compareSubterms: Boolean, compareAttachments: Boolean): Boolean {
        if (that::class.java != this::class.java) return false
        that as ValueTerm
        if (this.hashCode(compareSubterms, compareAttachments) != that.hashCode(compareSubterms, compareAttachments)) return false
        if (compareAttachments && (this.termAttachments != that.termAttachments)) return false
        return equalValues(that)
    }

    /**
     * Determines whether this value term and the given value term represent the same value.
     *
     * @return `true` if the value terms represent the same value; otherwise, `false`.
     */
    abstract fun equalValues(that: ValueTerm): Boolean

    final override fun hashCode(): Int = hashCode(compareSubterms = true, compareAttachments = true)

    final override fun hashCode(compareSubterms: Boolean, compareAttachments: Boolean): Int {
        return Objects.hash(
            this::class.java,
            valueHashCode(),
            if (compareAttachments) termAttachments else null
        )
    }

    /**
     * Computes a hash code value for the value of the term.
     *
     * If [equalValues] returns `true` for two value terms,
     * then this method must return the same hash code value for both values.
     *
     * @return A hash code value for the value of the term.
     */
    protected abstract fun valueHashCode(): Int

    override fun toString(): String {
        return value.toString()
    }
}


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

    override val termKind: TermKind get() = TermKind.VALUE_STRING

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


/**
 * An option term.
 *
 * @param E The type of the element in the option.
 */
sealed interface OptionTerm<out E : Term> : Term {

    /**
     * Whether the option is definitely empty (and not just an option variable).
     *
     * Note that [isNotEmpty] is not the inverse of [isEmpty]. It is possible for both to be false.
     */
    fun isEmpty(): Boolean

    /**
     * Whether the option is definitely not empty (and not just an option variable).
     *
     * Note that [isNotEmpty] is not the inverse of [isEmpty]. It is possible for both to be false.
     */
    fun isNotEmpty(): Boolean

}


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



/**
 * A list term.
 *
 * @param E The type of the elements in the list.
 */
sealed interface ListTerm<out E: Term> : Term {

    /**
     * Whether the list is definitely empty (and contains no list variables).
     *
     * Note that [isNotEmpty] is not the inverse of [isEmpty]. It is possible for both to be false.
     */
    fun isEmpty(): Boolean

    /**
     * Whether the list is definitely not empty (and not just a list variable).
     *
     * Note that [isNotEmpty] is not the inverse of [isEmpty]. It is possible for both to be false.
     */
    fun isNotEmpty(): Boolean

    /** The minimum number of elements in the list. This is the number of elements in [elements]. */
    val minSize: Int

    /** The number of elements in the list; or `null` if the list contains one or more term variables. */
    val size: Int?

    /** The elements in the list. If the list contains term variables, they are not included here. */
    val elements: List<E>

}


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


/**
 * An empty list.
 *
 * @property termAttachments The attachments of the term.
 */
class NilListTerm internal constructor(
    override val termAttachments: TermAttachments,
) : ListTerm<Nothing> {

    override val termChildren: List<Term> get() = emptyList()

    override val termKind: TermKind get() = TermKind.LIST_NIL

    override val termVars: Set<TermVar> get() = emptySet()

    override val isTermVar: Boolean get() = false

    override fun isEmpty(): Boolean = true

    override fun isNotEmpty(): Boolean = false

    override val minSize: Int get() = 0

    override val size: Int get() = 0

    override val elements: List<Nothing> get() = emptyList()

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitNilList(this)

    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitNilList(this, arg)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true                     // Identity equality
        val that = other as? Term ?: return false           // Must be a Term
        return equals(that, compareSubterms = true, compareAttachments = true)
    }

    override fun equals(that: Term, compareSubterms: Boolean, compareAttachments: Boolean): Boolean {
        if (that !is NilListTerm) return false
        if (this.hashCode(compareSubterms, compareAttachments) != that.hashCode(compareSubterms, compareAttachments)) return false
        if (compareAttachments && (this.termAttachments != that.termAttachments)) return false
        return true
    }

    override fun hashCode(): Int = hashCode(compareSubterms = true, compareAttachments = true)

    override fun hashCode(compareSubterms: Boolean, compareAttachments: Boolean): Int {
        return Objects.hash(
            this::class.java,                                   // Hash the class name
            if (compareAttachments) termAttachments else null
        )
    }

    override fun toString(): String {
        return "[]"
    }
}


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

    override val termKind: TermKind get() = TermKind.LIST_CONCAT

    private var _termVars: Set<TermVar>? = null
    override val termVars: Set<TermVar> get() = _termVars ?: (left.termVars + right.termVars).also { _termVars = it }

    override val isTermVar: Boolean get() = false

    override val termAttachments: TermAttachments get() = TermAttachments.empty()

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


/**
 * A (list/option) term variable.
 *
 * @property name The variable name. Any resource names should be encoded as part of the variable name.
 * @property termAttachments The attachments of the term.
 */
class TermVar internal constructor(
    val name: String,
    override val termAttachments: TermAttachments,
) : Term, ListTerm<Nothing>, OptionTerm<Nothing> {

    override val termKind: TermKind get() = TermKind.VAR

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
