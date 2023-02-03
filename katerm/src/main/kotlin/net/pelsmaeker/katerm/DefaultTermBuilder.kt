package net.pelsmaeker.katerm

import net.pelsmaeker.katerm.io.DefaultTermWriter
import java.util.*

class DefaultTermBuilder(
    private val customBuilders: Map<String, (ApplTermType, List<Term>, TermAttachments) -> ApplTerm> = emptyMap(),
): TermBuilder {

    override fun withAttachments(term: Term, newAttachments: TermAttachments): Term {
        if (term.attachments == newAttachments) return term
        return when (term) {
            is IntTerm -> createInt(term.value, newAttachments)
            is StringTerm -> createString(term.value, newAttachments)
            is BlobTerm -> createBlob(term.value, newAttachments)
            is ApplTerm -> createAppl(term.type, term.args, newAttachments)
            is ListTermVar -> createListVar(term.type, term.name, term.resource, newAttachments)
            is TermVar -> createVar(term.type, term.name, term.resource, newAttachments)
            is ListTerm -> createList(term.type, term.elements, newAttachments)
            else -> throw IllegalArgumentException("Unknown term type: $term")
        }
    }

    override fun createInt(value: Int, attachments: TermAttachments): IntTerm {
        return IntTermImpl(value, attachments)
    }

    override fun replaceInt(term: IntTerm, newValue: Int): IntTerm {
        if (term.value == newValue) return term
        return createInt(newValue, term.attachments)
    }

    override fun createString(value: String, attachments: TermAttachments): StringTerm {
        return StringTermImpl(value, attachments)
    }

    override fun replaceString(term: StringTerm, newValue: String): StringTerm {
        if (term.value == newValue) return term
        return createString(newValue, term.attachments)
    }

    override fun createBlob(value: Any, attachments: TermAttachments): BlobTerm {
        return BlobTermImpl(value, attachments)
    }

    override fun replaceBlob(term: BlobTerm, newValue: Any): BlobTerm {
        if (term.value === newValue) return term
        return createBlob(newValue, term.attachments)
    }

    override fun createAppl(type: ApplTermType, args: List<Term>, attachments: TermAttachments): ApplTerm {
        require((args zip type.paramTypes).all { (te, ty) -> te.isAssignableTo(ty) }) { "Arguments do not match parameter types." }

        val customBuilder = customBuilders[type.hashString] ?: ::ApplTermImpl
        return customBuilder(type, args, attachments)
    }

    override fun replaceAppl(term: ApplTerm, newArgs: List<Term>): ApplTerm {
        if (term.args == newArgs) return term
        return createAppl(term.type, newArgs, term.attachments)
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
        return createList(term.type, newElements, term.attachments)
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

    private abstract class TermImpl: Term {
        companion object {
            private val printer = DefaultTermWriter()
        }

        protected abstract val hash: Int

        override fun hashCode(): Int = hash

        override fun toString(): String {
            return printer.writeToString(this)
        }
    }

    private class ApplTermImpl(
        override val type: ApplTermType,
        override val args: List<Term>,
        override val attachments: TermAttachments = TermAttachments.empty()
    ) : ApplTerm, TermImpl() {

        init {
            require((args zip type.paramTypes).all { (te, ty) -> te.isAssignableTo(ty) }) { "Arguments do not match parameter types." }
        }

        override fun equals(that: Any?): Boolean {
            if (this === that) return true              // Identity equality
            if (that !is ApplTermImpl) return false     // Must be the exact same type (otherwise hash calculations may be different)
            if (this.hash != that.hash) return false    // Hashes must match (but this does not guarantee that the terms are equal)

            // Check that the term and all its subterms are truly equal
            // @formatter:off
            return this.type == that.type
                && this.args == that.args
                && this.attachments == that.attachments
            // @formatter:on
        }

        override val hash: Int = Objects.hash(type, args, attachments)
    }

    private abstract class ValueTermImpl<T>: ValueTerm<T>, TermImpl()

    private class IntTermImpl(
        override val value: Int,
        override val attachments: TermAttachments = TermAttachments.empty()
    ) : IntTerm, ValueTermImpl<Int>() {
        override val type: IntTermType get() = IntTermType

        override fun equals(that: Any?): Boolean {
            if (this === that) return true              // Identity equality
            if (that !is IntTermImpl) return false      // Must be the exact same type (otherwise hash calculations may be different)
            if (this.hash != that.hash) return false    // Hashes must match (but this does not guarantee that the terms are equal)

            // Check that the term and all its subterms are truly equal
            // @formatter:off
            return this.value == that.value
                && this.attachments == that.attachments
            // @formatter:on
        }

        override val hash: Int = Objects.hash(value, attachments)
    }

    private class StringTermImpl(
        override val value: String,
        override val attachments: TermAttachments = TermAttachments.empty()
    ) : StringTerm, ValueTermImpl<String>() {
        override val type: StringTermType get() = StringTermType

        override fun equals(that: Any?): Boolean {
            if (this === that) return true              // Identity equality
            if (that !is StringTermImpl) return false   // Must be the exact same type (otherwise hash calculations may be different)
            if (this.hash != that.hash) return false    // Hashes must match (but this does not guarantee that the terms are equal)

            // Check that the term and all its subterms are truly equal
            // @formatter:off
            return this.value == that.value
                && this.attachments == that.attachments
            // @formatter:on
        }

        override val hash: Int = Objects.hash(value, attachments)
    }

    private class BlobTermImpl(
        override val value: Any,
        override val attachments: TermAttachments = TermAttachments.empty()
    ) : BlobTerm, ValueTermImpl<Any>() {
        override val type: BlobTermType get() = BlobTermType

        override fun equals(that: Any?): Boolean {
            if (this === that) return true              // Identity equality
            if (that !is BlobTermImpl) return false     // Must be the exact same type (otherwise hash calculations may be different)
            if (this.hash != that.hash) return false    // Hashes must match (but this does not guarantee that the terms are equal)

            // Check that the term and all its subterms are truly equal
            // @formatter:off
            return this.value == that.value
                && this.attachments == that.attachments
            // @formatter:on
        }

        override val hash: Int = Objects.hash(value, attachments)
    }

    private abstract class ListTermImpl: ListTerm, TermImpl() {

        override fun equals(that: Any?): Boolean {
            if (this === that) return true              // Identity equality
            if (that !is ListTermImpl) return false     // Must be the exact same type (otherwise hash calculations may be different)
            if (this.hash != that.hash) return false    // Hashes must match (but this does not guarantee that the terms are equal)

            // Check that the term and all its subterms are truly equal
            // @formatter:off
            return this.elements == that.elements
                && this.attachments == that.attachments
            // @formatter:on
        }

        override val hash: Int = Objects.hash(elements, attachments)
    }

    private class ConsTermImpl(
        override val type: ListTermType,
        override val head: Term,
        override val tail: ListTerm,
        override val attachments: TermAttachments = TermAttachments.empty(),
    ) : ConsTerm, ListTermImpl() {
        init {
            require(head.isAssignableTo(tail.type.elementType)) { "Head does not match list type." }
        }
    }

    private class NilTermImpl(
        override val attachments: TermAttachments = TermAttachments.empty(),
    ) : NilTerm, ListTermImpl() {
        override val type: ListTermType get() = ListTermType(NoTermType)
    }

    private class ListTermVarImpl(
        override val type: ListTermType,
        override val name: String,
        override val resource: String?,
        override val attachments: TermAttachments = TermAttachments.empty(),
    ) : ListTermVar, ListTermImpl()

    private class TermVarImpl(
        override val type: TermType,
        override val name: String,
        override val resource: String?,
        override val attachments: TermAttachments = TermAttachments.empty(),
    ) : TermVar, TermImpl() {

        override fun equals(that: Any?): Boolean {
            if (this === that) return true              // Identity equality
            if (that !is TermVarImpl) return false      // Must be the exact same type (otherwise hash calculations may be different)
            if (this.hash != that.hash) return false    // Hashes must match (but this does not guarantee that the terms are equal)

            // Check that the term and all its subterms are truly equal
            // @formatter:off
            return this.type == that.type
                && this.name == that.name
                && this.resource == that.resource
                && this.attachments == that.attachments
            // @formatter:on
        }

        override val hash: Int = Objects.hash(type, name, resource, attachments)
    }


}