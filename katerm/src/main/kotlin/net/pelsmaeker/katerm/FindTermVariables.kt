package net.pelsmaeker.katerm

// TODO: Should we memoize the result?
/** All term variables used in this term and its subterms. */
val Term.variables: Set<TermVar> get() {
    val variables = mutableSetOf<TermVar>()
    this.accept(TermVariableVisitor(variables))
    return variables
}

/**
 * Visits all terms in the tree to find the term variables.
 */
private class TermVariableVisitor(
    /** A mutable set of variables found. */
    private val variables: MutableSet<TermVar>
): TermVisitor<Unit> {

    override fun visitInt(term: IntTerm) {
        // Nothing to do.
    }

    override fun visitReal(term: RealTerm) {
        // Nothing to do.
    }

    override fun visitString(term: StringTerm) {
        // Nothing to do.
    }

    override fun visitAppl(term: ApplTerm) {
        // Visit arguments.
        term.termArgs.forEach { it.accept(this) }
    }

    override fun visitList(term: ListTerm<*>) {
        term.elements.forEach { it.accept(this) }
    }

    override fun visitVar(term: TermVar) {
        variables.add(term)
    }

}