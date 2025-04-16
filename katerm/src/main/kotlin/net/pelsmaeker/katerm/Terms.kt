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

    // These will throw an exception if the term has less than the requested number of arguments. This is intentional.
    operator fun component1(): Term = termArgs[0]
    operator fun component2(): Term = termArgs[1]
    operator fun component3(): Term = termArgs[2]
    operator fun component4(): Term = termArgs[3]
    operator fun component5(): Term = termArgs[4]
    operator fun component6(): Term = termArgs[5]
    operator fun component7(): Term = termArgs[6]
    operator fun component8(): Term = termArgs[7]
    operator fun component9(): Term = termArgs[8]
    operator fun component10(): Term = termArgs[9]
}

/**
 * A value term.
 *
 * This replaces the BLOB type of term from the standard ATerm library.
 * When representing a custom value, it is preferred to represent it as an [ApplTerm].
 * When that's not possible, implement this [ValueTerm] interface instead.
 * The value should be immutable.
 *
 * @property V The type of value.
 */
interface ValueTerm<V> : Term {
    /** The value of the term. */
    val termValue: V
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

/**
 * A list term.
 *
 * @property E The type of the elements in the list.
 */
interface ListTerm<out E: Term> : Term {

    /** The minimum number of elements in the list. This is the number of elements in [elements]. */
    val minSize: Int
    /** The number of elements in the list; or `null` if the list ends with a variable. */
    val size: Int?
    /** The elements in the list. If the list ends with a variable, it is not included here. */
    val elements: List<E>
    /** The trailing variable of the list; or `null` if the list does not have a trailing variable. */
    val trailingVar: ListTermVar?

    /** The head of the list; or `null` if the list is empty. */
    val head: E?
    /** The tail of the list; or an [ListTermVar] if the list ends with a variable; or `null` if the list is empty. */
    val tail: ListTerm<E>?

    override val termChildren: List<E> get() = elements

    // These will throw an exception if the list is empty or a variable. This is intentional.
    operator fun component1(): Term = head!!
    operator fun component2(): Term = tail!!

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
interface ListTermVar: TermVar, ListTerm<Nothing> {
    override val termChildren: List<Nothing> get() = emptyList()
    override val termSeparators: List<String>? get() = null

    override val minSize: Int get() = 0
    override val size: Int? get() = null
    override val elements: List<Nothing> get() = emptyList()
    override val trailingVar: ListTermVar? get() = this

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitVar(this)
    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitVar(this, arg)
}
