package net.pelsmaeker.katerm

/**
 * A term.
 *
 * Terms are immutable. To create or change a term, use a [TermBuilder].
 */
sealed interface Term {
    /** The type of term. */
    val type: TermType
    /** The attachments of the term. */
    val attachments: TermAttachments
    /** A list of subterms. */
    val subterms: List<Term>
}

/** A constructor application term. */
interface ApplTerm : Term {
    /** The constructor name. */
    val op: String get() = type.op
    /** The term arguments. */
    val args: List<Term>

    override val type: ApplTermType
    override val subterms: List<Term> get() = args
}

/** A term with an associated value and no subterms. */
sealed interface ValueTerm<T>: Term {
    /** The value of the term. */
    val value: T

    override val subterms: List<Term> get() = emptyList()
}

interface IntTerm : ValueTerm<Int> {
    override val value: Int
    override val type: IntTermType get() = IntTermType
}

interface StringTerm : ValueTerm<String> {
    override val value: String
    override val type: StringTermType get() = StringTermType
}

interface BlobTerm : ValueTerm<Any> {
    override val value: Any
    override val type: BlobTermType get() = BlobTermType
}

/** A list term. */
sealed interface ListTerm : Term {
    /** The minimum number of elements in the list. */
    val minSize: Int
    /** The number of elements in the list; or `null` if the list ends with a variable. */
    val size: Int?
    /** The elements in the list. */
    val elements: List<Term>

    override val type: ListTermType
    override val subterms: List<Term> get() = elements
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
}

/** An empty list term. */
interface NilTerm : ListTerm {
    override val minSize: Int get() = 0
    override val size: Int? get() = 0
    override val elements: List<Term> get() = emptyList()
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
}

/** A term variable. */
interface TermVar: Term {
    /** The resource; or `null`. */
    val resource: String?
    /** The unique name. */
    val name: String

    override val type: TermType
    override val subterms: List<Term> get() = emptyList()
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
