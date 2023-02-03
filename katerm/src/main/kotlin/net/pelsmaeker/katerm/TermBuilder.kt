package net.pelsmaeker.katerm

/**
 * Builds terms.
 */
interface TermBuilder {

    /**
     * Creates a copy of the specified term with the specified new attachments.
     *
     * @param term the term to copy
     * @param newAttachments the new attachments of the term
     * @return the copy of the term, but with the new attachments
     */
    fun withAttachments(term: Term, newAttachments: TermAttachments): Term

    /////////
    // Int //
    /////////

    /**
     * Creates a new integer term with the specified value and no attachments.
     *
     * @param value the value of the term
     * @return the created term
     */
    fun createInt(value: Int): IntTerm = createInt(value, TermAttachments.empty())

    /**
     * Creates a new integer term with the specified value and attachments.
     *
     * @param value the value of the term
     * @param attachments the attachments of the term
     * @return the created term
     */
    fun createInt(value: Int, attachments: TermAttachments): IntTerm

    /**
     * Create a copy of the specified integer term with the specified new value
     * and the same attachments.
     *
     * @param newValue the new value of the term
     * @return the copy of the term, but with the new value
     */
    fun replaceInt(term: IntTerm, newValue: Int): IntTerm

    ////////////
    // String //
    ////////////

    /**
     * Creates a new string term with the specified value and no attachments.
     *
     * @param value the value of the term
     * @return the created term
     */
    fun createString(value: String): StringTerm = createString(value, TermAttachments.empty())

    /**
     * Creates a new string term with the specified value and attachments.
     *
     * @param value the value of the term
     * @param attachments the attachments of the term
     * @return the created term
     */
    fun createString(value: String, attachments: TermAttachments): StringTerm

    /**
     * Create a copy of the specified string term with the specified new value
     * and the same attachments.
     *
     * @param newValue the new value of the term
     * @return the copy of the term, but with the new value
     */
    fun replaceString(term: StringTerm, newValue: String): StringTerm

    //////////
    // Blob //
    //////////

    /**
     * Creates a new blob term with the specified value and no attachments.
     *
     * @param value the value of the term
     * @return the created term
     */
    fun createBlob(value: Any): BlobTerm = createBlob(value, TermAttachments.empty())

    /**
     * Creates a new blob term with the specified value and attachments.
     *
     * @param value the value of the term
     * @param attachments the attachments of the term
     * @return the created term
     */
    fun createBlob(value: Any, attachments: TermAttachments): BlobTerm

    /**
     * Create a copy of the specified blob term with the specified new value
     * and the same attachments.
     *
     * @param newValue the new value of the term
     * @return the copy of the term, but with the new value
     */
    fun replaceBlob(term: BlobTerm, newValue: Any): BlobTerm

    //////////
    // Appl //
    //////////

    fun createAppl(op: String, vararg args: Term): ApplTerm = createAppl(op, args.asList())
    fun createAppl(type: ApplTermType, vararg args: Term): ApplTerm = createAppl(type, args.asList())

    fun createAppl(op: String, args: List<Term>): ApplTerm = createAppl(ApplTermType(op, args.map { it.type }), args, TermAttachments.empty())
    fun createAppl(type: ApplTermType, args: List<Term>): ApplTerm = createAppl(type, args, TermAttachments.empty())

    fun createAppl(op: String, args: List<Term>, attachments: TermAttachments): ApplTerm = createAppl(ApplTermType(op, args.map { it.type }), args, attachments)
    fun createAppl(type: ApplTermType, args: List<Term>, attachments: TermAttachments): ApplTerm

    fun replaceAppl(term: ApplTerm, vararg newArgs: Term): ApplTerm = replaceAppl(term, newArgs.asList())
    fun replaceAppl(term: ApplTerm, newArgs: List<Term>): ApplTerm

    //////////
    // List //
    //////////

    fun createList(vararg elements: Term): ListTerm = createList(elements.asList())
    fun createList(type: ListTermType, vararg elements: Term): ListTerm = createList(elements.asList())

    fun createList(elements: List<Term>): ListTerm = createList(ListTermType(TermType.getSupertypeOf(elements.map { it.type} )), elements, TermAttachments.empty())
    fun createList(type: ListTermType, elements: List<Term>): ListTerm = createList(type, elements, TermAttachments.empty())

    fun createList(elements: List<Term>, attachments: TermAttachments): ListTerm = createList(ListTermType(TermType.getSupertypeOf(elements.map { it.type} )), elements, attachments)
    fun createList(type: ListTermType, elements: List<Term>, attachments: TermAttachments): ListTerm

    fun replaceList(term: ListTerm, vararg newElements: Term): ListTerm = replaceList(term, newElements.asList())
    fun replaceList(term: ListTerm, newElements: List<Term>): ListTerm

    /////////
    // Var //
    /////////

    fun createVar(type: TermType, name: String, resource: String? = null): TermVar = createVar(type, name, resource, TermAttachments.empty())
    fun createVar(type: TermType, name: String, resource: String? = null, attachments: TermAttachments): TermVar

    /////////////
    // ListVar //
    /////////////

    fun createListVar(type: ListTermType, name: String, resource: String? = null): ListTermVar = createListVar(type, name, resource, TermAttachments.empty())
    fun createListVar(type: ListTermType, name: String, resource: String? = null, attachments: TermAttachments): ListTermVar

}

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

    private abstract class TermImpl: Term

    private class ApplTermImpl(
        override val type: ApplTermType,
        override val args: List<Term>,
        override val attachments: TermAttachments = TermAttachments.empty()
    ) : ApplTerm, TermImpl() {

        init {
            require((args zip type.paramTypes).all { (te, ty) -> te.isAssignableTo(ty) }) { "Arguments do not match parameter types." }
        }

        override fun equals(other: Any?): Boolean {
            return super.equals(other)
        }

        override fun hashCode(): Int {
            return super.hashCode()
        }

        override fun toString(): String {
            return "${type.op}(${args.joinToString(", ")})"
        }
    }

    private abstract class ValueTermImpl<T>: ValueTerm<T>

    private class IntTermImpl(
        override val value: Int,
        override val attachments: TermAttachments = TermAttachments.empty()
    ) : IntTerm, ValueTermImpl<Int>() {
        override val type: IntTermType get() = IntTermType
    }

    private class StringTermImpl(
        override val value: String,
        override val attachments: TermAttachments = TermAttachments.empty()
    ) : StringTerm, ValueTermImpl<String>() {
        override val type: StringTermType get() = StringTermType
    }

    private class BlobTermImpl(
        override val value: Any,
        override val attachments: TermAttachments = TermAttachments.empty()
    ) : BlobTerm, ValueTermImpl<Any>() {
        override val type: BlobTermType get() = BlobTermType
    }

    private abstract class ListTermImpl: ListTerm

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
    ) : TermVar, TermImpl()


}
