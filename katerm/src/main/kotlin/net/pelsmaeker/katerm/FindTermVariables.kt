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
): TermVisitor<Unit>, ListTermVisitor<Unit> {

    override fun visitInt(term: IntTerm) {
        // Nothing to do.
    }

    override fun visitString(term: StringTerm) {
        // Nothing to do.
    }

    override fun visitBlob(term: BlobTerm) {
        // Nothing to do.
    }

    override fun visitAppl(term: ApplTerm) {
        // Visit arguments.
        term.termArgs.forEach { it.accept(this) }
    }

    override fun visitList(term: ListTerm) {
        term.accept(this as ListTermVisitor<Unit>)
    }

    override fun visitCons(term: ConsTerm) {
        term.head.accept(this)
        term.tail.accept(this as ListTermVisitor<Unit>)
    }

    override fun visitNil(term: NilTerm) {
        // Nothing to do.
    }

    override fun visitVar(term: TermVar) {
        variables.add(term)
    }

    override fun visitListVar(term: ListTermVar) {
        variables.add(term)
    }

}