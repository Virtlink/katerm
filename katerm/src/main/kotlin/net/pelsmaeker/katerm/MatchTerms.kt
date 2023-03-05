package net.pelsmaeker.katerm

/**
 * Matches this term and its subterms with the specified pattern.
 *
 * Any term variables in the pattern are returned as part of the match result,
 * with the term they were matched against. If the match fails, this method returns `null`.
 *
 * If the term is matched against itself, all variables will be associated with themselves.
 *
 * Note that the term attachments are not checked by this method.
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
): TermVisitor1<Term, Boolean> {

    override fun visitInt(term: IntTerm, pattern: Term): Boolean = when {
        term === pattern -> associateSelf(term)
        pattern is TermVar -> associate(term, pattern)
        pattern is IntTerm -> true
        else -> false
    }

    override fun visitReal(term: RealTerm, pattern: Term): Boolean = when {
        term === pattern -> associateSelf(term)
        pattern is TermVar -> associate(term, pattern)
        pattern is RealTerm -> true
        else -> false
    }

    override fun visitString(term: StringTerm, pattern: Term): Boolean = when {
        term === pattern -> associateSelf(term)
        pattern is TermVar -> associate(term, pattern)
        pattern is StringTerm -> true
        else -> false
    }

    override fun visitAppl(term: ApplTerm, pattern: Term): Boolean = when {
        term === pattern -> associateSelf(term)
        pattern is TermVar -> associate(term, pattern)
        pattern is ApplTerm -> acceptAll(term.termArgs, pattern.termArgs)
        else -> false
    }

    override fun visitList(term: ListTerm, pattern: Term): Boolean = when {
        term === pattern -> associateSelf(term)
        pattern is TermVar -> associate(term, pattern)
        pattern is ListTerm -> acceptAll(term.elements, pattern.elements)
        else -> false
    }

    override fun visitVar(term: TermVar, pattern: Term): Boolean = when {
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