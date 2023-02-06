package net.pelsmaeker.katerm

/**
 * Matches this term and its subterms with the specified pattern.
 *
 * Any term variables in the pattern are returned as part of the match result,
 * with the term they were matched against. If the match fails, this method returns `null`.
 *
 * Note that the attachments are not checked by this method.
 *
 * @param pattern the pattern to match against
 * @return the result of the match; or `null` if the match failed
 */
fun Term.match(pattern: Term): MatchResult? {
    val associations = mutableMapOf<TermVar, Term>()
    val matcher = MatcherVisitor(associations)
    return if (accept(matcher, pattern)) MatchResult(associations) else null
}

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
private class MatcherVisitor(
    private val associations: MutableMap<TermVar, Term>
): TermVisitor1<Term, Boolean>, ListTermVisitor1<Term, Boolean> {
    override fun visitInt(term: IntTerm, pattern: Term): Boolean = when {
        term === pattern -> associateSelf(term)
        pattern is TermVar -> associate(term, pattern)
        pattern is IntTerm -> true
        else -> false
    }

    override fun visitString(term: StringTerm, pattern: Term): Boolean = when {
        term === pattern -> associateSelf(term)
        pattern is TermVar -> associate(term, pattern)
        pattern is StringTerm -> true
        else -> false
    }

    override fun visitBlob(term: BlobTerm, pattern: Term): Boolean = when {
        term === pattern -> associateSelf(term)
        pattern is TermVar -> associate(term, pattern)
        pattern is BlobTerm -> true
        else -> false
    }

    override fun visitAppl(term: ApplTerm, pattern: Term): Boolean = when {
        term === pattern -> associateSelf(term)
        pattern is TermVar -> associate(term, pattern)
        pattern is ApplTerm -> acceptAll(term.args, pattern.args)
        else -> false
    }

    override fun visitList(term: ListTerm, pattern: Term): Boolean = when {
        term === pattern -> associateSelf(term)
        pattern is TermVar -> associate(term, pattern)
        pattern is ListTerm -> term.accept(this as ListTermVisitor1<Term, Boolean>, pattern)
        else -> false
    }

    override fun visitVar(term: TermVar, pattern: Term): Boolean = when {
        term === pattern -> associateSelf(term)
        pattern is TermVar -> associate(term, pattern)
        else -> false
    }

    override fun visitCons(term: ConsTerm, pattern: Term): Boolean = when {
        term === pattern -> associateSelf(term)
        pattern is TermVar -> associate(term, pattern)
        pattern is ConsTerm -> term.head.accept(this, pattern.head)
                && term.tail.accept(this as ListTermVisitor1<Term, Boolean>, pattern.tail)
        else -> false
    }

    override fun visitNil(term: NilTerm, pattern: Term): Boolean = when {
        term === pattern -> associateSelf(term)
        pattern is TermVar -> associate(term, pattern)
        pattern is NilTerm -> true
        else -> false
    }

    override fun visitListVar(term: ListTermVar, pattern: Term): Boolean = when {
        term === pattern -> associateSelf(term)
        pattern is TermVar -> associate(term, pattern)
        else -> false
    }

    private fun associateSelf(term: Term): Boolean {
        associations.putAll(term.variables.associateWith { it })
        return true
    }

    private fun associate(term: Term, pattern: TermVar): Boolean {
        associations[pattern] = term
        return true
    }

    private fun acceptAll(terms: List<Term>, patterns: List<Term>): Boolean {
        if (terms.size != patterns.size) return false
        return (terms zip patterns).all { (a, b) -> a.accept(this, b) }
    }

}