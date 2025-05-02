package net.pelsmaeker.katerm

import net.pelsmaeker.katerm.attachments.TermAttachments
import net.pelsmaeker.katerm.io.DefaultTermWriter
import net.pelsmaeker.katerm.io.TermTextWriter
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
            is IntTerm -> newInt(term.value, newAttachments) as T
            is RealTerm -> newReal(term.value, newAttachments) as T
            is StringTerm -> newString(term.value, newAttachments) as T
            is ApplTerm -> newAppl(term.termOp, term.termArgs, newAttachments) as T
            is OptionTerm<*> -> newOption(term.element, newAttachments) as T
            is ListTerm<*> -> newList(term.elements, newAttachments) as T
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

    override fun <E: Term> copyOption(term: OptionTerm<E>, newElement: E?): OptionTerm<E> {
        if (term.element == newElement) return term
        return newOption(newElement, term.termAttachments)
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

    override fun <E : Term> copyList(term: ListTerm<E>, newElements: List<E>): ListTerm<E> {
        if (term.elements == newElements) return term
        return newList(newElements, term.termAttachments)
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

    // The classes here are protected to prevent them from being instantiated or used outside of this class.
    // Instead, the base interfaces should be used.

    /** Base class for this term implementation. */
    @Suppress("EqualsOrHashCode")
    protected abstract inner class TermImplBase(
        override val termAttachments: TermAttachments,
    ): Term {

        /** An eager hash code calculation. */
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
         */
        abstract override fun equals(other: Any?): Boolean

        /**
         * Returns a string representation of this term.
         *
         * Override this method to customize the string representation.
         */
        override fun toString(): String {
            return this@TermBuilderBase.termPrinter.writeToString(this)
        }
    }

    @Suppress("EqualsOrHashCode")
    protected abstract inner class ValueTermImplBase<T>(
        attachments: TermAttachments,
    ) : ValueTerm<T>, TermImplBase(attachments) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true                     // Identity equality
            @Suppress("UNCHECKED_CAST")
            val that = other as? ValueTerm<T> ?: return false   // Must be a ValueTerm
            // @formatter:off
            return this::class.java == that::class.java
                && equalsValue(that)
                && this.termAttachments == that.termAttachments
            // @formatter:on
        }

        /**
         * Checks whether this term and the given term are equal, including comparing the attachments.
         *
         * Implement this method to customize the equality check.
         *
         * Note that even if the attachments are not equal, this method must still return `true`
         * if the value of the terms are equal.
         */
        protected abstract fun equalsValue(that: ValueTerm<T>): Boolean
    }

    /**
     * Constructor application term base class.
     *
     * Implementations should check that the number of separators, if not `null`, matches the number of arguments + 1.
     */
    @Suppress("EqualsOrHashCode")
    protected abstract inner class ApplTermBase(
        attachments: TermAttachments,
    ) : ApplTerm, TermImplBase(attachments) {

        abstract override val termArgs: List<Term>

        final override fun equals(other: Any?): Boolean {
            if (this === other) return true                     // Identity equality
            val that = other as? ApplTerm ?: return false       // Must be an ApplTerm
            // @formatter:off
            return this::class.java == that::class.java
                // TODO: Compare hash code
                && equalsAppl(that)
                && this.termAttachments == that.termAttachments
            // @formatter:on
        }

        /**
         * Checks whether this term and the given term are equal.
         *
         * Implement this method to customize the equality check.
         *
         * @param that the term to check
         * @return `true` is this term is equal to the specified term; otherwise, `false`
         */
        protected abstract fun equalsAppl(that: ApplTerm): Boolean

        /**
         * Implement this property to perform a custom hash code calculation.
         * Do include the attachments and separators.
         */
        abstract override val hash: Int
    }

    /** Integer value term base class. */
    @Suppress("EqualsOrHashCode")
    protected abstract inner class IntTermImplBase(
        attachments: TermAttachments = TermAttachments.empty(),
    ) : IntTerm, ValueTermImplBase<Int>(attachments) {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true                     // Identity equality
            val that = other as? IntTerm ?: return false        // Must be an IntTerm
            // @formatter:off
            return this::class.java == that::class.java
                // TODO: Compare hash code
                && equalsInt(that)
                && this.termAttachments == that.termAttachments
            // FIXME: Should we compare termText?
            // @formatter:on
        }

        final override fun equalsValue(that: ValueTerm<Int>): Boolean {
            return that is IntTerm && equalsInt(that)
        }

        /**
         * Checks whether this term and the given term are equal.
         *
         * Implement this method to customize the equality check.
         *
         * @param that The term to check.
         * @return `true` if this term is equal to the specified term; otherwise, `false`.
         */
        protected abstract fun equalsInt(that: IntTerm): Boolean

        /**
         * Implement this property to perform a custom hash code calculation.
         * Do include the attachments and separators.
         */
        abstract override val hash: Int
    }

    /** Integer value term. */
    private inner class IntTermImpl(
        override val value: Int,
        attachments: TermAttachments = TermAttachments.empty(),
    ) : IntTerm, IntTermImplBase(attachments) {

        override fun equalsInt(that: IntTerm): Boolean {
            return this.value == that.value
        }

        // The fields in the hash must match the fields in [equalsInt]
        override val hash: Int = Objects.hash(value)
    }

    /** Real value term base class. */
    @Suppress("EqualsOrHashCode")
    protected abstract inner class RealTermImplBase(
        attachments: TermAttachments = TermAttachments.empty(),
    ) : RealTerm, ValueTermImplBase<Double>(attachments) {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true                     // Identity equality
            val that = other as? RealTerm ?: return false       // Must be a RealTerm
            // @formatter:off
            return this::class.java == that::class.java
                // TODO: Compare hash code
                && equalsReal(that)
                && this.termAttachments == that.termAttachments
            // FIXME: Should we compare termText?
            // @formatter:on
        }

        final override fun equalsValue(that: ValueTerm<Double>): Boolean {
            return that is RealTerm && equalsReal(that)
        }

        /**
         * Checks whether this term and the given term are equal.
         *
         * Implement this method to customize the equality check.
         *
         * @param that The term to check.
         * @return `true` if this term is equal to the specified term; otherwise, `false`.
         */
        protected abstract fun equalsReal(that: RealTerm): Boolean

        /**
         * Implement this property to perform a custom hash code calculation.
         * Do include the attachments and separators.
         */
        abstract override val hash: Int
    }

    /** Real value term. */
    private inner class RealTermImpl(
        override val value: Double,
        attachments: TermAttachments = TermAttachments.empty(),
    ) : RealTerm, RealTermImplBase(attachments) {

        override fun equalsReal(that: RealTerm): Boolean {
            return this.value == that.value
        }

        override val hash: Int = Objects.hash(value)
    }

    /** String value term base class. */
    @Suppress("EqualsOrHashCode")
    protected abstract inner class StringTermImplBase(
        attachments: TermAttachments = TermAttachments.empty(),
    ) : StringTerm, ValueTermImplBase<String>(attachments) {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true                     // Identity equality
            val that = other as? StringTerm ?: return false     // Must be a StringTerm
            // @formatter:off
            return this::class.java == that::class.java
                // TODO: Compare hash code
                && equalsString(that)
                && this.termAttachments == that.termAttachments
            // FIXME: Should we compare termText?
            // @formatter:on
        }

        final override fun equalsValue(that: ValueTerm<String>): Boolean {
            return that is StringTerm && equalsString(that)
        }

        /**
         * Checks whether this term and the given term are equal.
         *
         * Implement this method to customize the equality check.
         *
         * @param that The term to check.
         * @return `true` if this term is equal to the specified term; otherwise, `false`.
         */
        protected abstract fun equalsString(that: StringTerm): Boolean

        /**
         * Implement this property to perform a custom hash code calculation.
         * Do include the attachments and separators.
         */
        abstract override val hash: Int
    }

    /** String term. */
    private inner class StringTermImpl(
        override val value: String,
        attachments: TermAttachments = TermAttachments.empty(),
    ) : StringTerm, StringTermImplBase(attachments) {

        override fun equalsString(that: StringTerm): Boolean {
            return this.value == that.value
        }

        override val hash: Int = Objects.hash(value)
    }

    /** Term variable. */
    @Suppress("EqualsOrHashCode")
    private inner class TermVarImpl(
        override val name: String,
        attachments: TermAttachments = TermAttachments.empty(),
    ) : TermVar, TermImplBase(attachments) {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true                 // Identity equality
            val that = other as? TermVar ?: return false    // Not a TermVar

            // Check that the term and all its subterms are truly equal
            // @formatter:off
            return this::class.java == that::class.java
                // TODO: Compare hash code
                && this.name == that.name
                && this.termAttachments == that.termAttachments
            // @formatter:on
        }

        override val hash: Int = Objects.hash(name)
    }

    /** Base class for option terms. */
    @Suppress("EqualsOrHashCode")
    protected abstract inner class OptionTermImplBase<T: Term>(
        attachments: TermAttachments,
    ): OptionTerm<T>, TermImplBase(attachments) {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true                     // Identity equality
            val that = other as? OptionTerm<*> ?: return false    // Must be a ListTerm
            // @formatter:off
            return this::class.java == that::class.java
                    // TODO: Compare hash code
                    && equalsOption(that)
                    && this.termAttachments == that.termAttachments
            // FIXME: Should we compare termText?
            // @formatter:on
        }

        /**
         * Checks whether this term and the given term are equal.
         *
         * Implement this method to customize the equality check.
         *
         * @param that The term to check.
         * @return `true` if this term is equal to the specified term; otherwise, `false`.
         */
        protected abstract fun equalsOption(that: OptionTerm<*>): Boolean

        /**
         * Implement this property to perform a custom hash code calculation.
         * Do include the attachments and separators.
         */
        abstract override val hash: Int
    }

    /** Option term with a value. */
    private inner class SomeTermImpl<E: Term>(
        override val element: E,
        attachments: TermAttachments = TermAttachments.empty(),
    ) : OptionTerm<E>, OptionTermImplBase<E>(attachments) {

        override val variable: TermVar? get() = null
        override val termChildren: List<Term> get() = listOf(element)

        override fun equalsOption(that: OptionTerm<*>): Boolean {
            // @formatter:off
            return this.element == that.element
            // @formatter:on
        }

        override val hash: Int = Objects.hash(element)
    }

    /** Option term without a value */
    private inner class NoneTermImpl(
        attachments: TermAttachments = TermAttachments.empty(),
    ) : OptionTerm<Nothing>, OptionTermImplBase<Nothing>(attachments) {

        override val element: Nothing? get() = null
        override val variable: TermVar? get() = null
        override val termChildren: List<Term> get() = emptyList()

        override fun equalsOption(that: OptionTerm<*>): Boolean {
            // @formatter:off
            return that.isEmpty()
            // @formatter:on
        }

        override val hash: Int = 0
    }

    /** Option term with a variable. */
    private inner class OptTermImpl<E: Term>(
        override val variable: TermVar,
        attachments: TermAttachments = TermAttachments.empty(),
    ) : OptionTerm<E>, OptionTermImplBase<E>(attachments) {

        override val element: E? get() = null
        override val termChildren: List<Term> get() = listOf(variable)

        override fun equalsOption(that: OptionTerm<*>): Boolean {
            // @formatter:off
            return this.variable == that.variable
            // @formatter:on
        }

        override val hash: Int = Objects.hash(variable)
    }

    /** Base class for list terms. */
    @Suppress("EqualsOrHashCode")
    protected abstract inner class ListTermImplBase<T: Term>(
        attachments: TermAttachments,
    ): ListTerm<T>, TermImplBase(attachments) {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true                     // Identity equality
            val that = other as? ListTerm<*> ?: return false    // Must be a ListTerm
            // @formatter:off
            return this::class.java == that::class.java
                    // TODO: Compare hash code
                    && equalsList(that)
                    && this.minSize == that.minSize
                    && this.size == that.size
                    && this.termChildren == that.termChildren
                    && this.termAttachments == that.termAttachments
            // FIXME: Should we compare termText?
            // @formatter:on
        }

        /**
         * Checks whether this term and the given term are equal.
         *
         * Implement this method to customize the equality check.
         *
         * @param that The term to check.
         * @return `true` if this term is equal to the specified term; otherwise, `false`.
         */
        protected abstract fun equalsList(that: ListTerm<*>): Boolean

        /**
         * Implement this property to perform a custom hash code calculation.
         * Do include the attachments and separators.
         */
        abstract override val hash: Int
    }

    /** List cons term (a list head with a tail). */
    private inner class ConsTermImpl<E: Term>(
        override val head: E,
        override val tail: ListTerm<E>,
        attachments: TermAttachments = TermAttachments.empty(),
    ) : ListTerm<E>, ListTermImplBase<E>(attachments) {

        override val minSize: Int = 1 + tail.minSize
        override val size: Int? = tail.size?.let { 1 + it }
        override val elements: List<E> get() = listOf(head) + tail.elements // TODO: Optimize
        override val termChildren: List<Term> get() = listOf(head) + tail.termChildren // TODO: Optimize
        override val prefix: TermVar? get() = null

        override fun equalsList(that: ListTerm<*>): Boolean {
            // @formatter:off
            return this.head == that.head
                && this.tail == that.tail
            // @formatter:on
        }

        override val hash: Int = Objects.hash(head, tail)
    }

    /** List nil term (an empty list). */
    private inner class NilTermImpl(
        attachments: TermAttachments = TermAttachments.empty(),
    ) : ListTerm<Nothing>, ListTermImplBase<Nothing>(attachments) {

        override val minSize: Int get() = 0
        override val size: Int? get() = null
        override val elements: List<Nothing> get() = emptyList()
        override val termChildren: List<Term> get() = emptyList()
        override val prefix: TermVar? get() = null
        override val head: Nothing? get() = null
        override val tail: ListTerm<Nothing>? get() = null

        override fun equalsList(that: ListTerm<*>): Boolean {
            // @formatter:off
            return this.head == that.head
                && this.tail == that.tail
            // @formatter:on
        }

        override val hash: Int = 0
    }

    /** Concat variable with list (a term variable and a tail). */
    private inner class ConcTermImpl<E: Term>(
        override val prefix: TermVar,
        override val tail: ListTerm<E>,
        attachments: TermAttachments = TermAttachments.empty(),
    ) : ListTerm<E>, ListTermImplBase<E>(attachments) {

        override val minSize: Int = 1 + tail.minSize
        override val size: Int? = tail.size?.let { 1 + it }
        override val elements: List<E> get() = tail.elements
        override val termChildren: List<Term> get() = listOf(prefix) + tail.termChildren // TODO: Optimize
        override val head: E? get() = null

        override fun equalsList(that: ListTerm<*>): Boolean {
            // @formatter:off
            return this.head == that.head
                    && this.tail == that.tail
            // @formatter:on
        }

        override val hash: Int = Objects.hash(prefix, tail)
    }

}