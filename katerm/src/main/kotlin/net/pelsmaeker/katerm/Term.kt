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

    fun <R> accept(visitor: TermVisitor<R>): R

    /**
     * Determines whether this term and its subterms are equivalent to the given term and its subterms.
     *
     * While [equals] checks that this term and the specified term have the same class,
     * this method merely checks that this term and the specified term represent the same value,
     * regardless of which implementation represents them.
     *
     * Note that the attachments are not checked by this method.
     */
    infix fun matches(that: Term): Boolean

    /**
     * Determines whether this term and its subterms are the same type and represent the same value
     * as the given term and it subterms. Even when this method returns `false`, [matches] may still
     * return `true`.
     *
     * Note that attachments are also checked by this method.
     *
     * This method may be used in tests to assert equality.
     */
    override fun equals(that: Any?): Boolean
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

    override infix fun matches(that: Term): Boolean {
        // Note that it does _not_ make sense to do a hashcode check here,
        // even if we know these classes implement the same class.
        // This is because some subterms might still match while having
        // different implementations/hashcodes, causing the super terms to
        // also have different hashcodes.
        if (this === that) return true              // Quick test: identity equality
        if (that !is ApplTerm) return false         // Must be same interface

        // @formatter:off
        return this.type == that.type               // Same type, constructor and arity
            && (this.args zip that.args).all { (a, b) -> a matches b }
        // @formatter:on
    }
}

/** A term with an associated value and no subterms. */
interface ValueTerm<T>: Term {
    /** The value of the term. */
    val value: T

    override val subterms: List<Term> get() = emptyList()
}

interface IntTerm : ValueTerm<Int> {
    override val value: Int
    override val type: IntTermType get() = IntTermType

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitInt(this)

    override infix fun matches(that: Term): Boolean {
        if (this === that) return true              // Quick test: identity equality
        if (that !is IntTerm) return false         // Must be same interface

        return this.value == that.value
    }
}

interface StringTerm : ValueTerm<String> {
    override val value: String
    override val type: StringTermType get() = StringTermType

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitString(this)

    override infix fun matches(that: Term): Boolean {
        if (this === that) return true              // Quick test: identity equality
        if (that !is StringTerm) return false         // Must be same interface

        return this.value == that.value
    }
}

interface BlobTerm : ValueTerm<Any> {
    override val value: Any
    override val type: BlobTermType get() = BlobTermType

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitBlob(this)

    override infix fun matches(that: Term): Boolean {
        if (this === that) return true              // Quick test: identity equality
        if (that !is BlobTerm) return false         // Must be same interface

        return this.value == that.value
    }
}

/** A list term. */
interface ListTerm : Term {
    /** The minimum number of elements in the list. */
    val minSize: Int
    /** The number of elements in the list; or `null` if the list ends with a variable. */
    val size: Int?
    /** The elements in the list. */
    val elements: List<Term>

    override val type: ListTermType
    override val subterms: List<Term> get() = elements

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitList(this)

    override infix fun matches(that: Term): Boolean {
        // Note that it does _not_ make sense to do a hashcode check here,
        // even if we know these classes implement the same class.
        // This is because some subterms might still match while having
        // different implementations/hashcodes, causing the super terms to
        // also have different hashcodes.
        if (this === that) return true              // Quick test: identity equality
        if (that !is ListTerm) return false         // Must be same interface

        // @formatter:off
        return this.size == that.size
            && this.type == that.type
            && (this.elements zip that.elements).all { (a, b) -> a matches b }
        // @formatter:on
    }
}

/** A non-empty list term. */
interface ConsTerm : ListTerm {
    /** The head of the list. */
    val head: Term
    /** The tail of the list, which may be a variable. */
    val tail: ListTerm

    override val minSize: Int get() = 1 + tail.minSize
    override val size: Int? get() = tail.size?.let { 1 + it }
    override val elements: List<Term> get() = listOf(head) + tail.elements  // TODO: Optimize
    // TODO: Visitor
    // TODO: Matches
}

/** An empty list term. */
interface NilTerm : ListTerm {
    override val minSize: Int get() = 0
    override val size: Int? get() = 0
    override val elements: List<Term> get() = emptyList()
    // TODO: Visitor
    // TODO: Matches
}

/** A list term variable. */
interface ListTermVar: ListTerm {
    /** The resource; or `null`. */
    val resource: String?
    /** The unique name. */
    val name: String

    override val type: ListTermType
    override val minSize: Int get() = 0
    override val size: Int? get() = null
    override val elements: List<Term> get() = emptyList() // TODO: This property should not exist?
    // TODO: Visitor
    // TODO: Matches
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

    override infix fun matches(that: Term): Boolean {
        if (this === that) return true              // Quick test: identity equality
        if (that !is TermVar) return false          // Must be same interface

        // @formatter:off
        return this.name == that.name
            && this.resource == that.resource
        // @formatter:on
    }
}


/**
 * Holds term attachments.
 */
interface TermAttachments {
    /** Whether the set of attachments is empty. */
    fun isEmpty(): Boolean
    /** Whether the set of attachments is not empty. */
    fun isNotEmpty(): Boolean = !isEmpty()
    /**
     * Gets the attachment with the specified key.
     *
     * @param key the key, the class of the attachment
     * @return the attachment, if found; otherwise, `null`
     */
    operator fun <T> get(key: Class<T>): T?

    companion object {
        fun empty(): TermAttachments = EmptyTermAttachments
    }

    private object EmptyTermAttachments: TermAttachments {
        override fun isEmpty(): Boolean = true
        override fun <T> get(key: Class<T>): T? = null
    }
}
