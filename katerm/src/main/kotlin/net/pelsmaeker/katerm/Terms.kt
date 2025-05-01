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
    val value: V

    override val termChildren: List<Term> get() = emptyList()
}

/** An integer number term. */
interface IntTerm : ValueTerm<Int> {
    override val value: Int

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitInt(this)
    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitInt(this, arg)
}

/** A real number term. */
interface RealTerm : ValueTerm<Double> {
    override val value: Double

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitReal(this)
    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitReal(this, arg)
}

/** A string term. */
interface StringTerm : ValueTerm<String> {
    override val value: String

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitString(this)
    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitString(this, arg)
}

/**
 * A list term.
 *
 * @property E The type of the elements in the list.
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


/**
 * An efficient implementation of a list term.
 *
 * @property content The contents of the list term.
 * @property startIndex The index of the first element in the list term.
 */
class ListTermView<out E: Term>(
    private val content: ListTermContent<E>,
    private val startIndex: Int,
) : ListTerm<E> {



    override val prefix: TermVar?
        get() = content.getVarAt(startIndex)
    override val head: E?
        get() = content.getElementAt(startIndex)
    override val tail: ListTerm<E>?
        get() = if (startIndex < content.size) ListTermView(content, startIndex + 1) else null
    override val minSize: Int
        get() = content.elementCount
    override val size: Int?
        get() = if (content.elementCount == content.size) content.size else null
    override val termChildren: List<Term>
        get() = object: AbstractList<Term>() {
            override val size: Int get() = content.size
            override fun get(index: Int): Term {
                require(index >= 0 && index < size) { "Index out of bounds: $index" }
                return content.getElementAt(index) ?: content.getVarAt(index)!!
            }
        }
    override val elements: List<E>
        get() = object: AbstractList<E>() {
            override val size: Int get() = content.elementCount
            override fun get(index: Int): E {
                require(index >= 0 && index < size) { "Index out of bounds: $index" }
                TODO()
            }
        }

    override val termAttachments: TermAttachments
        get() = content.getAttachmentAt(startIndex) ?: TermAttachments.empty()

    override fun equals(other: Any?): Boolean {
        TODO("Not yet implemented")
    }

}

/**
 * Efficiently encapulates the contents of a term list.
 *
 * @property elements The elements in the term list, where `null` indicates a term variable.
 * @property vars The term variables in the list, indexed by their position; or `null` if the list has no term variables.
 * @property attachments The attachments of the list (equal to the number of elements plus term variables); or `null` if the list has no term attachments.
 * @property separators The separators of the list (equal to three times the number of elements plus term variables); or `null` if the list has no term separators.
 */
class ListTermContent<out E: Term>(
    private val elements: Array<E?>,
    private val vars: Map<Int, TermVar>?,
    private val attachments: Array<TermAttachments>?,
) {
    /** The number of children of the list (the number of elements plus the number of term variables). */
    val size: Int get() = elements.size
    /** The number of term elements in the list. */
    val elementCount: Int get() = elements.size - (vars?.size ?: 0)

    /**
     * Returns the element at the specified index.
     *
     * @param index The index of the element to return.
     * @return The element at the specified index; or `null` if the element is a term variable.
     */
    fun getElementAt(index: Int): E? {
        assert(index >= 0 && index < size) { "Index out of bounds: $index" }
        return elements[index]
    }

    /**
     * Returns the term variable at the specified index.
     *
     * @param index The index of the term variable to return.
     * @return The term variable at the specified index; or `null` if there is no term variable at that index.
     */
    fun getVarAt(index: Int): TermVar? {
        assert(index >= 0 && index < size) { "Index out of bounds: $index" }
        return vars?.get(index)
    }

    /**
     * Returns the attachment at the specified index.
     *
     * @param index The index of the attachment to return.
     * @return The attachment at the specified index; or `null` if there is no attachment at that index.
     */
    fun getAttachmentAt(index: Int): TermAttachments? {
        assert(index >= 0 && index < size) { "Index out of bounds: $index" }
        return attachments?.get(index)
    }
}

/** A term variable. */
interface TermVar: Term {
    /** The variable name. Any resource names should be encoded as part of the variable name. */
    val name: String

    override val termChildren: List<Term> get() = emptyList()

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitVar(this)
    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitVar(this, arg)
}
