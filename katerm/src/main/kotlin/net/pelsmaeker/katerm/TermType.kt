package net.pelsmaeker.katerm

/** The type of a term. */
sealed interface TermType {
    /** A unique hash string for this type. */
    val hashString: String

    infix fun isSupertypeOf(other: TermType): Boolean = other isSubtypeOf this
    infix fun isSubtypeOf(other: TermType): Boolean

    companion object {

        fun getSupertypeOf(t1: TermType, t2: TermType): TermType = when {
            t1 isSubtypeOf t2 -> t2
            t2 isSubtypeOf t1 -> t1
            else -> AnyType
        }

        fun getSupertypeOf(vararg types: TermType): TermType = getSupertypeOf(types.toList())
        fun getSupertypeOf(types: List<TermType>): TermType = types.reduce(::getSupertypeOf)

        fun getSubtypeOf(t1: TermType, t2: TermType): TermType = when {
            t1 isSupertypeOf t2 -> t2
            t2 isSupertypeOf t1 -> t1
            else -> NoType
        }

        fun getSubtypeOf(vararg types: TermType): TermType = getSubtypeOf(types.toList())
        fun getSubtypeOf(types: List<TermType>): TermType = types.reduce(::getSubtypeOf)
    }
}


fun Term.isAssignableTo(other: TermType): Boolean = this.type isSupertypeOf other

/** Any type (top). */
object AnyType: TermType {
    override val hashString: String get() = "⊤"

    override infix fun isSubtypeOf(other: TermType): Boolean = false

    override fun toString(): String = "any"
}

/** No type (bottom). */
object NoType: TermType {
    override val hashString: String get() = "⊥"

    override infix fun isSubtypeOf(other: TermType): Boolean = other is AnyType

    override fun toString(): String = "nothing"
}

/** The type of a constructor application. */
data class ApplType(
    /** The constructor name. */
    val op: String,
    /** The covariant types of the term parameters. */
    val parameterTypes: List<TermType>
): TermType {
    /** The arity. */
    val arity: Int get() = parameterTypes.size

    override val hashString: String get() = "A$op/${parameterTypes.joinToString("")};"

    override infix fun isSubtypeOf(other: TermType): Boolean = other is AnyType ||
            // Is covariance correct here?
            (other is ApplType && op == other.op && (parameterTypes zip other.parameterTypes).all { (a, b) -> a isSubtypeOf b })

    override fun toString(): String = "$op(${parameterTypes.joinToString()})"
}

/** The type of an integer term. */
object IntType: TermType {
    override val hashString: String get() = "I"

    override infix fun isSubtypeOf(other: TermType): Boolean = other is AnyType

    override fun toString(): String = "int"
}

/** The type of a string term. */
object StringType: TermType {
    override val hashString: String get() = "S"

    override infix fun isSubtypeOf(other: TermType): Boolean = other is AnyType

    override fun toString(): String = "string"
}

/** The type of a blob term. */
object BlobType: TermType {
    override val hashString: String get() = "B"

    override infix fun isSubtypeOf(other: TermType): Boolean = other is AnyType

    override fun toString(): String = "blob"
}

/** The type of a list term. */
data class ListType(
    /** The covariant type of elements in the list. */
    val elementType: TermType,
): TermType {
    override val hashString: String get() = "L$elementType;"

    override infix fun isSubtypeOf(other: TermType): Boolean = other is AnyType ||
            (other is ListType && elementType isSubtypeOf other.elementType)

    override fun toString(): String = "list<$elementType>"
}
