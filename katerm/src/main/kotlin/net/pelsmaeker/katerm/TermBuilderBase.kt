package net.pelsmaeker.katerm

import net.pelsmaeker.katerm.attachments.TermAttachments
import net.pelsmaeker.katerm.io.DefaultTermWriter
import net.pelsmaeker.katerm.io.TermTextWriter
import net.pelsmaeker.katerm.substitutions.Substitution
import java.util.*

/**
 * Base class for term builders.
 *
 * @property termPrinter The term printer used for printing terms.
 */
abstract class TermBuilderBase(
    private val termPrinter: TermTextWriter = DefaultTermWriter(),
): TermBuilder {

    @Suppress("UNCHECKED_CAST")
    override fun <T : Term> withAttachments(term: T, newAttachments: TermAttachments): T {
        if (term.termAttachments == newAttachments) return term
        return when (term) {
            is ApplTerm -> newAppl(term.termOp, term.termArgs, newAttachments) as T
            is IntTerm -> newInt(term.value, newAttachments) as T
            is RealTerm -> newReal(term.value, newAttachments) as T
            is StringTerm -> newString(term.value, newAttachments) as T
            is SomeOptionTerm<*> -> newOption(term.element, newAttachments) as T
            is NoneOptionTerm -> newEmptyOption(newAttachments) as T
            is ConsListTerm<*> -> newList(term.head, term.tail, newAttachments) as T
            is NilListTerm -> newEmptyList(newAttachments) as T
            is ConcatListTerm<*> -> {
                require(newAttachments.isEmpty()) { "Concatenation terms cannot have attachments." }
                term
            }
            is TermVar -> newVar(term.name, newAttachments) as T
            else -> throw IllegalArgumentException("Unknown term type: $term")
        }
    }


    /////////
    // Int //
    /////////

    final override fun newInt(value: Int, attachments: TermAttachments): IntTerm {
        return IntTermImpl(value, attachments)
    }

    final override fun copyInt(term: IntTerm, newValue: Int): IntTerm {
        if (term.value == newValue) return term
        return newInt(newValue, term.termAttachments)
    }


    /////////
    // Real //
    /////////

    final override fun newReal(value: Double, attachments: TermAttachments): RealTerm {
        return RealTermImpl(value, attachments)
    }

    final override fun copyReal(term: RealTerm, newValue: Double): RealTerm {
        if (term.value == newValue) return term
        return newReal(newValue, term.termAttachments)
    }


    ////////////
    // String //
    ////////////

    final override fun newString(value: String, attachments: TermAttachments): StringTerm {
        return StringTermImpl(value, attachments)
    }

    final override fun copyString(term: StringTerm, newValue: String): StringTerm {
        if (term.value == newValue) return term
        return newString(newValue, term.termAttachments)
    }


    //////////
    // Appl //
    //////////

    abstract override fun newAppl(op: String, args: List<Term>, attachments: TermAttachments): ApplTerm

    override fun copyAppl(term: ApplTerm, newArgs: List<Term>): ApplTerm {
        // Can be overridden to provide a more efficient implementation.
        if (term.termArgs == newArgs) return term
        return newAppl(term.termOp, newArgs, term.termAttachments)
    }


    ////////////
    // Option //
    ////////////

    final override fun newEmptyOption(attachments: TermAttachments): NoneOptionTerm {
        return NoneOptionTermImpl(attachments)
    }

    final override fun <E: Term> newOption(element: E?, attachments: TermAttachments): OptionTerm<E> {
        return when (element) {
            null -> newEmptyOption(attachments)
            else -> SomeOptionTermImpl(element, attachments)
        }
    }

    final override fun <E: Term> copyOption(term: OptionTerm<E>, newElement: E?): OptionTerm<E> {
        if (term is SomeOptionTerm<E> && term.element == newElement) return term
        return newOption(newElement, term.termAttachments)
    }


    //////////
    // List //
    //////////

    final override fun newEmptyList(attachments: TermAttachments): NilListTerm {
        return NilListTermImpl(attachments)
    }

    final override fun <T : Term> newListOf(elements: List<T>): ListTerm<T> {
        return if (elements.isNotEmpty()) {
            newList(elements.first(), newListOf(elements.drop(1)))
        } else {
            newEmptyList()
        }
    }

    final override fun <E : Term> newList(head: E, tail: ListTerm<E>, attachments: TermAttachments): ListTerm<E> {
        return ConsListTermImpl(head, tail, attachments)
    }

    final override fun <E : Term> copyList(term: ListTerm<E>, newHead: E, newTail: ListTerm<E>): ListTerm<E> {
        if (term is ConsListTerm<*> && term.head == newHead && term.tail == newTail) return term
        return newList(newHead, newTail, term.termAttachments)
    }

    final override fun <E: Term> concatLists(leftList: ListTerm<E>, rightList: ListTerm<E>): ListTerm<E> {
        return if (rightList is NilListTerm) {
            // as ++ [] = as
            leftList
        } else {
            when (leftList) {
                // (a :: as) ++ bs = a :: (as ++ bs)
                is ConsListTerm<E> -> newList(leftList.head, concatLists(leftList.tail, rightList), leftList.termAttachments)

                // [] ++ bs = bs
                is NilListTerm -> rightList

                // ?xs ++ bs = ?xs ++ bs
                is TermVar -> ConcatListTermImpl(leftList, rightList)

                // (xs ++ as) ++ bs == xs ++ (as ++ bs)
                is ConcatListTerm<E> -> ConcatListTermImpl<E>(leftList.left, concatLists(leftList.right, rightList))
            }
        }
    }


    /////////
    // Var //
    /////////

    final override fun newVar(name: String, attachments: TermAttachments): TermVar {
        return TermVarImpl(name, attachments)
    }

    final override fun copyVar(term: TermVar, newName: String): TermVar {
        return newVar(newName, term.termAttachments)
    }


    //////////////////
    // Term Classes //
    //////////////////

    // The classes here are protected and private to prevent them from being instantiated or used outside of this class.
    // Instead, the base interfaces should be used.

    /**
     * Base class for term implementations.
     *
     * @property termAttachments The attachments of the term.
     */
    protected abstract inner class TermImplBase(
        override val termAttachments: TermAttachments,
    ): Term {

        private var _termVars: Set<TermVar>? = null
        override val termVars: Set<TermVar>
            get() = _termVars ?: termChildren.flatMapTo(HashSet()) { it.termVars }.also { _termVars = it }

        /**
         * Determines whether this term and its subterms represent the same value
         * as the given term and it subterms, regardless of the actual implementations
         * of the terms and its subterms.
         *
         * Note that attachments are also checked by this method.
         *
         * Implementations should compare equal to other implementations of the same term type,
         * but can take shortcuts when comparing to the same implementation of the term type.
         *
         * @param other The other object to compare to.
         * @return `true` if the terms are equal; otherwise, `false`.
         */
        final override fun equals(other: Any?): Boolean {
            if (this === other) return true                     // Identity equality
            val that = other as? Term ?: return false           // Must be a Term
            return equals(that)
        }

        /**
         * Returns a hash code value for the term, including its subterms and attachments.
         *
         * This cannot be overridden. Instead, implement the [hashCode] function and perform an eager
         * hash code calculation for the subterms when the term is constructed.
         *
         * @return A hash code value for the term, including its subterms and attachments.
         */
        final override fun hashCode(): Int = hashCode(compareSubterms = true, compareAttachments = true)

        abstract override fun hashCode(compareSubterms: Boolean, compareAttachments: Boolean): Int

        /**
         * Returns a string representation of this term.
         *
         * Override this method to customize the string representation.
         */
        override fun toString(): String {
            return this@TermBuilderBase.termPrinter.writeToString(this)
        }
    }

    /**
     * Constructor application term base class.
     */
    protected abstract inner class ApplTermBase(
        termAttachments: TermAttachments,
    ) : ApplTerm, TermImplBase(termAttachments) {

        /** The constructor name. */
        abstract override val termOp: String

        /**
         * Override this property to optimize specifying the arity.
         */
        open override val termArity: Int get() = termArgs.size

        abstract override val termArgs: List<Term>

        final override val termKind: TermKind get() = TermKind.APPL

        final override val termChildren: List<Term> get() = termArgs

        override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitAppl(this)

        override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitAppl(this, arg)

        final override fun equals(that: Term, compareSubterms: Boolean, compareAttachments: Boolean): Boolean {
            if (that !is ApplTerm) return false
            if (that is ApplTermBase && this.hashCode(compareSubterms, compareAttachments) != that.hashCode(compareSubterms, compareAttachments)) return false
            if (this.termArity != that.termArity) return false
            if (this.termOp != that.termOp) return false
            if (compareSubterms && !this.equalSubterms(that, compareAttachments)) return false
            if (compareAttachments && (this.termAttachments != that.termAttachments)) return false
            return true
        }

        /** Override this field to optimize the hash code calculation. */
        protected open val subtermHash: Int = Objects.hash(termArgs)

        override fun hashCode(compareSubterms: Boolean, compareAttachments: Boolean): Int {
            return Objects.hash(
                this::class.java,                                   // Hash the class name
                termOp,
                termArity,
                if (compareSubterms) subtermHash else 0,            // Use the precomputed subterm hash if comparing subterms
                if (compareAttachments) termAttachments else null
            )
        }

        /**
         * Checks whether this term and the given term have equal subterms.
         *
         * Override this method to customize or optimize the equality check.
         *
         * @param that The term to check.
         * @param compareAttachments Whether to compare the attachments.
         * @return `true` if this term has equal subterms as the specified term; otherwise, `false`.
         */
        protected open fun equalSubterms(that: ApplTerm, compareAttachments: Boolean): Boolean {
            return (this.termArgs zip that.termArgs).all { (a, b) -> a.equals(b, compareSubterms = true, compareAttachments) }
        }

        override fun toString(): String {
            return "$termOp(${termArgs.joinToString(", ")})"
        }
    }

    /**
     * An integer value term.
     *
     * @property value The integer value.
     * @param termAttachments The attachments of the term.
     */
    private inner class IntTermImpl(
        override val value: Int,
        termAttachments: TermAttachments,
    ) : IntTerm, TermImplBase(termAttachments) {

        override val termKind: TermKind get() = TermKind.VALUE_INT

        override val termChildren: List<Term> get() = emptyList()

        override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitInt(this)

        override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitInt(this, arg)

        override fun equals(that: Term, compareSubterms: Boolean, compareAttachments: Boolean): Boolean {
            if (that !is IntTerm) return false
            if (that is IntTermImpl && this.hashCode(compareSubterms, compareAttachments) != that.hashCode(compareSubterms, compareAttachments)) return false
            if (compareAttachments && (this.termAttachments != that.termAttachments)) return false
            return true
        }

        override fun hashCode(compareSubterms: Boolean, compareAttachments: Boolean): Int {
            return Objects.hash(
                this::class.java,
                value,
                if (compareAttachments) termAttachments else null
            )
        }

        override fun toString(): String {
            return value.toString()
        }

    }

    /**
     * A real value term.
     *
     * @property value The real value.
     * @param termAttachments The attachments of the term.
     */
    private inner class RealTermImpl(
        override val value: Double,
        termAttachments: TermAttachments,
    ) : RealTerm, TermImplBase(termAttachments) {

        override val termKind: TermKind get() = TermKind.VALUE_REAL

        override val termChildren: List<Term> get() = emptyList()

        override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitReal(this)

        override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitReal(this, arg)

        override fun equals(that: Term, compareSubterms: Boolean, compareAttachments: Boolean): Boolean {
            if (that !is RealTerm) return false
            if (that is RealTermImpl && this.hashCode(compareSubterms, compareAttachments) != that.hashCode(compareSubterms, compareAttachments)) return false
            if (compareAttachments && (this.termAttachments != that.termAttachments)) return false
            return true
        }

        override fun hashCode(compareSubterms: Boolean, compareAttachments: Boolean): Int {
            return Objects.hash(
                this::class.java,
                value,
                if (compareAttachments) termAttachments else null
            )
        }

        override fun toString(): String {
            return value.toString()
        }

    }

    /**
     * A string value term.
     *
     * @property value The string value.
     * @param termAttachments The attachments of the term.
     */
    private inner class StringTermImpl(
        override val value: String,
        termAttachments: TermAttachments,
    ) : StringTerm, TermImplBase(termAttachments) {

        override val termKind: TermKind get() = TermKind.VALUE_STRING

        override val termChildren: List<Term> get() = emptyList()

        override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitString(this)

        override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitString(this, arg)

        override fun equals(that: Term, compareSubterms: Boolean, compareAttachments: Boolean): Boolean {
            if (that !is StringTerm) return false
            if (that is StringTermImpl && this.hashCode(compareSubterms, compareAttachments) != that.hashCode(compareSubterms, compareAttachments)) return false
            if (compareAttachments && (this.termAttachments != that.termAttachments)) return false
            return true
        }

        override fun hashCode(compareSubterms: Boolean, compareAttachments: Boolean): Int {
            return Objects.hash(
                this::class.java,
                value,
                if (compareAttachments) termAttachments else null
            )
        }

        override fun toString(): String {
            return "\"${escape(value)}\""
        }

        private fun escape(s: String): String = s
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")

    }

    /**
     * A non-empty option term.
     *
     * @param E The type of the element in the option.
     * @param element The term that is the element of the option.
     * @param termAttachments The attachments of the term.
     */
    private inner class SomeOptionTermImpl<E: Term>(
        override val element: E,
        termAttachments: TermAttachments,
    ) : SomeOptionTerm<E>, TermImplBase(termAttachments) {

        override fun isEmpty(): Boolean = false

        override fun isNotEmpty(): Boolean = true

        override val termKind: TermKind get() = TermKind.OPTION_SOME

        override val termChildren: List<Term> get() = listOf(element)

        override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitSomeOption(this)

        override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitSomeOption(this, arg)

        override fun equals(that: Term, compareSubterms: Boolean, compareAttachments: Boolean): Boolean {
            if (that !is SomeOptionTerm<*>) return false
            if (that is SomeOptionTermImpl<*> && this.hashCode(compareSubterms, compareAttachments) != that.hashCode(compareSubterms, compareAttachments)) return false
            if (compareSubterms) {
                if (!this.element.equals(that.element, compareSubterms = true, compareAttachments)) return false
            }
            if (compareAttachments && (this.termAttachments != that.termAttachments)) return false
            return true
        }

        private val subtermHash: Int = Objects.hash(element)

        override fun hashCode(compareSubterms: Boolean, compareAttachments: Boolean): Int {
            return Objects.hash(
                this::class.java,                                   // Hash the class name to make minimal hash of SomeOptionTerm and NoneOptionTerm different
                if (compareSubterms) subtermHash else 0,
                if (compareAttachments) termAttachments else null
            )
        }

        override fun toString(): String {
            return "<${this.element}>"
        }

    }

    /**
     * An empty option term.
     *
     * @param termAttachments The attachments of the term.
     */
    private inner class NoneOptionTermImpl(
        termAttachments: TermAttachments,
    ) : NoneOptionTerm, TermImplBase(termAttachments) {

        override fun isEmpty(): Boolean = true

        override fun isNotEmpty(): Boolean = false

        override val termKind: TermKind get() = TermKind.OPTION_NONE

        override val termChildren: List<Term> get() = emptyList()

        override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitNoneOption(this)

        override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitNoneOption(this, arg)

        override fun equals(that: Term, compareSubterms: Boolean, compareAttachments: Boolean): Boolean {
            if (that !is NoneOptionTerm) return false
            if (that is NoneOptionTermImpl && this.hashCode(compareSubterms, compareAttachments) != that.hashCode(compareSubterms, compareAttachments)) return false
            if (compareAttachments && (this.termAttachments != that.termAttachments)) return false
            return true
        }

        override fun hashCode(compareSubterms: Boolean, compareAttachments: Boolean): Int {
            return Objects.hash(
                this::class.java,                                   // Hash the class name to make minimal hash of SomeOptionTerm and NoneOptionTerm different
                if (compareAttachments) termAttachments else null
            )
        }

        override fun toString(): String {
            return "<>"
        }
    }


    /**
     * List cons term (a list head with a tail).
     *
     * @property head The head of the list.
     * @property tail The tail of the list. It cannot be a concatenation.
     */
    private inner class ConsListTermImpl<E: Term>(
        override val head: E,
        override val tail: ListTerm<E>,
        termAttachments: TermAttachments,
    ) : ConsListTerm<E>, TermImplBase(termAttachments) {

        init {
            require(tail !is ConcatListTerm<E>) {
                "The tail list must not be a concatenation."
            }
        }

        override val termChildren: List<Term> get() = listOf(head, tail)

        override val termKind: TermKind get() = TermKind.LIST_CONS

        override fun isEmpty(): Boolean = false

        override fun isNotEmpty(): Boolean = true

        override val minSize: Int get() = 1 + tail.minSize

        override val size: Int? get() = tail.size?.let { 1 + it }

        override val elements: List<E> get() = listOf(head) + tail.elements

        override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitConsList(this)

        override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitConsList(this, arg)

        override fun equals(that: Term, compareSubterms: Boolean, compareAttachments: Boolean): Boolean {
            if (that !is ConsListTerm<*>) return false
            if (that is ConsListTermImpl<*> && this.hashCode(compareSubterms, compareAttachments) != that.hashCode(compareSubterms, compareAttachments)) return false
            if (compareSubterms) {
                if (!this.head.equals(that.head, compareSubterms = true, compareAttachments)) return false
                if (!this.tail.equals(that.tail, compareSubterms = true, compareAttachments)) return false
            }
            if (compareAttachments && (this.termAttachments != that.termAttachments)) return false
            return true
        }

        private val subtermHash: Int = Objects.hash(head, tail)

        override fun hashCode(compareSubterms: Boolean, compareAttachments: Boolean): Int {
            return Objects.hash(
                this::class.java,                                   // Hash the class name
                if (compareSubterms) subtermHash else 0,
                if (compareAttachments) termAttachments else null
            )
        }

        override fun toString(): String = buildString {
            append("[")
            append(head)
            var current: ListTerm<Term>? = tail
            while (current != null) {
                if (current.isEmpty()) {
                    append("]")
                    return@buildString
                } else if (current is ConsListTerm<*>) {
                    append(", ")
                    append(current.head)
                    current = current.tail
                } else if (current is TermVar) {
                    append(" | ")
                    append(current)
                    append("]")
                    return@buildString
                } else {
                    append("]")
                    append(current)
                    break
                }
            }
            append("]")
        }
    }

    /**
     * An empty list.
     *
     * @param termAttachments The attachments of the term.
     */
    private inner class NilListTermImpl(
        termAttachments: TermAttachments,
    ) : NilListTerm, TermImplBase(termAttachments) {

        override val termChildren: List<Term> get() = emptyList()

        override val termKind: TermKind get() = TermKind.LIST_NIL

        override fun isEmpty(): Boolean = true

        override fun isNotEmpty(): Boolean = false

        override val minSize: Int get() = 0

        override val size: Int get() = 0

        override val elements: List<Nothing> get() = emptyList()

        override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitNilList(this)

        override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitNilList(this, arg)

        override fun equals(that: Term, compareSubterms: Boolean, compareAttachments: Boolean): Boolean {
            if (that !is NilListTerm) return false
            if (that is NilListTermImpl && this.hashCode(compareSubterms, compareAttachments) != that.hashCode(compareSubterms, compareAttachments)) return false
            if (compareAttachments && (this.termAttachments != that.termAttachments)) return false
            return true
        }

        override fun hashCode(compareSubterms: Boolean, compareAttachments: Boolean): Int {
            return Objects.hash(
                this::class.java,                                   // Hash the class name
                if (compareAttachments) termAttachments else null
            )
        }

        override fun toString(): String {
            return "[]"
        }
    }

    /**
     * A concatenation of two lists.
     *
     * A concatenation cannot have term attachments.
     *
     * @property left The left list.
     * @property right The right list.
     */
    private inner class ConcatListTermImpl<E: Term>(
        override val left: ListTerm<E>,
        override val right: ListTerm<E>,
    ) : ConcatListTerm<E>, TermImplBase(TermAttachments.empty()) {

        init {
            require(left is TermVar || (left is ConsListTerm<E> && left.tail is TermVar)) {
                "The left list must be a variable or have a variable as the tail."
            }
            require(right.size != 0) {
                "The right list must not be empty."
            }
        }

        override val termChildren: List<Term> get() = listOf(left, right)

        override val termKind: TermKind get() = TermKind.LIST_CONCAT

        override fun isEmpty(): Boolean = false

        override fun isNotEmpty(): Boolean = left.isNotEmpty() && right.isNotEmpty()

        override val minSize: Int get() = left.minSize + right.minSize

        override val size: Int? get() = null

        override val elements: List<E> get() = left.elements + right.elements

        override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitConcatList(this)

        override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitConcatList(this, arg)

        override fun equals(that: Term, compareSubterms: Boolean, compareAttachments: Boolean): Boolean {
            if (that !is ConcatListTerm<*>) return false
            if (that is ConcatListTermImpl<*> && this.hashCode(compareSubterms, compareAttachments) != that.hashCode(compareSubterms, compareAttachments)) return false
            if (compareSubterms) {
                if (!this.left.equals(that.left, compareSubterms = true, compareAttachments)) return false
                if (!this.right.equals(that.right, compareSubterms = true, compareAttachments)) return false
            }
            if (compareAttachments && (this.termAttachments != that.termAttachments)) return false
            return true
        }

        private val subtermHash: Int = Objects.hash(left, right)

        override fun hashCode(compareSubterms: Boolean, compareAttachments: Boolean): Int {
            return Objects.hash(
                this::class.java,                                   // Hash the class name
                if (compareSubterms) subtermHash else 0,
                if (compareAttachments) termAttachments else null
            )
        }

        override fun toString(): String {
            return "$left ++ $right"
        }
    }

    /**
     * A term variable.
     *
     * @property name The variable name. Any resource names should be encoded as part of the variable name.
     */
    private inner class TermVarImpl(
        override val name: String,
        termAttachments: TermAttachments,
    ) : TermVar, TermImplBase(termAttachments) {

        override val termKind: TermKind get() = TermKind.VAR

        override val termChildren: List<Term> get() = emptyList()

        override val termVars: Set<TermVar> get() = setOf(this)

        override fun isEmpty(): Boolean = false

        override fun isNotEmpty(): Boolean = false

        override val minSize: Int get() = 0

        override val size: Int? get() = null

        override val elements: List<Nothing> get() = emptyList()

        override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitVar(this)

        override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitVar(this, arg)

        override fun equals(that: Term, compareSubterms: Boolean, compareAttachments: Boolean): Boolean {
            if (that !is TermVar) return false
            if (that is TermVarImpl && this.hashCode(compareSubterms, compareAttachments) != that.hashCode(compareSubterms, compareAttachments)) return false
            if (compareAttachments && (this.termAttachments != that.termAttachments)) return false
            return true
        }

        override fun hashCode(compareSubterms: Boolean, compareAttachments: Boolean): Int {
            return Objects.hash(
                this::class.java,                                   // Hash the class name
                name,
                if (compareAttachments) termAttachments else null
            )
        }

        override fun toString(): String {
            return "?$name"
        }

    }

}