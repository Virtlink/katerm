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

    override fun apply(substitution: Substitution, term: Term): Term {
        return when (term) {
            is TermVar -> {
                val mappedTerm = substitution[term]
                if (mappedTerm !is TermVar) {
                    // Also substitute in the mapped term.
                    apply(substitution, mappedTerm)
                } else term
            }
            is ApplTerm -> copyAppl(term, term.termArgs.map { apply(substitution, it) })
            is OptionTerm<*> -> {
                if (term.variable != null) { TODO("Variables in OptionTerm are not yet supported.") }
                copyOption(term, term.element?.let { apply(substitution, it) })
            }
            is ListTerm<*> -> {
                if (term.prefix != null) { TODO("Variables in ListTerm are not yet supported.") }
                copyList(term, term.elements.map { apply(substitution, it) })
            }
            else -> term
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Term> withAttachments(term: T, newAttachments: TermAttachments): T {
        if (term.termAttachments == newAttachments) return term
        return when (term) {
            is IntTerm -> newInt(term.value, newAttachments) as T
            is RealTerm -> newReal(term.value, newAttachments) as T
            is StringTerm -> newString(term.value, newAttachments) as T
            is ApplTerm -> newAppl(term.termOp, term.termArgs, newAttachments) as T
            is OptionTerm<*> -> {
                if (term.variable != null) { TODO("Variables in OptionTerm are not yet supported.") }
                newOption(term.element, newAttachments) as T
            }
            is ListTerm<*> -> {
                if (term.prefix != null) { TODO("Variables in ListTerm are not yet supported.") }
                newList(term.elements, newAttachments) as T
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

    open override fun copyAppl(term: ApplTerm, newArgs: List<Term>): ApplTerm {
        // Can be overridden to provide a more efficient implementation.
        if (term.termArgs == newArgs) return term
        return newAppl(term.termOp, newArgs, term.termAttachments)
    }


    ////////////
    // Option //
    ////////////

    override fun <E: Term> newOption(element: E?, attachments: TermAttachments): OptionTerm<E> {
        return when (element) {
            null -> NoneTermImpl(attachments) as OptionTerm<E>
            else -> SomeTermImpl(element, attachments)
        }
    }

    override fun <E : Term> newOptionWithVar(variable: TermVar?, attachments: TermAttachments): OptionTerm<E> {
        return when (variable) {
            null -> NoneTermImpl(attachments) as OptionTerm<E>
            else -> OptTermImpl(variable, attachments)
        }
    }

    override fun <E: Term> copyOption(term: OptionTerm<E>, newElement: E?): OptionTerm<E> {
        if (term.element == newElement) return term
        return newOption(newElement, term.termAttachments)
    }

    override fun <E : Term> copyOptionWithVar(term: OptionTerm<E>, newVariable: TermVar?): OptionTerm<E> {
        if (term.variable == newVariable) return term
        return newOptionWithVar(newVariable, term.termAttachments)
    }


    //////////
    // List //
    //////////

    override fun <T : Term> newList(
        elements: List<T>,
        attachments: TermAttachments,
    ): ListTerm<T> {
        return if (elements.isNotEmpty()) {
            // TODO: Enforce that all elements of a list share the same separators and attachments?
            //  This would mean: no term sharing, or copying a tail of a list to another list

            ConsTermImpl(elements.first(), newList(elements.drop(1)), attachments)
        } else {
            NilTermImpl(attachments)
        }
    }

    override fun <E : Term> newList(head: E, tail: ListTerm<E>, attachments: TermAttachments): ListTerm<E> {
        return ConsTermImpl(head, tail, attachments)
    }

    override fun <E : Term> newListWithVar(
        variable: TermVar,
        tail: ListTerm<E>,
        attachments: TermAttachments,
    ): ListTerm<E> {
        return ConcTermImpl(variable, tail, attachments)
    }

    override fun <E : Term> copyList(term: ListTerm<E>, newElements: List<E>): ListTerm<E> {
        if (term.elements == newElements) return term
        return newList(newElements, term.termAttachments)
    }

    override fun <E : Term> copyList(term: ListTerm<E>, newHead: E, newTail: ListTerm<E>): ListTerm<E> {
        if (term.head == newHead && term.tail == newTail) return term
        return newList(newHead, newTail, term.termAttachments)
    }

    override fun <E : Term> copyListWithVar(term: ListTerm<E>, newPrefix: TermVar, newTail: ListTerm<E>): ListTerm<E> {
        if (term.prefix == newPrefix && term.tail == newTail) return term
        return newListWithVar(newPrefix, newTail, term.termAttachments)
    }


    /////////
    // Var //
    /////////

    override fun newVar(name: String, attachments: TermAttachments): TermVar {
        return TermVarImpl(name, attachments)
    }

    override fun copyVar(term: TermVar, newName: String): TermVar {
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
    @Suppress("EqualsOrHashCode")
    protected abstract inner class TermImplBase(
        override val termAttachments: TermAttachments,
    ): Term {

        private var _termVars: Set<TermVar>? = null
        final override val termVars: Set<TermVar>
            get() = _termVars ?: termChildren.flatMapTo(HashSet()) { it.termVars }.also { _termVars = it }

        /**
         * An eager hash code calculation.
         *
         * Implement this field to compute and store the hash code of the term when the object is created.
         * Do not include the hash code of the attachments.
         */
        protected abstract val hash: Int

        /**
         * Returns the hash code of the term, include the hash of the attachments.
         *
         * This cannot be overridden. Instead, implement the [hash] property
         * by performing an eager hash calculation that includes the hash of the attachments.
         */
        final override fun hashCode(): Int = hash

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

        abstract override val termArgs: List<Term>

        final override val termKind: TermKind get() = TermKind.APPL

        final override fun equals(that: Term, compareSubterms: Boolean, compareAttachments: Boolean): Boolean {
            if (that !is ApplTerm) return false
            // @formatter:off
            return (that !is ApplTermBase || this.hash == that.hash)
                && this.termArity == that.termArity
                && this.termOp == that.termOp
                && (!compareSubterms || this.equalsSubterms(that, compareAttachments))
                && (!compareAttachments || (this.termAttachments == that.termAttachments))
            // @formatter:on
        }

        /**
         * Override this method to optimize the hash code calculation.
         */
        open override val hash: Int = Objects.hash(termOp, termArgs)

        /**
         * Checks whether this term and the given term have equal subterms.
         *
         * Override this method to customize or optimize the equality check.
         *
         * @param that The term to check.
         * @param compareAttachments Whether to compare the attachments.
         * @return `true` if this term has equal subterms as the specified term; otherwise, `false`.
         */
        protected open fun equalsSubterms(that: ApplTerm, compareAttachments: Boolean): Boolean {
            return (this.termArgs zip that.termArgs).all { (a, b) -> a.equals(
                b,
                compareSubterms = true,
                compareAttachments = compareAttachments
            ) }
        }

        override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitAppl(this)

        override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitAppl(this, arg)

    }

    protected abstract inner class ValueTermImplBase(
        termAttachments: TermAttachments,
    ) : ValueTerm, TermImplBase(termAttachments) {

        /**
         * Override this method to optimize the hash code calculation.
         */
        open override val hash: Int = Objects.hash(value)

        /**
         * Override this method to customize or optimize the equality check.
         */
        open override fun equals(that: Term, compareSubterms: Boolean, compareAttachments: Boolean): Boolean {
            if (that !is ValueTerm) return false
            // @formatter:off
            return (that !is ValueTermImplBase || this.hash == that.hash)
                && this.value == that.value
                && (!compareAttachments || (this.termAttachments == that.termAttachments))
            // @formatter:on
        }

    }

    /** Integer value term. */
    private inner class IntTermImpl(
        override val value: Int,
        termAttachments: TermAttachments,
    ) : IntTerm, ValueTermImplBase(termAttachments) {

        final override val termKind: TermKind get() = TermKind.INT

        override val hash: Int = Objects.hash(value)

        override fun equals(that: Term, compareSubterms: Boolean, compareAttachments: Boolean): Boolean {
            if (that !is IntTerm) return false
            // @formatter:off
            return (that !is IntTermImpl || this.hash == that.hash)
                && this.value == that.value
                && (!compareAttachments || (this.termAttachments == that.termAttachments))
            // @formatter:on
        }

        override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitInt(this)

        override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitInt(this, arg)
    }

    /** Real value term. */
    private inner class RealTermImpl(
        override val value: Double,
        termAttachments: TermAttachments,
    ) : RealTerm, ValueTermImplBase(termAttachments) {

        final override val termKind: TermKind get() = TermKind.REAL

        override val hash: Int = Objects.hash(value)

        override fun equals(that: Term, compareSubterms: Boolean, compareAttachments: Boolean): Boolean {
            if (that !is RealTerm) return false
            // @formatter:off
            return (that !is RealTermImpl || this.hash == that.hash)
                && this.value == that.value
                && (!compareAttachments || (this.termAttachments == that.termAttachments))
            // @formatter:on
        }

        override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitReal(this)

        override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitReal(this, arg)

    }

    /** String value term base class. */
    private inner class StringTermImpl(
        override val value: String,
        termAttachments: TermAttachments,
    ) : StringTerm, ValueTermImplBase(termAttachments) {

        final override val termKind: TermKind get() = TermKind.STRING

        override val hash: Int = Objects.hash(value)

        override fun equals(that: Term, compareSubterms: Boolean, compareAttachments: Boolean): Boolean {
            if (that !is StringTerm) return false
            // @formatter:off
            return (that !is StringTermImpl || this.hash == that.hash)
                && this.value == that.value
                && (!compareAttachments || (this.termAttachments == that.termAttachments))
            // @formatter:on
        }

        override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitString(this)

        override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitString(this, arg)
    }

    /** Base class for option terms. */
    private abstract inner class OptionTermImplBase<T: Term>(
        termAttachments: TermAttachments,
    ): OptionTerm<T>, TermImplBase(termAttachments) {

        final override val termKind: TermKind get() = TermKind.OPTION

        final override fun equals(that: Term, compareSubterms: Boolean, compareAttachments: Boolean): Boolean {
            if (that !is OptionTerm<*>) return false
            // @formatter:off
            return (that !is OptionTermImplBase<*> || this.hash == that.hash)
                && (!compareSubterms || this.element.equals(that.element, compareSubterms, compareAttachments))
                && (!compareSubterms || this.variable.equals(that.variable, compareSubterms, compareAttachments))
                && (!compareAttachments || (this.termAttachments == that.termAttachments))
            // @formatter:on
        }

        final override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitOption(this)

        final override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitOption(this, arg)

    }

    /** Option term with a value. */
    private inner class SomeTermImpl<E: Term>(
        override val element: E,
        termAttachments: TermAttachments,
    ) : OptionTerm<E>, OptionTermImplBase<E>(termAttachments) {

        override val variable: TermVar? get() = null

        override val termChildren: List<Term> get() = listOf(element)

        override val hash: Int = Objects.hash(element)

    }

    /** Option term without a value */
    private inner class NoneTermImpl(
        termAttachments: TermAttachments,
    ) : OptionTerm<Nothing>, OptionTermImplBase<Nothing>(termAttachments) {

        override val element: Nothing? get() = null

        override val variable: TermVar? get() = null

        override val termChildren: List<Term> get() = emptyList()

        override val hash: Int = 0
    }

    /** Option term with a variable. */
    private inner class OptTermImpl<E: Term>(
        override val variable: TermVar,
        termAttachments: TermAttachments = TermAttachments.empty(),
    ) : OptionTerm<E>, OptionTermImplBase<E>(termAttachments) {

        override val element: E? get() = null

        override val termChildren: List<Term> get() = listOf(variable)

        override val hash: Int = Objects.hash(variable)
    }

    /** Base class for list terms. */
    @Suppress("EqualsOrHashCode")
    private abstract inner class ListTermImplBase<T: Term>(
        termAttachments: TermAttachments,
    ): ListTerm<T>, TermImplBase(termAttachments) {

        final override val termKind: TermKind get() = TermKind.LIST

        final override fun equals(that: Term, compareSubterms: Boolean, compareAttachments: Boolean): Boolean {
            if (that !is ListTerm<*>) return false
            // @formatter:off
            return (that !is ListTermImplBase<*> || this.hash == that.hash)
                && this.minSize == that.minSize
                && this.size == that.size
                && (!compareSubterms || this.head.equals(that.head, compareSubterms, compareAttachments))
                && (!compareSubterms || this.tail.equals(that.tail, compareSubterms, compareAttachments))
                && (!compareSubterms || this.prefix.equals(that.prefix, compareSubterms, compareAttachments))
                && (!compareAttachments || (this.termAttachments == that.termAttachments))
            // @formatter:on
        }

        final override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitList(this)

        final override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitList(this, arg)

    }

    /** List cons term (a list head with a tail). */
    private inner class ConsTermImpl<E: Term>(
        override val head: E,
        override val tail: ListTerm<E>,
        termAttachments: TermAttachments,
    ) : ListTerm<E>, ListTermImplBase<E>(termAttachments) {

        override val minSize: Int = 1 + tail.minSize
        override val size: Int? = tail.size?.let { 1 + it }
        override val elements: List<E> get() = listOf(head) + tail.elements // TODO: Optimize
        override val termChildren: List<Term> get() = listOf(head) + tail.termChildren // TODO: Optimize
        override val prefix: TermVar? get() = null

        override val hash: Int = Objects.hash(head, tail)
    }

    /** List nil term (an empty list). */
    private inner class NilTermImpl(
        termAttachments: TermAttachments,
    ) : ListTerm<Nothing>, ListTermImplBase<Nothing>(termAttachments) {

        override val minSize: Int get() = 0
        override val size: Int? get() = null
        override val elements: List<Nothing> get() = emptyList()
        override val termChildren: List<Term> get() = emptyList()
        override val prefix: TermVar? get() = null
        override val head: Nothing? get() = null
        override val tail: ListTerm<Nothing>? get() = null

        override val hash: Int = 0
    }

    /** Concat variable with list (a term variable and a tail). */
    private inner class ConcTermImpl<E: Term>(
        override val prefix: TermVar,
        override val tail: ListTerm<E>,
        termAttachments: TermAttachments,
    ) : ListTerm<E>, ListTermImplBase<E>(termAttachments) {

        override val minSize: Int = 1 + tail.minSize
        override val size: Int? = tail.size?.let { 1 + it }
        override val elements: List<E> get() = tail.elements
        override val termChildren: List<Term> get() = listOf(prefix) + tail.termChildren // TODO: Optimize
        override val head: E? get() = null

        override val hash: Int = Objects.hash(prefix, tail)
    }

    /** Term variable. */
    private inner class TermVarImpl(
        override val name: String,
        termAttachments: TermAttachments,
    ) : TermVar, TermImplBase(termAttachments) {

        final override val termKind: TermKind get() = TermKind.VAR

        override val hash: Int = Objects.hash(name)

        override fun equals(that: Term, compareSubterms: Boolean, compareAttachments: Boolean): Boolean {
            if (that !is TermVar) return false
            // @formatter:off
            return (that !is TermVarImpl || this.hash == that.hash)
                && this.name == that.name
                && (!compareAttachments || (this.termAttachments == that.termAttachments))
            // @formatter:on
        }

        override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitVar(this)

        override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitVar(this, arg)

    }

}