package net.pelsmaeker.katerm.regex

import net.pelsmaeker.katerm.substitutions.Substitution
import net.pelsmaeker.katerm.terms.Term
import net.pelsmaeker.katerm.terms.TermBuilder
import net.pelsmaeker.katerm.terms.TermBuilderHelper

class TermRegexBuilder<R : Regex<T, Substitution>, T: Term>(
    termBuilder: TermBuilder,
    private val regexBuilder: RegexBuilder<R, T, Substitution>
) : RegexBuilder<R, T, Substitution> by regexBuilder, TermBuilderHelper(termBuilder) {

    /**
     * Creates a regular expression pattern matching the given term.
     *
     * @param term The term to match.
     * @return The built regular expression.
     */
    @Suppress("FunctionName")
    fun T(term: T): R = atom(UnifyingMatcher(term))


}