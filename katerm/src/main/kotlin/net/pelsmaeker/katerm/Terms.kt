package net.pelsmaeker.katerm

/**
 * A term.
 *
 * Terms are immutable. To create or change a term, use a [TermBuilder].
 */
interface Term {
    /** The type of term. */
    val type: TermType
    /** The attachments of the term. */
    val attachments: TermAttachments
    /** A list of subterms. */
    val subterms: List<Term>

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
    val op: String get() = type.op
    /** The term arguments. */
    val args: List<Term>

    override val type: ApplTermType
    override val subterms: List<Term> get() = args

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitAppl(this)
    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitAppl(this, arg)
}

/** An integer term. */
interface IntTerm : Term {
    /** The value of the term. */
    val value: Int
    override val type: IntTermType get() = IntTermType
    override val subterms: List<Term> get() = emptyList()

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitInt(this)
    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitInt(this, arg)
}

/** A string term. */
interface StringTerm : Term {
    /** The value of the term. */
    val value: String
    override val type: StringTermType get() = StringTermType
    override val subterms: List<Term> get() = emptyList()

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitString(this)
    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitString(this, arg)
}

/** A blob term. */
interface BlobTerm : Term {
    /** The value of the term. */
    val value: Any
    override val type: BlobTermType get() = BlobTermType
    override val subterms: List<Term> get() = emptyList()

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitBlob(this)
    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitBlob(this, arg)
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

    override val type: ListTermType
    override val subterms: List<Term> get() = elements

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitList(this)
    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitList(this, arg)

    /**
     * Accepts a list term visitor.
     *
     * @param visitor the visitor to accept
     * @return the result returned by the visitor
     */
    fun <R> accept(visitor: ListTermVisitor<R>): R

    /**
     * Accepts a list term visitor.
     *
     * @param visitor the visitor to accept
     * @param arg the argument to pass to the visitor
     * @return the result returned by the visitor
     */
    fun <A, R> accept(visitor: ListTermVisitor1<A, R>, arg: A): R
}

/** A term variable. */
interface TermVar: Term {
    /** The resource; or `null`. */
    val resource: String?
    /** The unique name. */
    val name: String

    override val type: TermType
    override val subterms: List<Term> get() = emptyList()

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitVar(this)
    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitVar(this, arg)
}

/** A term list variable. */
interface ListTermVar: TermVar, ListTerm {
    override val type: ListTermType
    override val subterms: List<Term> get() = emptyList()

    override val minSize: Int get() = 0
    override val size: Int? get() = null
    override val elements: List<Term> get() = emptyList()
    override val trailingVar: ListTermVar? get() = this

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitVar(this)
    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitVar(this, arg)

    override fun <R> accept(visitor: ListTermVisitor<R>): R = visitor.visitListVar(this)
    override fun <A, R> accept(visitor: ListTermVisitor1<A, R>, arg: A): R = visitor.visitListVar(this, arg)
}

/** A non-empty list term. */
interface ConsTerm : ListTerm {
    /** The head of the list. */
    val head: Term
    /** The tail of the list, which may be a variable. */
    val tail: ListTerm

    override val elements: List<Term> get() = listOf(head) + tail.elements  // TODO: Optimize

    override fun <R> accept(visitor: ListTermVisitor<R>): R = visitor.visitCons(this)
    override fun <A, R> accept(visitor: ListTermVisitor1<A, R>, arg: A): R = visitor.visitCons(this, arg)
}

/** An empty list term. */
interface NilTerm : ListTerm {
    override val minSize: Int get() = 0
    override val size: Int? get() = 0
    override val elements: List<Term> get() = emptyList()
    override val trailingVar: ListTermVar? get() = null

    override fun <R> accept(visitor: ListTermVisitor<R>): R = visitor.visitNil(this)
    override fun <A, R> accept(visitor: ListTermVisitor1<A, R>, arg: A): R = visitor.visitNil(this, arg)
}


