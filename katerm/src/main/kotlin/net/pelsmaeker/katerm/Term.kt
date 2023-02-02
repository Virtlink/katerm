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
}

/** A constructor application term. */
interface ApplTerm : Term {
    /** The type of the term. */
    override val type: ApplTermType
    /** The constructor name. */
    val op: String get() = type.op
    /** The term arguments. */
    val args: List<Term>

}

/** A term with an associated value and no subterms. */
sealed interface ValueTerm<T>: Term {
    val value: T
}

interface IntTerm : ValueTerm<Int> {
    /** The type of the term. */
    override val type: IntTermType get() = IntTermType

    override val value: Int
}

interface StringTerm : ValueTerm<String> {
    /** The type of the term. */
    override val type: StringTermType get() = StringTermType

    override val value: String
}

interface BlobTerm : ValueTerm<Any> {
    /** The type of the term. */
    override val type: BlobTermType get() = BlobTermType

    override val value: Any
}

/** A list term. */
sealed interface ListTerm : Term {
    /** The minimum number of elements in the list. */
    val minSize: Int
    /** The number of elements in the list; or `null` if the list ends with a variable. */
    val size: Int?
    /** The elements in the list. */
    val elements: List<Term>
    /** The type of the term. */
    override val type: ListTermType
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
    /** The type of the variable. */
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
    /** The type of the variable. */
    override val type: TermType
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
