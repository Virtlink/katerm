package net.pelsmaeker.katerm

import net.pelsmaeker.katerm.io.DefaultTermWriter
import java.util.*

typealias ApplTermBuilder = (ApplTermType, List<Term>, TermAttachments) -> ApplTerm

/**
 * The default term builder.
 */
open class DefaultTermBuilder: TermBuilder {

    override fun withAttachments(term: Term, newAttachments: TermAttachments): Term {
        if (term.termAttachments == newAttachments) return term
        return when (term) {
            is IntTerm -> createInt(term.value, newAttachments)
            is StringTerm -> createString(term.value, newAttachments)
            is BlobTerm -> createBlob(term.value, newAttachments)
            is ApplTerm -> createAppl(term.termType, term.termArgs, newAttachments)
            is ListTermVar -> createListVar(term.termType, term.name, term.resource, newAttachments)
            is TermVar -> createVar(term.termType, term.name, term.resource, newAttachments)
            is ListTerm -> createList(term.termType, term.elements, newAttachments)
            else -> throw IllegalArgumentException("Unknown term type: $term")
        }
    }

    override fun createInt(value: Int, attachments: TermAttachments): IntTerm {
        return IntTermImpl(value, attachments)
    }

    override fun replaceInt(term: IntTerm, newValue: Int): IntTerm {
        if (term.value == newValue) return term
        return createInt(newValue, term.termAttachments)
    }

    override fun createString(value: String, attachments: TermAttachments): StringTerm {
        return StringTermImpl(value, attachments)
    }

    override fun replaceString(term: StringTerm, newValue: String): StringTerm {
        if (term.value == newValue) return term
        return createString(newValue, term.termAttachments)
    }

    override fun createBlob(value: Any, attachments: TermAttachments): BlobTerm {
        return BlobTermImpl(value, attachments)
    }

    override fun replaceBlob(term: BlobTerm, newValue: Any): BlobTerm {
        if (term.value === newValue) return term
        return createBlob(newValue, term.termAttachments)
    }

    override fun createAppl(type: ApplTermType, args: List<Term>, attachments: TermAttachments): ApplTerm {
        require((args zip type.paramTypes).all { (te, ty) -> te.isAssignableTo(ty) }) { "Arguments do not match parameter types." }

        val customBuilder = getApplBuilder(type)
        return customBuilder(type, args, attachments)
    }

    /**
     * Gets the builder to use to build a term of the specified type.
     *
     * @param type the type of the term to build
     * @return the builder to use
     */
    protected open fun getApplBuilder(type: ApplTermType): ApplTermBuilder {
        return ::ApplTermImpl
    }

    override fun replaceAppl(term: ApplTerm, newArgs: List<Term>): ApplTerm {
        if (term.termArgs == newArgs) return term
        return createAppl(term.termType, newArgs, term.termAttachments)
    }

    override fun createList(type: ListTermType, elements: List<Term>, attachments: TermAttachments): ListTerm {
        require(elements.all { it.isAssignableTo(type.elementType) }) { "Elements do not match list type." }

        return if (elements.isNotEmpty()) {
            ConsTermImpl(type, elements.first(), createList(type, elements.drop(1)))  // TODO: Optimize
        } else {
            NilTermImpl()
        }
    }

    override fun replaceList(term: ListTerm, newElements: List<Term>): ListTerm {
        if (term.elements == newElements) return term
        return createList(term.termType, newElements, term.termAttachments)
    }

    override fun createVar(type: TermType, name: String, resource: String?, attachments: TermAttachments): TermVar {
        return TermVarImpl(type, name, resource, attachments)
    }

    override fun createListVar(
        type: ListTermType,
        name: String,
        resource: String?,
        attachments: TermAttachments,
    ): ListTermVar {
        return ListTermVarImpl(type, name, resource, attachments)
    }

    // The classes here are private to prevent them from being instantiated or used outside of this class.
    // Instead, the base interfaces should be used.

    /** Base class for this term implementation. */
    protected abstract class TermImpl(
        override val termAttachments: TermAttachments
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

        /**
         * Checks whether this term and the given term could be equal.
         * Returns `false` if they can never be equal.
         *
         * @param that the term to check
         * @return `true` if this term could be equal to the given term; otherwise, `false` if it can never be equal
         */
        @Suppress("NOTHING_TO_INLINE")
        protected inline fun maybeEqual(that: Term): Boolean {
            // If they use the same implementation, their hashes must be equal
            return this::class.java == that::class.java && this.hash != (that as TermImpl).hash
        }

        /**
         * Returns a string representation of this term.
         *
         * Override this method to customize the string representation.
         */
        override fun toString(): String {
            return printer.writeToString(this)
        }
    }


    /** Constructor application term base class. */
    protected abstract class ApplTermImplBase(
        attachments: TermAttachments,
    ) : ApplTerm, TermImpl(attachments) {

        abstract override val termArgs: List<Term>

        final override fun equals(other: Any?): Boolean {
            if (this === other) return true                // Identity equality
            val that = other as? ApplTerm ?: return false  // Not an ApplTerm
            return maybeEqual(other) && equals(that)
        }

        /**
         * Checks whether this term and the given term are equal, including comparing the attachments.
         *
         * Implement this method to customize the equality check.
         *
         * Note that even if the attachments are not equal, this method must still return `true`
         * when the terms represent the same value and have the same subterms and attachments.
         *
         * @param that the term to check
         * @return `true` is this term is equal to the specified term; otherwise, `false`
         */
        protected abstract fun equals(that: ApplTerm): Boolean

        /**
         * Implement this property to perform a custom hash code calculation.
         * Do include the attachments, and the type if it can differ between instances.
         */
        abstract override val hash: Int
    }

    /** Constructor application term. */
    protected class ApplTermImpl(
        override val termType: ApplTermType,
        override val termArgs: List<Term>,
        attachments: TermAttachments = TermAttachments.empty()
    ) : ApplTerm, ApplTermImplBase(attachments) {

        init {
            require((termArgs zip termType.paramTypes).all { (te, ty) -> te.isAssignableTo(ty) }) { "Arguments do not match parameter types." }
        }

        override fun equals(that: ApplTerm): Boolean {
            // @formatter:off
            return this.termType == that.termType
                && this.termArgs == that.termArgs
                && this.termAttachments == that.termAttachments
            // @formatter:on
        }

        // Note that the attachments are part of the hash
        override val hash: Int = Objects.hash(termType, termArgs, attachments)
    }

    /** Integer term. */
    protected class IntTermImpl(
        override val value: Int,
        attachments: TermAttachments = TermAttachments.empty()
    ) : IntTerm, TermImpl(attachments) {
        override val termType: IntTermType get() = IntTermType

        override fun equals(other: Any?): Boolean {
            if (this === other) return true                 // Identity equality
            val that = other as? IntTerm ?: return false    // Not an IntTerm

            // Check that the term and all its subterms are truly equal
            // @formatter:off
            return maybeEqual(other)
                && this.value == that.value
                && this.termAttachments == that.termAttachments
            // @formatter:on
        }

        override val hash: Int = Objects.hash(value, attachments)
    }

    /** String term. */
    protected class StringTermImpl(
        override val value: String,
        attachments: TermAttachments = TermAttachments.empty()
    ) : StringTerm, TermImpl(attachments) {
        override val termType: StringTermType get() = StringTermType

        override fun equals(other: Any?): Boolean {
            if (this === other) return true                 // Identity equality
            val that = other as? StringTerm ?: return false // Not a StringTerm

            // Check that the term and all its subterms are truly equal
            // @formatter:off
            return maybeEqual(other)
                && this.value == that.value
                && this.termAttachments == that.termAttachments
            // @formatter:on
        }

        override val hash: Int = Objects.hash(value, attachments)
    }

    /** Blob term. */
    // TODO: We should probably not support this? Instead, users can create an implementation of `ApplTerm`.
    protected class BlobTermImpl(
        override val value: Any,
        attachments: TermAttachments = TermAttachments.empty()
    ) : BlobTerm, TermImpl(attachments) {
        override val termType: BlobTermType get() = BlobTermType

        override fun equals(other: Any?): Boolean {
            if (this === other) return true                 // Identity equality
            val that = other as? BlobTerm ?: return false   // Not a BlobTerm

            // Check that the term and all its subterms are truly equal
            // @formatter:off
            return maybeEqual(other)
                && this.value == that.value
                && this.termAttachments == that.termAttachments
            // @formatter:on
        }

        override val hash: Int = Objects.hash(value, attachments)
    }

    /** A term variable. */
    protected class TermVarImpl(
        override val termType: TermType,
        override val name: String,
        override val resource: String?,
        attachments: TermAttachments = TermAttachments.empty(),
    ) : TermVar, TermImpl(attachments) {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true                 // Identity equality
            val that = other as? TermVar ?: return false    // Not a TermVar

            // Check that the term and all its subterms are truly equal
            // @formatter:off
            return maybeEqual(other)
                && this.termType == that.termType
                && this.name == that.name
                && this.resource == that.resource
                && this.termAttachments == that.termAttachments
            // @formatter:on
        }

        override val hash: Int = Objects.hash(termType, name, resource, attachments)
    }

    /** Base class for list terms. */
    protected sealed class ListTermImpl(
        attachments: TermAttachments,
    ): ListTerm, TermImpl(attachments) {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true                 // Identity equality
            val that = other as? ListTerm ?: return false   // Not a ListTerm

            // Check that the term and all its subterms are truly equal
            // @formatter:off
            return maybeEqual(other)
                && this.minSize == that.minSize
                && this.size == that.size
                && this.elements == that.elements
                && this.trailingVar == that.trailingVar
                && this.termAttachments == that.termAttachments
            // @formatter:on
        }

        override val hash: Int = Objects.hash(termType, elements, trailingVar, attachments)
    }

    /** List cons term (a list head with a tail). */
    protected class ConsTermImpl(
        override val termType: ListTermType,
        override val head: Term,
        override val tail: ListTerm,
        attachments: TermAttachments = TermAttachments.empty(),
    ) : ConsTerm, ListTermImpl(attachments) {

        override val minSize: Int = 1 + tail.minSize
        override val size: Int? = tail.size?.let { 1 + it }
        override val trailingVar: ListTermVar? get() = tail.trailingVar

        init {
            require(head.isAssignableTo(tail.termType.elementType)) { "Head does not match list type." }
        }

    }

    /** List nil term (an empty list). */
    protected class NilTermImpl(
        attachments: TermAttachments = TermAttachments.empty(),
    ) : NilTerm, ListTermImpl(attachments) {
        override val termType: ListTermType get() = ListTermType(NoTermType)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true                 // Identity equality
            val that = other as? ListTerm ?: return false   // Not a ListTerm

            // Check that the term and all its subterms are truly equal
            // @formatter:off
            return maybeEqual(other)
                && that.size == 0
                && this.termAttachments == that.termAttachments
            // @formatter:on
        }

        override val hash: Int = Objects.hash(attachments)
    }

    /** A term variable as a list tail. */
    protected class ListTermVarImpl(
        override val termType: ListTermType,
        override val name: String,
        override val resource: String?,
        attachments: TermAttachments = TermAttachments.empty(),
    ) : ListTermVar, ListTermImpl(attachments) {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true                 // Identity equality
            val that = other as? ListTerm ?: return false   // Not a ListTerm

            // Check that the term and all its subterms are truly equal
            // @formatter:off
            return maybeEqual(other)
                    && this.minSize == that.minSize
                    && this.size == that.size
                    && this.elements == that.elements
                    && this.trailingVar == that.trailingVar
                    && this.termAttachments == that.termAttachments
            // @formatter:on
        }

        override val hash: Int = Objects.hash(termType, name, resource, attachments)
    }

}