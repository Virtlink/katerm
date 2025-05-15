package net.pelsmaeker.katerm

import net.pelsmaeker.katerm.attachments.TermAttachments

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

    /**
     * Determines whether this term and its subterms represent the same value
     * as the given term and it subterms, regardless of the actual implementations
     * of the terms and its subterms.
     *
     * Note that attachments are also checked by this method.
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
     * Accepts a term visitor.
     *
     * @param visitor the visitor to accept
     * @return the result returned by the visitor
     */
    fun <R> accept(visitor: TermVisitor<R>): R

    /**
     * Accepts a term visitor.
     *
     * @param visitor the visitor to accept
     * @param arg the argument to pass to the visitor
     * @return the result returned by the visitor
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
    val termArity: Int
    /** The term arguments. */
    val termArgs: List<Term>

    override val termChildren: List<Term> get() = termArgs

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitAppl(this)

    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitAppl(this, arg)

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

    override val termChildren: List<Term> get() = emptyList()

    override fun <R> accept(visitor: TermVisitor<R>): R

    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R

}

/** An integer number term. */
interface IntTerm : ValueTerm {
    override val value: Int

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitInt(this)

    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitInt(this, arg)
}

/** A real number term. */
interface RealTerm : ValueTerm {
    override val value: Double

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitReal(this)

    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitReal(this, arg)
}

/** A string term. */
interface StringTerm : ValueTerm {
    override val value: String

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitString(this)

    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitString(this, arg)
}

/**
 * An option term.
 *
 * @param E The type of the element in the option.
 */
interface OptionTerm<out E : Term> : Term {
    /** The term that is the element of the option; or `null` if the option is empty or a variable. */
    val element: E?
    /** The term variable that is in this option; or `null`. */
    val variable: TermVar?

    fun isEmpty(): Boolean = element == null && variable == null
    fun isPresent(): Boolean = element != null && variable == null

    /** The child of the option. If the option contains a term variable, it is also returned here. */
    override val termChildren: List<Term>

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitOption(this)

    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitOption(this, arg)
}

/**
 * A list term.
 *
 * @param E The type of the elements in the list.
 */
interface ListTerm<out E: Term> : Term {
    /** The term variable that is the prefix of the list; or `null` when the list has no prefix variable. */
    val prefix: TermVar?
    /** The term that is the head of the list; or `null` when the list has a prefix variable or is empty. */
    val head: E?
    /** The list term that is the tail of the list; or `null` when the list is empty. */
    val tail: ListTerm<E>?

    /** The minimum number of elements in the list. This is the number of elements in [elements]. */
    val minSize: Int
    /** The number of elements in the list; or `null` if the list contains a term variable. */
    val size: Int?
    /** The elements in the list. If the list contains term variables, they are not included here. */
    val elements: List<E>
    /** The children of the list. If the list contains term variables, they are also included here. */
    override val termChildren: List<Term>

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitList(this)

    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitList(this, arg)
}

/** A term variable. */
interface TermVar: Term {
    /** The variable name. Any resource names should be encoded as part of the variable name. */
    val name: String

    override val termChildren: List<Term> get() = emptyList()

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitVar(this)

    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitVar(this, arg)
}
