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
    /** The constructor name. */
    val op: String
    /** The term arguments. */
    val args: List<Term>

}

/** A term with an associated value and no subterms. */
sealed interface ValueTerm<T>: Term {
    val value: T
}

interface IntTerm : ValueTerm<Int> {
    override val value: Int
}

interface StringTerm : ValueTerm<String> {
    override val value: String
}

interface BlobTerm : ValueTerm<Any> {
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
}

/** A non-empty list term. */
interface ConsTerm : ListTerm {
    /** The head of the list. */
    val head: Term
    /** The tail of the list, which may be a variable. */
    val tail: ListTerm
}

/** An empty list term. */
interface NilTerm : ListTerm {

}

/** A term variable. */
interface TermVar: Term, ListTerm {
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
}