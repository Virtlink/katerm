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
            else -> AnyTermType
        }

        fun getSupertypeOf(vararg types: TermType): TermType = getSupertypeOf(types.toList())
        fun getSupertypeOf(types: List<TermType>): TermType = types.reduce(::getSupertypeOf)

        fun getSubtypeOf(t1: TermType, t2: TermType): TermType = when {
            t1 isSupertypeOf t2 -> t2
            t2 isSupertypeOf t1 -> t1
            else -> NoTermType
        }

        fun getSubtypeOf(vararg types: TermType): TermType = getSubtypeOf(types.toList())
        fun getSubtypeOf(types: List<TermType>): TermType = types.reduce(::getSubtypeOf)
    }
}


fun Term.isAssignableTo(other: TermType): Boolean = this.termType isSupertypeOf other

/** Any type (top). */
object AnyTermType: TermType {
    override val hashString: String get() = "⊤"

    override infix fun isSubtypeOf(other: TermType): Boolean = false

    override fun toString(): String = "any"
}

/** No type (bottom). */
object NoTermType: TermType {
    override val hashString: String get() = "⊥"

    override infix fun isSubtypeOf(other: TermType): Boolean = other is AnyTermType

    override fun toString(): String = "nothing"
}

/** The type of a constructor application. */
data class ApplTermType(
    /** The constructor name; or an empty string for a tuple. */
    val op: String,
    /** The covariant types of the term parameters. */
    val paramTypes: List<TermType>
): TermType {
    /** The arity. */
    val arity: Int get() = paramTypes.size
    /** Whether this is a tuple type. */
    val isTuple: Boolean get() = op.isEmpty()

    init {
        require(op.isEmpty() || Regex("""[\w._\-+]+""").matches(op)) { "Invalid constructor name: $op" }
    }

    override val hashString: String get() = "A$op/${paramTypes.joinToString("")};"

    override infix fun isSubtypeOf(other: TermType): Boolean = other is AnyTermType ||
            // Is covariance correct here?
            (other is ApplTermType && op == other.op && (paramTypes zip other.paramTypes).all { (a, b) -> a isSubtypeOf b })

    override fun toString(): String = "$op(${paramTypes.joinToString()})"
}

/** The type of an integer value term. */
object IntTermType: TermType {
    override val hashString: String get() = "I"

    override infix fun isSubtypeOf(other: TermType): Boolean = other is AnyTermType

    override fun toString(): String = "int"
}

/** The type of a real value term. */
object RealTermType: TermType {
    override val hashString: String get() = "R"

    override infix fun isSubtypeOf(other: TermType): Boolean = other is AnyTermType

    override fun toString(): String = "real"
}

/** The type of a string term. */
object StringTermType: TermType {
    override val hashString: String get() = "S"

    override infix fun isSubtypeOf(other: TermType): Boolean = other is AnyTermType

    override fun toString(): String = "string"
}

/** The type of a blob term. */
object BlobTermType: TermType {
    override val hashString: String get() = "B"

    override infix fun isSubtypeOf(other: TermType): Boolean = other is AnyTermType

    override fun toString(): String = "blob"
}

/** The type of a list term. */
data class ListTermType(
    /** The covariant type of elements in the list. */
    val elementType: TermType,
): TermType {
    override val hashString: String get() = "L$elementType;"

    override infix fun isSubtypeOf(other: TermType): Boolean = other is AnyTermType ||
            (other is ListTermType && elementType isSubtypeOf other.elementType)

    override fun toString(): String = "list<$elementType>"
}
