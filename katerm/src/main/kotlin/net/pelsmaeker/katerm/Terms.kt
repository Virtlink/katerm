package net.pelsmaeker.katerm

import net.pelsmaeker.katerm.attachments.TermAttachments

/**
 * A term.
 *
 * Terms are immutable. To create or change a term, use a [TermBuilder].
 */
sealed interface Term {

    /** A list of child terms of the term. */
    val termChildren: List<Term>

    /** The attachments of the term. */
    val termAttachments: TermAttachments

    /** The free variables that occur in the term at any depth. This can be used for an 'occurs check'. */
    val termVars: Set<TermVar>

    /** The kind of term. */
    val termKind: TermKind

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


/** A constructor application term. */
interface ApplTerm : Term {

    /** The constructor name. */
    val termOp: String
    /** The constructor arity. */
    val termArity: Int  //get() = termArgs.size
    /** The term arguments. */
    val termArgs: List<Term>

//    override val termKind: TermKind get() = TermKind.APPL
//
//    override val termChildren: List<Term> get() = termArgs
//
//    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitAppl(this)
//
//    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitAppl(this, arg)

}


/**
 * A value term.
 *
 * This replaces the BLOB type of term from the standard ATerm library.
 * When representing a custom value, it is preferred to represent it as an [ApplTerm].
 * When that's not possible, implement this [ValueTerm] interface instead.
 * The value should be immutable.
 */
interface ValueTerm : Term {

    /** The value of the term. */
    val value: Any

}


/** An integer number term. */
interface IntTerm : ValueTerm {

    override val value: Int

//    override val termKind: TermKind get() = TermKind.VALUE_INT
//
//    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitInt(this)
//
//    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitInt(this, arg)
}


/** A real number term. */
interface RealTerm : ValueTerm {

    override val value: Double

//    override val termKind: TermKind get() = TermKind.VALUE_REAL
//
//    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitReal(this)
//
//    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitReal(this, arg)
}


/** A string term. */
interface StringTerm : ValueTerm {

    override val value: String

//    override val termKind: TermKind get() = TermKind.VALUE_STRING
//
//    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitString(this)
//
//    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitString(this, arg)
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
 */
interface SomeOptionTerm<out E : Term> : OptionTerm<E> {

    /** The term that is the element of the option. */
    val element: E

//    override fun isEmpty(): Boolean = false
//
//    override fun isNotEmpty(): Boolean = true
//
//    override val termKind: TermKind get() = TermKind.OPTION_SOME
//
//    override val termChildren: List<Term> get() = listOf(element)
//
//    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitSomeOption(this)
//
//    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitSomeOption(this, arg)

}

/**
 * An empty option term.
 */
interface NoneOptionTerm : OptionTerm<Nothing> {

//    override fun isEmpty(): Boolean = true
//
//    override fun isNotEmpty(): Boolean = false
//
//    override val termKind: TermKind get() = TermKind.OPTION_NONE
//
//    override val termChildren: List<Term> get() = emptyList()
//
//    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitNoneOption(this)
//
//    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitNoneOption(this, arg)

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


/** A non-empty list. */
interface ConsListTerm<out E: Term> : ListTerm<E> {

    /** The head of the list. */
    val head: E

    /** The tail of the list. It cannot be a concatenation. */
    val tail: ListTerm<E>

//    override val termChildren: List<Term> get() = listOf(head, tail)
//
//    override val termKind: TermKind get() = TermKind.LIST_CONS
//
//    override fun isEmpty(): Boolean = false
//
//    override fun isNotEmpty(): Boolean = true
//
//    override val minSize: Int get() = 1 + tail.minSize
//
//    override val size: Int? get() = tail.size?.let { 1 + it }
//
//    override val elements: List<E> get() = listOf(head) + tail.elements
//
//    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitConsList(this)
//
//    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitConsList(this, arg)

}


/** An empty list. */
interface NilListTerm : ListTerm<Nothing> {

//    override val termChildren: List<Term> get() = emptyList()
//
//    override val termKind: TermKind get() = TermKind.LIST_NIL
//
//    override fun isEmpty(): Boolean = true
//
//    override fun isNotEmpty(): Boolean = false
//
//    override val minSize: Int get() = 0
//
//    override val size: Int? get() = 0
//
//    override val elements: List<Nothing> get() = emptyList()
//
//    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitNilList(this)
//
//    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitNilList(this, arg)

}


/**
 * A concatenation of two lists.
 *
 * A concatenation cannot have term attachments.
 */
interface ConcatListTerm<out E: Term> : ListTerm<E> {

    /** The left list term. It cannot be a concatenation or empty list. */
    val left: ListTerm<E>

    /** The right list term. It cannot be an empty list. */
    val right: ListTerm<E>

//    override val termChildren: List<Term> get() = listOf(left, right)
//
//    override val termKind: TermKind get() = TermKind.LIST_CONCAT
//
//    override fun isEmpty(): Boolean = left.isEmpty() && right.isEmpty()     // This is always false
//
//    override fun isNotEmpty(): Boolean = left.isNotEmpty() && right.isNotEmpty()
//
//    override val minSize: Int get() = left.minSize + right.minSize
//
//    override val size: Int? get() = left.size?.let { leftSize -> right.size?.let { rightSize -> leftSize + rightSize} }
//
//    override val elements: List<E> get() = left.elements + right.elements
//
//    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitConcList(this)
//
//    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitConcList(this, arg)

}


/** A term variable or list term variable. */
interface TermVar: Term, ListTerm<Nothing>, OptionTerm<Nothing> {

    /** The variable name. Any resource names should be encoded as part of the variable name. */
    val name: String

//    override val termKind: TermKind get() = TermKind.VAR
//
//    override val termChildren: List<Term> get() = emptyList()
//
//    override fun isEmpty(): Boolean = false
//
//    override fun isNotEmpty(): Boolean = false
//
//    override val minSize: Int get() = 0
//
//    override val size: Int? get() = null
//
//    override val elements: List<Nothing> get() = emptyList()
//
//    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitVar(this)
//
//    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitVar(this, arg)

}
