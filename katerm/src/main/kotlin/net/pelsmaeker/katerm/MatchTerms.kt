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
 * @param pattern The pattern to match against.
 * @return The result of the match; or `null` if the match failed.
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

    override fun visitConsList(term: ConsListTerm<Term>, pattern: Term): Boolean = when {
        term === pattern -> associateSelf(term)
        pattern is TermVar -> associate(term, pattern)
        pattern is ConsListTerm<*> -> { accept(term.head, pattern.head); accept(term.tail, pattern.tail) }
        else -> false
    }

    override fun visitNilList(term: NilListTerm, pattern: Term): Boolean = when {
        term === pattern -> associateSelf(term)
        pattern is TermVar -> associate(term, pattern)
        pattern is NilListTerm -> true
        else -> false
    }

    override fun visitConcatList(term: ConcatListTerm<Term>, pattern: Term): Boolean = when {
        term === pattern -> associateSelf(term)
        pattern is TermVar -> associate(term, pattern)
        pattern is ConcatListTerm<*> -> { accept(term.left, pattern.left); accept(term.right, pattern.right) }
        else -> false
    }

    override fun visitSomeOption(term: SomeOptionTerm<Term>, pattern: Term): Boolean = when {
        term === pattern -> associateSelf(term)
        pattern is TermVar -> associate(term, pattern)
        pattern is SomeOptionTerm<*> -> accept(term.element, pattern.element)
        else -> false
    }

    override fun visitNoneOption(term: NoneOptionTerm, pattern: Term): Boolean = when {
        term === pattern -> associateSelf(term)
        pattern is TermVar -> associate(term, pattern)
        pattern is NoneOptionTerm -> true
        else -> false
    }

    override fun visitVar(term: TermVar, pattern: Term): Boolean = when {
        term === pattern -> associateSelf(term)
        pattern is TermVar -> associate(term, pattern)
        else -> false
    }

    private fun associateSelf(term: Term): Boolean {
        associations.putAll(term.termVars.associateWith { it })
        return true
    }

    private fun associate(term: Term, pattern: TermVar): Boolean {
        associations[pattern] = term
        return true
    }

    private fun accept(term: Term?, pattern: Term?): Boolean {
        if (term == null && pattern == null) return true
        if (term == null || pattern == null) return false
        return term.accept(this, pattern)
    }

    private fun acceptAll(terms: List<Term>, patterns: List<Term>): Boolean {
        if (terms.size != patterns.size) return false
        return (terms zip patterns).all { (a, b) -> a.accept(this, b) }
    }

}