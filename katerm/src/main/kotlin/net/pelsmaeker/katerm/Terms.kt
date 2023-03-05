package net.pelsmaeker.katerm

/**
 * A term.
 *
 * Terms are immutable. To create or change a term, use a [TermBuilder].
 */
interface Term {
    /** The attachments of the term. */
    val termAttachments: TermAttachments
    /** A list of child terms of the term. */
    val termChildren: List<Term>
    /** A list of separators between the child terms; or `null` to use the default separators.
     * If specified, there are always (n + 1) separators, where n is the number of children. */
    val termSeparators: List<String>?

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
}

/** A constructor application term. */
interface ApplTerm : Term {
    /** The constructor name. */
    val termOp: String
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
interface ValueTerm<T> : Term {
    /** The value of the term. */
    val termValue: T
    /** The text representation of the value of the term. */
    val termText: String

    override val termChildren: List<Term> get() = emptyList()
}

/** An integer number term. */
interface IntTerm : ValueTerm<Int> {
    override val termValue: Int // FIXME: Is this a boxed Int?

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitInt(this)
    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitInt(this, arg)
}

/** A real number term. */
interface RealTerm : ValueTerm<Double> {
    override val termValue: Double  // FIXME: Is this a boxed Double?
    override val termChildren: List<Term> get() = emptyList()

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitReal(this)
    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitReal(this, arg)
}

/** A string term. */
interface StringTerm : ValueTerm<String> {
    override val termValue: String
    override val termChildren: List<Term> get() = emptyList()

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitString(this)
    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitString(this, arg)
}

/** A list term. */
interface ListTerm : Term {

    /** The minimum number of elements in the list. This is the number of elements in [elements]. */
    val minSize: Int
    /** The number of elements in the list; or `null` if the list ends with a variable. */
    val size: Int?
    /** The elements in the list. If the list ends with a variable, it is not included here. */
    val elements: List<Term>
    /** The trailing variable of the list; or `null` if the list does not have a trailing variable. */
    val trailingVar: ListTermVar?

    /** The head of the list; or `null` if the list is empty. */
    val head: Term?
    /** The tail of the list; or an [ListTermVar] if the list ends with a variable; or `null` if the list is empty. */
    val tail: ListTerm?

    override val termChildren: List<Term> get() = elements

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitList(this)
    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitList(this, arg)
}

/** A term variable. */
interface TermVar: Term {
    /** The variable name. Any resource names should be encoded as part of the variable name. */
    val name: String

    override val termChildren: List<Term> get() = emptyList()
    override val termSeparators: List<String>? get() = null

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitVar(this)
    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitVar(this, arg)
}

/** A term list variable. */
interface ListTermVar: TermVar, ListTerm {
    override val termChildren: List<Term> get() = emptyList()
    override val termSeparators: List<String>? get() = null

    override val minSize: Int get() = 0
    override val size: Int? get() = null
    override val elements: List<Term> get() = emptyList()
    override val trailingVar: ListTermVar? get() = this

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitVar(this)
    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitVar(this, arg)
}

///** A non-empty list term. */
//interface ConsTerm : ListTerm {
//    /** The head of the list. */
//    val head: Term
//    /** The tail of the list, which may be a variable. */
//    val tail: ListTerm
//
//    override val elements: List<Term> get() = listOf(head) + tail.elements  // TODO: Optimize
//
//    override fun <R> accept(visitor: ListTermVisitor<R>): R = visitor.visitCons(this)
//    override fun <A, R> accept(visitor: ListTermVisitor1<A, R>, arg: A): R = visitor.visitCons(this, arg)
//}
//
///** An empty list term. */
//interface NilTerm : ListTerm {
//    override val minSize: Int get() = 0
//    override val size: Int? get() = 0
//    override val elements: List<Term> get() = emptyList()
//    override val trailingVar: ListTermVar? get() = null
//
//    override fun <R> accept(visitor: ListTermVisitor<R>): R = visitor.visitNil(this)
//    override fun <A, R> accept(visitor: ListTermVisitor1<A, R>, arg: A): R = visitor.visitNil(this, arg)
//}
//

