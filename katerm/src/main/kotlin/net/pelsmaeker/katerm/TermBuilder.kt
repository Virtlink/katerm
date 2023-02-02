package net.pelsmaeker.katerm

/**
 * Builds terms.
 */
interface TermBuilder {
    fun intTerm(value: Int): IntTerm

    fun stringTerm(value: String): StringTerm

    fun blobTerm(value: Any): BlobTerm

    fun applTerm(op: String, vararg args: Term): ApplTerm = applTerm(op, args.toList())
    fun applTerm(op: String, args: List<Term>): ApplTerm = applTerm(ApplType(op, args.map { it.type }), args)
    fun applTerm(type: ApplType, vararg args: Term): ApplTerm = applTerm(type, args.toList())
    fun applTerm(type: ApplType, args: List<Term>): ApplTerm

    fun listTerm(vararg elements: Term): ListTerm = listTerm(elements.toList())
    fun listTerm(elements: List<Term>): ListTerm = listTerm(ListType(TermType.getSupertypeOf(elements.map { it.type} )), elements)
    fun listTerm(type: ListType, vararg elements: Term): ListTerm = listTerm(elements.toList())
    fun listTerm(type: ListType, elements: List<Term>): ListTerm

    fun termVar(name: String, resource: String?): TermVar
}

class DefaultTermBuilder: TermBuilder {

    private val customBuilders: Map<String, (String, List<Term>) -> ApplTerm> = emptyMap()

    override fun intTerm(value: Int): IntTerm {
        TODO("Not yet implemented")
    }

    override fun stringTerm(value: String): StringTerm {
        TODO("Not yet implemented")
    }

    override fun blobTerm(value: Any): BlobTerm {
        TODO("Not yet implemented")
    }

    override fun applTerm(type: ApplType, args: List<Term>): ApplTerm {
        require((args zip type.parameterTypes).all { (te, ty) -> te.isAssignableTo(ty) }) { "Arguments do not match parameter types." }
        TODO("Not yet implemented")
    }

    override fun listTerm(type: ListType, elements: List<Term>): ListTerm {
        require(elements.all { it.isAssignableTo(type.elementType) }) { "Elements do not match list type." }
        TODO("Not yet implemented")
    }

    override fun termVar(name: String, resource: String?): TermVar {
        TODO("Not yet implemented")
    }

}
