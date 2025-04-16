package net.pelsmaeker.katerm

import net.pelsmaeker.katerm.io.DefaultTermWriter
import java.util.*

// TODO: Make this an interface
typealias ApplTermBuilder = (String, List<Term>, TermAttachments, List<String>?) -> ApplTerm

/**
 * The default simple term builder.
 */
open class DefaultSimpleTermBuilder: SimpleTermBuilder {

    override fun <T : Term> withAttachments(term: T, newAttachments: TermAttachments): T {
        if (term.termAttachments == newAttachments) return term
        return when (term) {
            is IntTerm -> newInt(term.termValue, newAttachments, term.termSeparators) as T
            is RealTerm -> newReal(term.termValue, newAttachments, term.termSeparators) as T
            is StringTerm -> newString(term.termValue, newAttachments, term.termSeparators) as T
            is ApplTerm -> newAppl(term.termOp, term.termArgs, newAttachments, term.termSeparators) as T
            is ListTermVar -> newListVar(term.name, newAttachments) as T
            is ListTerm<*> -> newList(term.elements, newAttachments, term.termSeparators) as T
            is TermVar -> newVar(term.name, newAttachments) as T
            else -> throw IllegalArgumentException("Unknown term type: $term")
        }
    }

    override fun <T : Term> withSeparators(term: T, newSeparators: List<String>?): T {
        if (term.termSeparators == newSeparators) return term
        return when (term) {
            is IntTerm -> newInt(term.termValue, term.termAttachments, newSeparators) as T
            is RealTerm -> newReal(term.termValue, term.termAttachments, newSeparators) as T
            is StringTerm -> newString(term.termValue, term.termAttachments, newSeparators) as T
            is ApplTerm -> newAppl(term.termOp, term.termArgs, term.termAttachments, newSeparators) as T
            is ListTermVar -> if (newSeparators != null) throw IllegalArgumentException("Term variables cannot have separators") else term
            is ListTerm<*> -> newList(term.elements, term.termAttachments, newSeparators) as T
            is TermVar -> if (newSeparators != null) throw IllegalArgumentException("Term variables cannot have separators") else term
            else -> throw IllegalArgumentException("Unknown term type: $term")
        }
    }

    override fun newInt(value: Int, attachments: TermAttachments, separators: List<String>?): IntTerm {
        return IntTermImpl(value, null /* TODO */, attachments, separators)
    }

    override fun copyInt(term: IntTerm, newValue: Int): IntTerm {
        if (term.termValue == newValue) return term
        return newInt(newValue /* TODO: termText */, term.termAttachments, term.termSeparators)
    }

    override fun newReal(value: Double, attachments: TermAttachments, separators: List<String>?): RealTerm {
        return RealTermImpl(value, null /* TODO */, attachments, separators)
    }

    override fun copyReal(term: RealTerm, newValue: Double): RealTerm {
        if (term.termValue == newValue) return term
        return newReal(newValue, term.termAttachments, term.termSeparators)
    }

    override fun newString(value: String, attachments: TermAttachments, separators: List<String>?): StringTerm {
        return StringTermImpl(value, null /* TODO */, attachments, separators)
    }

    override fun copyString(term: StringTerm, newValue: String): StringTerm {
        if (term.termValue == newValue) return term
        return newString(newValue, term.termAttachments, term.termSeparators)
    }

    override fun <T> newValue(value: T, attachments: TermAttachments, separators: List<String>?): ValueTerm<T> {
        TODO("Not yet implemented")
    }

    override fun <V> copyValue(term: ValueTerm<V>, newValue: V): ValueTerm<V> {
        TODO("Not yet implemented")
    }

    override fun newAppl(op: String, args: List<Term>, attachments: TermAttachments, separators: List<String>?): ApplTerm {
        require(separators == null || separators.size == args.size + 1) {
            "Expected ${args.size + 1} separators separating ${args.size} arguments; got ${separators!!.size}."
        }

        val customBuilder = getApplBuilder(op)
        return customBuilder(op, args, attachments, separators)
    }

    /**
     * Gets the builder to use to build a term of the specified type.
     *
     * @param op The constructor name.
     * @return The builder to use.
     */
    protected open fun getApplBuilder(op: String): ApplTermBuilder {
        return ::ApplTermImpl
    }

    override fun copyAppl(term: ApplTerm, newArgs: List<Term>): ApplTerm {
        if (term.termArgs == newArgs) return term
        return newAppl(term.termOp, newArgs, term.termAttachments, term.termSeparators)
    }

    override fun <T : Term> newList(
        elements: List<T>,
        attachments: TermAttachments,
        separators: List<String>?
    ): ListTerm<T> {
        return if (elements.isNotEmpty()) {
            // TODO: Enforce that all elements of a list share the same separators and attachments?
            //  This would mean: no term sharing, or copying a tail of a list to another list

            ConsTermImpl(elements.first(), newList(elements.drop(1)), attachments, separators)
        } else {
            NilTermImpl(attachments, separators)
        }
    }

    override fun <E : Term> copyList(term: ListTerm<E>, newElements: List<E>): ListTerm<E> {
        if (term.elements == newElements) return term
        return newList(newElements, term.termAttachments, term.termSeparators)
    }

    override fun copyVar(term: TermVar, newName: String): TermVar {
        TODO("Not yet implemented")
    }

    override fun newVar(name: String, attachments: TermAttachments): TermVar {
        return TermVarImpl(name, attachments)
    }

    override fun newListVar(name: String, attachments: TermAttachments): ListTermVar {
        return ListTermVarImpl(name, attachments)
    }

    // The classes here are protected to prevent them from being instantiated or used outside of this class.
    // Instead, the base interfaces should be used.

    /** Base class for this term implementation. */
    @Suppress("EqualsOrHashCode")
    protected abstract class TermImplBase(
        override val termAttachments: TermAttachments,
        override val termSeparators: List<String>?,
    ): Term {
        companion object {
            /** The default term writer used for [toString]. */
            private val printer = DefaultTermWriter()
        }

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

//        /**
//         * Determines whether this term and its subterms represent the same value
//         * as the given term and it subterms, regardless of the actual implementations
//         * of the terms and its subterms.
//         *
//         * Note that attachments are not checked by this method.
//         *
//         * Implementations should compare equivalent to other implementations of the same term type,
//         * but can take shortcuts when comparing to the same implementation of the term type.
//         */
//        abstract fun equivalentTo(other: Term): Boolean

//        /**
//         * Checks whether this term and the given term could be equal.
//         * Returns `false` if they can never be equal.
//         *
//         * @param that the term to check
//         * @return `true` if this term could be equal to the given term; otherwise, `false` if it can never be equal
//         */
//        @Suppress("NOTHING_TO_INLINE")
//        protected inline fun maybeEqual(that: Term): Boolean {
//            // If they use the same implementation, their hashes must be equal
//            return this::class.java == that::class.java && this.hash != (that as TermImplBase).hash
//        }

        /**
         * Returns a string representation of this term.
         *
         * Override this method to customize the string representation.
         */
        override fun toString(): String {
            return printer.writeToString(this)
        }
    }

    @Suppress("EqualsOrHashCode")
    protected abstract class ValueTermImplBase<T>(
        attachments: TermAttachments,
        separators: List<String>?,
    ) : ValueTerm<T>, TermImplBase(attachments, separators) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true                     // Identity equality
            @Suppress("UNCHECKED_CAST")
            val that = other as? ValueTerm<T> ?: return false   // Must be a ValueTerm
            // @formatter:off
            return this::class.java == that::class.java
                && equalsValue(that)
                && this.termAttachments == that.termAttachments
                && this.termSeparators == that.termSeparators
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
    protected abstract class ApplTermImplBase(
        attachments: TermAttachments,
        termSeparators: List<String>?,
    ) : ApplTerm, TermImplBase(attachments, termSeparators) {

        abstract override val termArgs: List<Term>

        final override fun equals(other: Any?): Boolean {
            if (this === other) return true                     // Identity equality
            val that = other as? ApplTerm ?: return false       // Must be an ApplTerm
            // @formatter:off
            return this::class.java == that::class.java
                // TODO: Compare hash code
                && equalsAppl(that)
                && this.termAttachments == that.termAttachments
                && this.termSeparators == that.termSeparators
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

    /** Constructor application term. */
    private class ApplTermImpl(
        override val termOp: String,
        termArgs: List<Term>,
        attachments: TermAttachments = TermAttachments.empty(),
        separators: List<String>? = null,
    ) : ApplTerm, ApplTermImplBase(attachments, separators) {

        override val termArgs: List<Term> = termArgs.toList() // Safety copy.

        init {
            require(separators == null || separators.size == termArgs.size + 1) {
                "Expected ${termArgs.size + 1} separators separating ${termArgs.size} arguments; got ${separators!!.size}."
            }
        }

        override fun equalsAppl(that: ApplTerm): Boolean {
            // @formatter:off
            return this.termOp == that.termOp
                && this.termArgs == that.termArgs
            // @formatter:on
        }

        // The fields in the hash must match the fields in [equalsAppl]
        override val hash: Int = Objects.hash(termOp, termArgs)
    }

    /** Integer value term base class. */
    @Suppress("EqualsOrHashCode")
    protected abstract class IntTermImplBase(
        attachments: TermAttachments = TermAttachments.empty(),
        separators: List<String>?,
    ) : IntTerm, ValueTermImplBase<Int>(attachments, separators) {

        init {
            require(separators == null || separators.size == 2) { "Expected 2 separators, got ${separators?.size}." }
        }

        abstract override val termText: String

        override fun equals(other: Any?): Boolean {
            if (this === other) return true                     // Identity equality
            val that = other as? IntTerm ?: return false        // Must be an IntTerm
            // @formatter:off
            return this::class.java == that::class.java
                // TODO: Compare hash code
                && equalsInt(that)
                && this.termAttachments == that.termAttachments
                && this.termSeparators == that.termSeparators
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
    private class IntTermImpl(
        override val termValue: Int,
        /** The text representation of the value of the term; or `null` to use the default representation of [termValue]. */
        private val text: String? = null,
        attachments: TermAttachments = TermAttachments.empty(),
        separators: List<String>?,
    ) : IntTerm, IntTermImplBase(attachments, separators) {

        init {
            require(separators == null || separators.size == 2) { "Expected 2 separators, got ${separators?.size}." }
        }

        override val termText: String get() = text ?: termValue.toString()

        override fun equalsInt(that: IntTerm): Boolean {
            return this.termValue == that.termValue
        }

        // The fields in the hash must match the fields in [equalsInt]
        override val hash: Int = Objects.hash(termValue)
    }

    /** Real value term base class. */
    @Suppress("EqualsOrHashCode")
    protected abstract class RealTermImplBase(
        attachments: TermAttachments = TermAttachments.empty(),
        separators: List<String>?,
    ) : RealTerm, ValueTermImplBase<Double>(attachments, separators) {

        init {
            require(separators == null || separators.size == 2) { "Expected 2 separators, got ${separators?.size}." }
        }

        abstract override val termText: String

        override fun equals(other: Any?): Boolean {
            if (this === other) return true                     // Identity equality
            val that = other as? RealTerm ?: return false       // Must be a RealTerm
            // @formatter:off
            return this::class.java == that::class.java
                // TODO: Compare hash code
                && equalsReal(that)
                && this.termAttachments == that.termAttachments
                && this.termSeparators == that.termSeparators
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
    private class RealTermImpl(
        override val termValue: Double,
        /** The text representation of the value of the term; or `null` to use the default representation of [termValue]. */
        private val text: String? = null,
        attachments: TermAttachments = TermAttachments.empty(),
        separators: List<String>?,
    ) : RealTerm, RealTermImplBase(attachments, separators) {

        init {
            require(separators == null || separators.size == 2) { "Expected 2 separators, got ${separators?.size}." }
        }

        override val termText: String get() = text ?: termValue.toString()

        override fun equalsReal(that: RealTerm): Boolean {
            return this.termValue == that.termValue
        }

        override val hash: Int = Objects.hash(termValue)
    }

    /** String value term base class. */
    @Suppress("EqualsOrHashCode")
    protected abstract class StringTermImplBase(
        attachments: TermAttachments = TermAttachments.empty(),
        separators: List<String>?,
    ) : StringTerm, ValueTermImplBase<String>(attachments, separators) {

        init {
            require(separators == null || separators.size == 2) { "Expected 2 separators, got ${separators?.size}." }
        }

        abstract override val termText: String

        override fun equals(other: Any?): Boolean {
            if (this === other) return true                     // Identity equality
            val that = other as? StringTerm ?: return false     // Must be a StringTerm
            // @formatter:off
            return this::class.java == that::class.java
                // TODO: Compare hash code
                && equalsString(that)
                && this.termAttachments == that.termAttachments
                && this.termSeparators == that.termSeparators
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
    private class StringTermImpl(
        override val termValue: String,
        /** The text representation of the value of the term; or `null` to use the default representation of [termValue]. */
        private val text: String? = null,
        attachments: TermAttachments = TermAttachments.empty(),
        separators: List<String>?,
    ) : StringTerm, StringTermImplBase(attachments, separators) {

        init {
            require(separators == null || separators.size == 2) { "Expected 2 separators, got ${separators?.size}." }
        }

        override val termText: String get() = text ?: termValue

        override fun equalsString(that: StringTerm): Boolean {
            return this.termValue == that.termValue
        }

        override val hash: Int = Objects.hash(termValue)
    }

//    /** Blob term. */
//    // TODO: We should probably not support this? Instead, users can create an implementation of `ApplTerm`.
//    @Suppress("EqualsOrHashCode")
//    protected class BlobTermImpl(
//        override val value: Any,
//        attachments: TermAttachments = TermAttachments.empty()
//    ) : BlobTerm, TermImpl(attachments, null) {
//
//        override val termSeparators: List<String>? get() = null
//
//        override fun equals(other: Any?): Boolean {
//            if (this === other) return true                 // Identity equality
//            val that = other as? BlobTerm ?: return false   // Not a BlobTerm
//
//            // Check that the term and all its subterms are truly equal
//            // @formatter:off
//            return maybeEqual(other)
//                && this.value == that.value
//                && this.termAttachments == that.termAttachments
//            // @formatter:on
//        }
//
//        override val hash: Int = Objects.hash(value, termAttachments)
//    }

    /** Term variable. */
    @Suppress("EqualsOrHashCode")
    private class TermVarImpl(
        override val name: String,
        attachments: TermAttachments = TermAttachments.empty(),
    ) : TermVar, TermImplBase(attachments, null) {

        override val termSeparators: List<String>? get() = null

        override fun equals(other: Any?): Boolean {
            if (this === other) return true                 // Identity equality
            val that = other as? TermVar ?: return false    // Not a TermVar

            // Check that the term and all its subterms are truly equal
            // @formatter:off
            return this::class.java == that::class.java
                // TODO: Compare hash code
                && this.name == that.name
                && this.termAttachments == that.termAttachments
                && this.termSeparators == that.termSeparators
            // @formatter:on
        }

        override val hash: Int = Objects.hash(name)
    }

    /** Base class for list terms. */
    @Suppress("EqualsOrHashCode")
    protected sealed class ListTermImplBase<T: Term>(
        attachments: TermAttachments,
        separators: List<String>?,
    ): ListTerm<T>, TermImplBase(attachments, separators) {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true                     // Identity equality
            val that = other as? ListTerm<*> ?: return false    // Must be a ListTerm
            // @formatter:off
            return this::class.java == that::class.java
                    // TODO: Compare hash code
                    && equalsList(that)
                    && this.minSize == that.minSize
                    && this.size == that.size
                    && this.elements == that.elements
                    && this.trailingVar == that.trailingVar
                    && this.termAttachments == that.termAttachments
                    && this.termSeparators == that.termSeparators
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
    private class ConsTermImpl<T: Term>(
        override val head: T,
        override val tail: ListTerm<T>,
        attachments: TermAttachments = TermAttachments.empty(),
        separators: List<String>?,
    ) : ListTerm<T>, ListTermImplBase<T>(attachments, separators) {

        init {
            require(separators == null || separators.size == 3) { "Expected 3 separators; got ${separators!!.size}." }
        }

        override val minSize: Int = 1 + tail.minSize
        override val size: Int? = tail.size?.let { 1 + it }
        override val elements: List<T> get() = listOf(head) + tail.elements // TODO: Optimize
        override val trailingVar: ListTermVar? get() = tail.trailingVar

        override fun equalsList(that: ListTerm<*>): Boolean {
            // @formatter:off
            return this.head == that.head
                && this.tail == that.tail
            // @formatter:on
        }

        override val hash: Int = Objects.hash(head, tail)

    }

    /** List nil term (an empty list). */
    private class NilTermImpl(
        attachments: TermAttachments = TermAttachments.empty(),
        separators: List<String>?,
    ) : ListTerm<Nothing>, ListTermImplBase<Nothing>(attachments, separators) {

        init {
            require(separators == null || separators.size == 2) { "Expected 2 separators; got ${separators!!.size}." }
        }

        override val minSize: Int get() = 0
        override val size: Int? get() = null
        override val elements: List<Nothing> get() = emptyList()
        override val trailingVar: ListTermVar? get() = null
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

    /** A term variable as a list tail. */
    private class ListTermVarImpl(
        override val name: String,
        attachments: TermAttachments = TermAttachments.empty(),
    ) : ListTermVar, ListTermImplBase<Nothing>(attachments, null) {

        override val termSeparators: List<String>? get() = null

        override val head: Nothing? get() = null
        override val tail: ListTerm<Nothing>? get() = null

        override fun equalsList(that: ListTerm<*>): Boolean {
            // @formatter:off
            return that is ListTermVar
                && this.name == that.name
            // @formatter:on
        }

        override val hash: Int = Objects.hash(name)
    }

}