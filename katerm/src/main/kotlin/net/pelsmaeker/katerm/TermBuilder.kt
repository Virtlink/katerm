package net.pelsmaeker.katerm

/**
 * Builds terms.
 */
interface TermBuilder {
    fun intTerm(value: Int): IntTerm

    fun stringTerm(value: String): StringTerm

    fun blobTerm(value: Any): BlobTerm

    fun applTerm(op: String, vararg args: Term): ApplTerm = applTerm(op, args.toList())
    fun applTerm(op: String, args: List<Term>): ApplTerm = applTerm(ApplTermType(op, args.map { it.type }), args)
    fun applTerm(type: ApplTermType, vararg args: Term): ApplTerm = applTerm(type, args.toList())
    fun applTerm(type: ApplTermType, args: List<Term>): ApplTerm

    fun listTerm(vararg elements: Term): ListTerm = listTerm(elements.toList())
    fun listTerm(elements: List<Term>): ListTerm = listTerm(ListTermType(TermType.getSupertypeOf(elements.map { it.type} )), elements)
    fun listTerm(type: ListTermType, vararg elements: Term): ListTerm = listTerm(elements.toList())
    fun listTerm(type: ListTermType, elements: List<Term>): ListTerm

    fun termVar(type: TermType, name: String, resource: String?): TermVar
    fun listTermVar(type: ListTermType, name: String, resource: String?): ListTermVar
}

class DefaultTermBuilder(
    private val customBuilders: Map<String, (String, List<Term>) -> ApplTerm> = emptyMap(),
): TermBuilder {

    override fun intTerm(value: Int): IntTerm {
        return IntTermImpl(value)
    }

    override fun stringTerm(value: String): StringTerm {
        return StringTermImpl(value)
    }

    override fun blobTerm(value: Any): BlobTerm {
        return BlobTermImpl(value)
    }

    override fun applTerm(type: ApplTermType, args: List<Term>): ApplTerm {
        require((args zip type.paramTypes).all { (te, ty) -> te.isAssignableTo(ty) }) { "Arguments do not match parameter types." }

        val customBuilder = customBuilders[type.hashString]
        return customBuilder?.let { it(type.op, args) } ?: ApplTermImpl(type, args)
    }

    override fun listTerm(type: ListTermType, elements: List<Term>): ListTerm {
        require(elements.all { it.isAssignableTo(type.elementType) }) { "Elements do not match list type." }

        return if (elements.isNotEmpty()) {
            ConsTermImpl(type, elements.first(), listTerm(type, elements.drop(1)))  // TODO: Optimize
        } else {
            NilTermImpl()
        }
    }

    override fun termVar(type: TermType, name: String, resource: String?): TermVar {
        return TermVarImpl(type, name, resource)
    }

    override fun listTermVar(type: ListTermType, name: String, resource: String?): ListTermVar {
        return ListTermVarImpl(type, name, resource)
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
