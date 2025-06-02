package net.pelsmaeker.katerm.terms

/**
 * A constructor application term.
 */
interface ApplTerm : Term {

    /** The constructor name. */
    val termOp: String

    /** The constructor arity. */
    val termArity: Int

    /** The term arguments. */
    val termArgs: List<Term>

}