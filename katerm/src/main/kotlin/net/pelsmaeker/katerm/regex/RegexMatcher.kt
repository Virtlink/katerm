package net.pelsmaeker.katerm.regex

import net.pelsmaeker.katerm.substitutions.Substitution
import net.pelsmaeker.katerm.terms.Term

interface RegexMatcher {

    val substitution: Substitution

    fun match(input: Term): RegexMatcher?

    fun matchAll(inputs: Iterable<Term>): RegexMatcher? {
        var currentMatcher = this
        for (input in inputs) {
            val newMatcher = currentMatcher.match(input) ?: return null
            currentMatcher = newMatcher
        }
        return currentMatcher
    }

    fun isAccepting(): Boolean
    fun isFinal(): Boolean
    fun isEmpty(): Boolean = !isAccepting() && !isFinal()
    fun isNotAccepting(): Boolean = !isAccepting()
    fun isNotFinal(): Boolean = !isFinal()
    fun isNotEmpty(): Boolean = !isEmpty()

}