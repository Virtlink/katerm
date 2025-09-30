package net.pelsmaeker.katerm.regex

import net.pelsmaeker.katerm.regex.RegexNfa.RegexMatcherImpl
import net.pelsmaeker.katerm.substitutions.Substitution
import net.pelsmaeker.katerm.terms.Term

/**
 * A regular expression.
 *
 * @param T The type of input tokens to match against.
 * @param M The type of metadata associated with each state.
 */
interface Regex<T, M> {

    /**
     * Builds a new regex matcher for this regular expression.
     *
     * @return A new regex matcher for this regular expression.
     */
    fun buildMatcher(initialMetadata: M): RegexMatcher<T, M>

}

